package prj.cognitive.providers;

import java.util.HashMap;
import java.util.Map;

public class ProviderSystem {
    private static Map<String, AbstractProvider> providers;

    public static void start() {
        providers = new HashMap<>();
        providers.put(Tegaki.NAME, new Tegaki());
        providers.put(ImageExtractor.NAME, new ImageExtractor());
        providers.put(TextExtractor.NAME, new TextExtractor());
        providers.put(MicrosoftHandwriting.NAME, new MicrosoftHandwriting());
        providers.put(Zxing.NAME, new Zxing());
        providers.put(SimpleWeb.NAME, new SimpleWeb());
    }

    public static AbstractProvider get(String name) {
        if (providers == null) {
            start();
        }

        return providers.get(name);
    }
}
