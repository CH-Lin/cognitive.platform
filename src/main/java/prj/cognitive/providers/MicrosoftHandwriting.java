package prj.cognitive.providers;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import prj.cognitive.pdf.elements.Text;
import prj.cognitive.pdf.elements.attrs.PositionalArea;
import prj.cognitive.processor.DocumentConfiguration;
import prj.cognitive.utils.Config;
import prj.cognitive.utils.Images;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MicrosoftHandwriting extends AbstractProvider {
    public static final String NAME = Config.get(
            MicrosoftHandwriting.class, "NAME", "microsoft"
    );

    private static final String API_KEY = Config.get(
            MicrosoftHandwriting.class, "API_KEY", "340c53f60ed24e7790b29d9f6fe4e962"
    );

    private static final String BASE_URL = Config.get(
            MicrosoftHandwriting.class, "BASE_URL", "https://australiaeast.api.cognitive.microsoft.com/vision/v2.0/read/core/asyncBatchAnalyze"
    );

    public static final int MINIMUM_DIMENSION = Config.get(
            MicrosoftHandwriting.class, "MINIMUM_DIMENSION", 50
    );

    public String startProcessing(BufferedImage image, List<Text> documentText, DocumentConfiguration.Field configuration) {
        // If successful, body is empty but on error, JSON is returned explaining why
        HttpResponse<JsonNode> response = Unirest.post(BASE_URL)
                .header("Ocp-Apim-Subscription-Key", API_KEY)
                .header("Content-Type", "application/octet-stream")
                .queryString("mode", "Handwritten")
                .body(Images.toByteArray(
                        Images.withMinimumSize(image, MINIMUM_DIMENSION, MINIMUM_DIMENSION)
                ))
                .asJson();

        if (!response.getHeaders().containsKey("Operation-Location")) {
            // TODO: Add proper logging but this at least allows debugging
            System.err.println(response.getBody().toString());
            return null;
        }

        return response.getHeaders().getFirst("Operation-Location");
    }

    @Override
    public Optional<ProviderResult> getValue(String processingId) {
        MicrosoftStatusResponse statusResponse = null;
        int backoff = 1;

        if (processingId == null) {
            return Optional.empty();
        }

        while (statusResponse == null
                || MicrosoftStatusResponse.STATUS_NOT_STARTED.equalsIgnoreCase(statusResponse.getStatus())
                || MicrosoftStatusResponse.STATUS_RUNNING.equalsIgnoreCase(statusResponse.getStatus())) {
            try {
                TimeUnit.SECONDS.sleep(backoff);
            } catch (InterruptedException ignore) {

            }

            HttpResponse<MicrosoftStatusResponse> fullResponse = Unirest.get(processingId)
                    .header("Ocp-Apim-Subscription-Key", API_KEY)
                    .asObject(MicrosoftStatusResponse.class);

            if (fullResponse.getStatus() != 200) {
                System.out.println(fullResponse.getStatus());
                System.out.println(fullResponse.getBody());

                if (backoff == 12) {
                    return Optional.empty();
                }
            } else {
                statusResponse = fullResponse.getBody();
            }

            backoff = Math.min(12, backoff * 2);
        }

        return statusResponse.toProviderResult();
    }

    public class MicrosoftStatusResponse {
        public static final String STATUS_FAILED = "Failed";
        public static final String STATUS_NOT_STARTED = "Not started";
        public static final String STATUS_RUNNING = "Running";
        public static final String STATUS_SUCCESS = "Succeeded";
        @Expose
        private String status;
        @Expose
        private List<RecognitionResult> recognitionResults;

        public MicrosoftStatusResponse(String status, List<RecognitionResult> recognitionResults) {
            this.status = status;
            this.recognitionResults = recognitionResults;
        }

        public String getStatus() {
            return status;
        }

        public List<RecognitionResult> getRecognitionResults() {
            return recognitionResults;
        }

        public Optional<ProviderResult> toProviderResult() {
            String text = getRecognitionResults().stream()
                    .flatMap(page -> page.getLines().stream())
                    .map(RecognitionResult.RecognisedLines::getText)
                    .collect(Collectors.joining("\n"));

            double confidence = getRecognitionResults().stream()
                    .flatMap(page -> page.getLines().stream())
                    .mapToDouble(RecognitionResult.RecognisedLines::getConfidenceValue)
                    .average().orElse(0.0);

            if (text.trim().length() == 0 || confidence == 0) {
                return Optional.empty();
            }

            return Optional.of(new ProviderResult(
                    confidence, text
            ));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MicrosoftStatusResponse)) return false;
            MicrosoftStatusResponse that = (MicrosoftStatusResponse) o;
            return Objects.equal(status, that.status) &&
                    Objects.equal(recognitionResults, that.recognitionResults);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(status, recognitionResults);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("status", status)
                    .add("recognitionResults", recognitionResults)
                    .toString();
        }

        public class RecognitionResult {
            @Expose
            List<RecognisedLines> lines;

            public RecognitionResult(List<RecognisedLines> lines) {
                this.lines = lines;
            }

            public List<RecognisedLines> getLines() {
                return lines;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof RecognitionResult)) return false;
                RecognitionResult that = (RecognitionResult) o;
                return Objects.equal(lines, that.lines);
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(lines);
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .add("lines", lines)
                        .toString();
            }

            public class RecognisedLines {
                @Expose
                private List<Integer> boundingBox;

                @Expose
                private String text;

                @Expose
                private String confidence = "High";

                @Expose
                private List<RecognisedLines> words;

                public RecognisedLines(List<Integer> boundingBox, String text, String confidence, List<RecognisedLines> words) {
                    this.boundingBox = boundingBox;
                    this.text = text;
                    this.confidence = confidence;
                    this.words = words;
                }

                public List<Integer> getBoundingBox() {
                    return boundingBox;
                }

                public PositionalArea getRegion() {
                    return new PositionalArea(
                            boundingBox.get(0),
                            boundingBox.get(1),
                            null,
                            boundingBox.get(4),
                            boundingBox.get(5)
                    );
                }

                public String getText() {
                    return text;
                }

                public String getConfidence() {
                    return confidence;
                }

                public Double getConfidenceValue() {
                    if ("HIGH".equalsIgnoreCase(confidence)) {
                        return 1.0;
                    } else {
                        return 0.5;
                    }
                }

                public List<RecognisedLines> getWords() {
                    return words;
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (!(o instanceof RecognisedLines)) return false;
                    RecognisedLines that = (RecognisedLines) o;
                    return Objects.equal(boundingBox, that.boundingBox) &&
                            Objects.equal(text, that.text) &&
                            Objects.equal(confidence, that.confidence) &&
                            Objects.equal(words, that.words);
                }

                @Override
                public int hashCode() {
                    return Objects.hashCode(boundingBox, text, confidence, words);
                }

                @Override
                public String toString() {
                    return MoreObjects.toStringHelper(this)
                            .add("boundingBox", boundingBox)
                            .add("text", text)
                            .add("confidence", confidence)
                            .add("words", words)
                            .toString();
                }
            }
        }
    }
}
