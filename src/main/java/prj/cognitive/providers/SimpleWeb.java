package prj.cognitive.providers;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import prj.cognitive.pdf.elements.Text;
import prj.cognitive.processor.DocumentConfiguration;
import prj.cognitive.utils.Config;
import prj.cognitive.utils.Images;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SimpleWeb extends AbstractProvider {
    public static final String NAME = Config.get(
            SimpleWeb.class, "NAME", "simple_web"
    );

    public String startProcessing(BufferedImage image, List<Text> documentText, DocumentConfiguration.Field configuration) {
        RequestBody body = new RequestBody(
                Images.toBase64Uri(image),
                documentText.stream().map(Text::getValue).collect(Collectors.toList()),
                configuration.getSimpleWeb().getData()
        );

        System.out.println(body);

        HttpResponse<ProviderResult> response = Unirest.post(configuration.getSimpleWeb().getUrl())
                .header("content-type", "application/json")
                .body(body)
                .asObject(ProviderResult.class);

        if (response.isSuccess()) {
            return response.getBody().getConfidence() + "|" + response.getBody().getText();
        }

        return null;
    }

    @Override
    public Optional<ProviderResult> getValue(String processingId) {
        if (processingId == null) {
            return Optional.empty();
        }

        return Optional.of(new ProviderResult(
                Double.parseDouble(processingId.split("\\|", 2)[0]),
                processingId.split("\\|", 2)[1]
        ));
    }

    public static class RequestBody {
        @Expose
        private String image;

        @Expose
        private List<String> text;

        @Expose
        private JsonObject data;

        public RequestBody(String image, List<String> text, JsonObject data) {
            this.image = image;
            this.text = text;
            this.data = data;
        }

        public String getImage() {
            return image;
        }

        public List<String> getText() {
            return text;
        }

        public JsonObject getData() {
            return data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RequestBody)) return false;
            RequestBody that = (RequestBody) o;
            return Objects.equal(image, that.image) &&
                    Objects.equal(text, that.text) &&
                    Objects.equal(data, that.data);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(image, text, data);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("image", image)
                    .add("text", text)
                    .add("data", data)
                    .toString();
        }
    }

    public static class Configuration {
        @Expose
        private String url;

        @Expose
        private JsonObject data;

        public Configuration(String url, JsonObject data) {
            this.url = url;
            this.data = data;
        }

        public String getUrl() {
            return url;
        }

        public JsonObject getData() {
            return data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Configuration)) return false;
            Configuration that = (Configuration) o;
            return Objects.equal(url, that.url) &&
                    Objects.equal(data, that.data);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(url, data);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("url", url)
                    .add("data", data)
                    .toString();
        }
    }
}
