package prj.cognitive.providers;

import prj.cognitive.pdf.elements.Text;
import prj.cognitive.processor.DocumentConfiguration;
import prj.cognitive.utils.Config;
import prj.cognitive.utils.Images;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

public class ImageExtractor extends AbstractProvider {
    public static final String NAME = Config.get(
            ImageExtractor.class, "NAME", "image"
    );

    @Override
    public String startProcessing(BufferedImage image, List<Text> documentText, DocumentConfiguration.Field configuration) {
        return Images.toBase64Uri(image);
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
