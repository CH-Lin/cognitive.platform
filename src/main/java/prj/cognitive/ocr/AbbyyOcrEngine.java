package prj.cognitive.ocr;

import com.abbyy.FREngine.*;
import com.google.common.io.ByteStreams;

import prj.cognitive.pdf.PdfDocument;
import prj.cognitive.utils.Config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

public class AbbyyOcrEngine extends OcrEngine implements Closeable {
    public static final String CODE = Config.get(
            AbbyyOcrEngine.class, "CODE", "afr"
    );

    private static final String DLL_PATH_64BIT = Config.get(
            AbbyyOcrEngine.class, "DLL_PATH_64BIT",
            "C:\\OCR Plugins\\ABBYY SDK\\12\\FineReader Engine\\Bin64"
    );

    private static final String DLL_PATH_32BIT = Config.get(
            AbbyyOcrEngine.class, "DLL_PATH_32BIT",
            "C:\\OCR Plugins\\ABBYY SDK\\12\\FineReader Engine\\Bin"
    );

    private static final String PROJECT_ID = Config.get(
            AbbyyOcrEngine.class, "PROJECT_ID", "FhRNwyLugtpncBXcawMd"
    );

    private static final String PROFILE = Config.get(
            AbbyyOcrEngine.class, "PROFILE", "DocumentConversion_Accuracy"
    );

    private static final String PROCESSING_FILE_NAME = Config.get(
            AbbyyOcrEngine.class, "PROCESSING_FILE_NAME", "input.pdf"
    );

    private static final String LICENSE_PATH = Config.get(
            AbbyyOcrEngine.class, "LICENSE_PATH", ""
    );

    private static final String LICENSE_PASSWORD = Config.get(
            AbbyyOcrEngine.class, "LICENSE_PASSWORD", ""
    );

    private IEngine engine;

    private void load() throws Exception {
        engine = Engine.InitializeEngine(
                Config.is64bit() ? DLL_PATH_64BIT : DLL_PATH_32BIT,
                PROJECT_ID,
                LICENSE_PATH,
                LICENSE_PASSWORD,
                Config.EMPTY_STRING,
                Config.EMPTY_STRING,
                false
        );

        engine.LoadPredefinedProfile(PROFILE);
    }

    public void close() {
        if (engine != null) {
            try {
                engine = null;
                Engine.DeinitializeEngine();
            } catch (Exception ignore) {

            }
        }
    }

    private RotationTypeEnum reverseRotation(RotationTypeEnum originalRotation) {
        if (RotationTypeEnum.RT_Clockwise.equals(originalRotation)) {
            return RotationTypeEnum.RT_Counterclockwise;
        }

        if (RotationTypeEnum.RT_Counterclockwise.equals(originalRotation)) {
            return RotationTypeEnum.RT_Clockwise;
        }

        if (RotationTypeEnum.RT_Upsidedown.equals(originalRotation)) {
            return RotationTypeEnum.RT_Upsidedown;
        }

        return RotationTypeEnum.RT_NoRotation;
    }

    private void removeObjects(IFRPage page, ObjectsTypeEnum objectType, AbbyyOcrSettings settings) {
        if (settings.getRemoveGreenColor()) {
            page.getImageDocument().RemoveColorObjects(
                    engine.CreateRegion(),
                    ObjectsColorEnum.OC_Green,
                    objectType
            );
        }

        if (settings.getRemoveBlueColor()) {
            page.getImageDocument().RemoveColorObjects(
                    engine.CreateRegion(),
                    ObjectsColorEnum.OC_Blue,
                    objectType
            );
        }

        if (settings.getRemoveRedColor()) {
            page.getImageDocument().RemoveColorObjects(
                    engine.CreateRegion(),
                    ObjectsColorEnum.OC_Red,
                    objectType
            );
        }

        if (settings.getRemoveYellowColor()) {
            page.getImageDocument().RemoveColorObjects(
                    engine.CreateRegion(),
                    ObjectsColorEnum.OC_Yellow,
                    objectType
            );
        }
    }

    private void processPage(IFRPage page, IDocumentProcessingParams params, AbbyyOcrSettings abbyyOcrSettings) {
        RotationTypeEnum detectedRotation = RotationTypeEnum.RT_UnknownRotation;

        if (abbyyOcrSettings.getCorrectOrientation()) {
            ITextOrientation textOrientation = page.DetectOrientation(
                    null,
                    params.getPageProcessingParams().getObjectsExtractionParams(),
                    params.getPageProcessingParams().getRecognizerParams()
            );

            if (textOrientation != null) {
                detectedRotation = textOrientation.getRotationType();
            }
        }

        if (abbyyOcrSettings.getCropImage()) {
            page.getImageDocument().CropImage();
        }

        if (abbyyOcrSettings.getEnhanceLocalContrast()) {
            page.getImageDocument().EnhanceLocalContrast();
        }

        if (!RotationTypeEnum.RT_UnknownRotation.equals(detectedRotation)) {
            RotationTypeEnum applyRotation = reverseRotation(detectedRotation);
            page.getImageDocument().Transform(applyRotation, false, abbyyOcrSettings.getInvertImage());
        } else if (abbyyOcrSettings.getInvertImage()) {
            page.getImageDocument().Transform(RotationTypeEnum.RT_NoRotation, false, true);
        }

        if (abbyyOcrSettings.getCorrectDistortions()) {
            page.CorrectGeometricalDistortions(params.getPageProcessingParams().getObjectsExtractionParams());
        }

        if (abbyyOcrSettings.getDeskewImage()) {
            page.getImageDocument().CorrectSkew(
                    CorrectSkewModeEnum.CSM_CorrectSkewByHorizontalText.getValue()
            );
        }

        if (abbyyOcrSettings.getRemoveGarbage()) {
            page.getImageDocument().RemoveGarbage(null, -1);
        }

        if (abbyyOcrSettings.getRemoveCorrelatedNoise()) {
            page.getImageDocument().RemoveNoise(
                    NoiseModelEnum.NM_CorrelatedNoise,
                    abbyyOcrSettings.getHasLargeCharacters()
            );
        }

        if (abbyyOcrSettings.getRemoveWhiteNoise()) {
            page.getImageDocument().RemoveNoise(
                    NoiseModelEnum.NM_WhiteNoise,
                    abbyyOcrSettings.getHasLargeCharacters()
            );
        }

        if (abbyyOcrSettings.getRemoveMotionBlur()) {
            page.getImageDocument().RemoveMotionBlur(engine.CreateRegion());
        }

        if (abbyyOcrSettings.getRemoveBackgroundObjects()) {
            removeObjects(page, ObjectsTypeEnum.OT_Background, abbyyOcrSettings);
        }

        if (abbyyOcrSettings.getRemoveFullObjects()) {
            removeObjects(page, ObjectsTypeEnum.OT_Full, abbyyOcrSettings);
        }

        if (abbyyOcrSettings.getRemoveStampObjects()) {
            removeObjects(page, ObjectsTypeEnum.OT_Stamp, abbyyOcrSettings);
        }

        page.Flush(true);
    }

    @Override
    public PdfDocument process(OcrDocument document) throws Exception {
        if (engine == null) {
            load();
        }

        IFRDocument frDocument = engine.CreateFRDocument();
        IPDFExportParams pdfParams = engine.CreatePDFExportParams();
        pdfParams.setScenario(PDFExportScenarioEnum.PES_Balanced);
        PdfFileWriter writer = new PdfFileWriter();
        byte[] data = ByteStreams.toByteArray(document.getDocument().getDocumentData().openStream());
        IPrepareImageMode prepareImageMode = engine.CreatePrepareImageMode();
        IDocumentProcessingParams params = engine.CreateDocumentProcessingParams();

        // Set the language
        params.getPageProcessingParams().getRecognizerParams().setTextLanguage(
                engine.CreateLanguageDatabase().CreateCompoundTextLanguage(
                        document.getLanguage()
                )
        );

        if (document.getAbbyyOcrSettings().getTextExtractMode()) {
            params.getPageProcessingParams().getPageAnalysisParams().setEnableTextExtractionMode(true);
        }

        if (document.getAbbyyOcrSettings().getAggressiveTextExtraction()) {
            params.getPageProcessingParams().getObjectsExtractionParams().setEnableAggressiveTextExtraction(true);
        }

        // Fix the document resolution
        if (document.getAbbyyOcrSettings().getCorrectResolution()) {
            if (document.getAbbyyOcrSettings().getOverrideResolution().equals(0)) {
                prepareImageMode.setAutoOverwriteResolution(true);
            } else {
                prepareImageMode.setOverwriteResolution(true);
                prepareImageMode.setXResolutionToOverwrite(document.getAbbyyOcrSettings().getOverrideResolution());
                prepareImageMode.setYResolutionToOverwrite(document.getAbbyyOcrSettings().getOverrideResolution());
            }
        }

        frDocument.AddImageFileFromMemory(data,
                null, prepareImageMode, null,
                PROCESSING_FILE_NAME
        );

        for (int pageNumber = 0; pageNumber < frDocument.getPages().getCount(); pageNumber++) {
            processPage(
                    frDocument.getPages().getElement(pageNumber),
                    params,
                    document.getAbbyyOcrSettings()
            );
        }

        frDocument.Process(params);
        frDocument.ExportToMemory(writer, FileExportFormatEnum.FEF_PDF, pdfParams);
        frDocument.Close();

        // As the writer is written to, getPdf() will never be empty
        return writer.getPdf().orElse(null);
    }

    protected class PdfFileWriter implements IFileWriter, Closeable {
        ByteArrayOutputStream outputStream;

        public void Open(String s, Ref<Integer> ref) {
            outputStream = new ByteArrayOutputStream();
        }

        public void Write(byte[] bytes) {
            try {
                assert outputStream != null;
                outputStream.write(bytes);
            } catch (IOException ignore) {

            }
        }

        public void Close() {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ignore) {

            }
        }

        public void close() {
            Close();
        }

        public Optional<PdfDocument> getPdf() throws IOException {
            if (outputStream == null) {
                return Optional.empty();
            }

            return Optional.of(new PdfDocument(
                    new ByteArrayInputStream(outputStream.toByteArray())
            ));
        }
    }
}
