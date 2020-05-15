package prj.cognitive.providers;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import prj.cognitive.pdf.elements.Text;
import prj.cognitive.processor.DocumentConfiguration;
import prj.cognitive.utils.Config;
import prj.cognitive.utils.Images;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

public class Zxing extends AbstractProvider {
    public static final String NAME = Config.get(
            ImageExtractor.class, "NAME", "barcode"
    );

    public static final Integer MINIMUM_DIMENSION = Config.get(
            ImageExtractor.class, "MINIMUM_DIMENSION", 250
    );

    @Override
    public String startProcessing(BufferedImage image, List<Text> documentText, DocumentConfiguration.Field configuration) {
        LuminanceSource luminanceSource = new BufferedImageLuminanceSource(
                Images.withMinimumSize(image, MINIMUM_DIMENSION, MINIMUM_DIMENSION)
        );
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            System.out.println(result.getBarcodeFormat().name());
            return result.getText();
        } catch (NotFoundException exc) {
            System.out.println("unable to read barcode");
            return null;
        }
    }

    @Override
    public Optional<ProviderResult> getValue(String processingId) {
        if (processingId == null || processingId.trim().length() == 0) {
            return Optional.empty();
        }

        return Optional.of(new ProviderResult(
                1.0,
                processingId
        ));
    }
}
