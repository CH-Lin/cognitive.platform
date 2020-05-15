package prj.cognitive.web;

import prj.cognitive.content.wrapper.PdfWrapper;
import prj.cognitive.func.Processor;
import prj.cognitive.func.content.comparison.DiffRequest;
import prj.cognitive.func.content.comparison.patch.DiffResult;
import prj.cognitive.func.content.elements.Node;
import prj.cognitive.func.content.result.DiffListRedirector;
import prj.cognitive.func.content.result.PdfDiffMaskRedirector;
import prj.cognitive.func.content.result.ResultRedirector;
import prj.cognitive.ocr.AbbyyOcrEngine;
import prj.cognitive.ocr.AbbyyOcrSettings;
import prj.cognitive.ocr.OcrDocument;
import prj.cognitive.ocr.OcrSystem;
import prj.cognitive.pdf.PdfAnnotation;
import prj.cognitive.pdf.PdfDocument;
import prj.cognitive.pdf.PdfSplitter;
import prj.cognitive.pdf.elements.Document;
import prj.cognitive.pdf.elements.Page;
import prj.cognitive.processor.DocumentConfiguration;
import prj.cognitive.processor.DocumentProcessor;
import prj.cognitive.utils.Config;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class WebServer {
    private static final int PORT = Config.get(
            WebServer.class, "PORT", 11051
    );

    private static final String IP_ADDR = Config.get(
            WebServer.class, "IP_ADDR", "0.0.0.0"
    );

    private static final int THREADS = Config.get(
            WebServer.class, "THREADS", 8
    );

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

    public WebServer() {
        initialise(IP_ADDR, PORT, THREADS);
    }

    public WebServer(String host, int port, int threads) {
        initialise(host, port, threads);
    }

    public void addOCRRoute() {
        post("/ocr/:engine", (req, res) -> {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));


            String language = req.queryParams("language");
            AbbyyOcrSettings ocrSettings = new AbbyyOcrSettings();

            if (req.queryParams().contains("ocrSettings")) {
                ocrSettings = gson.fromJson(req.queryParams("ocrSettings"), AbbyyOcrSettings.class);
            }

            try (InputStream rawInput = req.raw().getPart("file").getInputStream()) {
                try (PdfDocument input = new PdfDocument(rawInput)) {
                    try (PdfDocument output = OcrSystem.ocr(new OcrDocument(
                            input, language, req.params(":engine"), ocrSettings
                    )).orElse(null)) {
                        if (output == null) {
                            return 500;
                        }

                        if (req.queryParams().contains("text")) {
                            int depth = req.queryParams().contains("depth") ? Integer.parseInt(req.queryParams("depth")) : -1;
                            Document document = output.getStructuredDocument();
                            res.type("application/json");

                            if (depth > -1) {
                                document.limitDepth(depth);
                            }

                            return gson.toJson(document);
                        } else {
                            res.type("application/pdf");
                            output.outputPDF(res.raw().getOutputStream()).close();
                            return res.raw();
                        }
                    }
                }
            }
        });
    }

    public void addPDFRoutes() {
        post("/pdf/image", (req, res) -> {
            // TODO Config-urise this and document API endpoints
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            res.type("image/jpeg");

            try (InputStream rawInput = req.raw().getPart("file").getInputStream()) {
                try (PdfDocument input = new PdfDocument(rawInput)) {
                    BufferedImage image = input.renderPage(
                            Integer.parseInt(req.queryParams("page"))
                    );
                    ImageIO.write(image, "jpg", res.raw().getOutputStream());
                    res.raw().getOutputStream().close();
                    return res.raw();
                }
            }
        });

        post("/pdf/text", (req, res) -> {
            res.type("application/json");
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            int pageNumber = req.queryParams().contains("page") ? Integer.parseInt(req.queryParams("page")) : -1;
            int limitDepth = req.queryParams().contains("depth") ? Integer.parseInt(req.queryParams("depth")) : -1;

            try (InputStream rawInput = req.raw().getPart("file").getInputStream()) {
                try (PdfDocument input = new PdfDocument(rawInput)) {
                    Document document = input.getStructuredDocument();

                    if (pageNumber > -1) {
                        document.setChildren(
                                document.getChildren().stream().filter(
                                        (page) -> ((Page) page).getPageNumber() == pageNumber
                                ).collect(Collectors.toList())
                        );
                    }

                    if (limitDepth > -1) {
                        document.limitDepth(limitDepth);
                    }

                    return document;
                }
            }
        }, gson::toJson);

        post("/pdf/annotate", (req, res) -> {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            Type annotationsType = new TypeToken<List<PdfAnnotation>>() {
            }.getType();
            List<PdfAnnotation> annotations = gson.fromJson(req.queryParams("json"), annotationsType);

            res.type("application/pdf");

            try (InputStream rawInput = req.raw().getPart("file").getInputStream()) {
                try (PdfDocument input = new PdfDocument(rawInput)) {
                    for (PdfAnnotation annotation : annotations) {
                        System.out.println(annotation);
                        input.annotate(annotation);
                    }

                    input.outputPDF(res.raw().getOutputStream()).close();
                    return res.raw();
                }
            }
        });

        post("/pdf/process", (req, res) -> {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            DocumentConfiguration dc = gson.fromJson(req.queryParams("json"), DocumentConfiguration.class);

            if (dc == null) {
                try (InputStream rawJson = req.raw().getPart("json").getInputStream()) {
                    dc = gson.fromJson(CharStreams.toString(
                            new InputStreamReader(rawJson, Charsets.UTF_8)
                    ), DocumentConfiguration.class);
                }
            }

            res.type("application/json");

            try (InputStream rawInput = req.raw().getPart("file").getInputStream()) {
                try (PdfDocument input = new PdfDocument(rawInput)) {
                    DocumentProcessor dp = new DocumentProcessor(input, dc);
                    dp.ocr();

                    String group = dp.classifyDocument().orElse(null);

                    if (group == null) {
                        return "no group";
                    }
                    System.out.println(group);
                    System.out.println(dp.getDocument(group).getScore());
                    dp.getDocument(group).process();
                    System.out.println(dp.getDocument(group).getResults());

                    return dp.getDocument(group).getResults();
                }
            }
        }, gson::toJson);

        post("/pdf/search", (req, res) -> {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            res.type("application/json");

            try (InputStream rawInput = req.raw().getPart("file").getInputStream()) {
                try (PdfDocument input = new PdfDocument(rawInput)) {
                    PdfDocument searchDoc = input;
                    if (req.queryParams().contains("language")) {
                        searchDoc = OcrSystem.ocr(new OcrDocument(
                                input, req.queryParams("language"), AbbyyOcrEngine.CODE
                        )).orElse(null);
                    }

                    return searchDoc.getStructuredDocument().search(req.queryParams("query"));
                }
            }
        }, gson::toJson);

		post("/pdf/info-extraction", (req, res) -> {
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			res.type("application/pdf");

			try (InputStream rawInput = req.raw().getPart("file").getInputStream()) {
				try (PdfDocument input = new PdfDocument(rawInput)) {

					Processor.get(Processor.algCode.INFO_EXTRACTOR).run(res.raw().getOutputStream(), input)
							.close();

					return res.raw();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			return null;
		}, gson::toJson);

		post("/pdf/info-compare", (req, res) -> {
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			res.type("application/pdf");
			
			DiffRequest config;
			try {
				config = gson.fromJson(req.queryParams("config"), DiffRequest.class);
			} catch (JsonSyntaxException jse) {
				config = new DiffRequest();
			}
			
			if (config == null) {
				config = new DiffRequest();
			}

			try (InputStream rawInput1 = req.raw().getPart("source").getInputStream();
					InputStream rawInput2 = req.raw().getPart("target").getInputStream()) {
				try (PdfWrapper input1 = new PdfWrapper(rawInput1); PdfWrapper input2 = new PdfWrapper(rawInput2)) {

					ResultRedirector<DiffResult<Node>> redirector = null;
					switch (config.getAction()) {
					case "annotate":
						redirector = new PdfDiffMaskRedirector(res.raw().getOutputStream(), input2);
						break;
					case "list":
						redirector = new DiffListRedirector(res.raw().getOutputStream());
						break;
					default:
						redirector = new PdfDiffMaskRedirector(res.raw().getOutputStream(), input2);
						break;
					}
					res.type(redirector.getResponseContentType());
					
					Processor.get(Processor.algCode.INFO_COMPARER).run(redirector, input1, input2);
					res.raw().getOutputStream().close();

					return res.raw();
				}
			} catch (IOException ioe) {
				// ioe.printStackTrace();
			} catch (IllegalArgumentException iae) {
				iae.printStackTrace();
			}
			return null;
		}, gson::toJson);

        post("/pdf/split", (req, res) -> {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            res.type("application/pdf");

            try (InputStream rawInput = req.raw().getPart("file").getInputStream()) {
                try (PdfDocument input = new PdfDocument(rawInput)) {
                    PdfSplitter.split(input, req.queryParams("range"), res.raw().getOutputStream());
                    return res.raw();
                }
            }
        });
    }

    private void initialise(String host, int port, int threads) {
        ipAddress(host);
        port(port);
        threadPool(threads);
        addOCRRoute();
        addPDFRoutes();
    }

    public class WebServerError {
        public int code;
        public String message;

        public WebServerError(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
