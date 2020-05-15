package prj.cognitive.providers;

import prj.cognitive.pdf.elements.Text;
import prj.cognitive.processor.DocumentConfiguration;
import prj.cognitive.utils.Config;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TextExtractor extends AbstractProvider {
    public static final String NAME = Config.get(
            TextExtractor.class, "NAME", "text"
    );

    @Override
    public String startProcessing(BufferedImage image, List<Text> documentText, DocumentConfiguration.Field configuration) {
        return documentText.stream().map(Text::getValue).collect(Collectors.joining("\n"));
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
