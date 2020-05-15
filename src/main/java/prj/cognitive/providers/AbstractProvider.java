package prj.cognitive.providers;

import prj.cognitive.pdf.elements.Text;
import prj.cognitive.processor.DocumentConfiguration;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

public abstract class AbstractProvider {
    public abstract String startProcessing(BufferedImage image, List<Text> documentText, DocumentConfiguration.Field configuration);

    public abstract Optional<ProviderResult> getValue(String processingId);
}
