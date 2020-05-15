package prj.cognitive.providers;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Tegaki extends AbstractProvider {
    public static final String NAME = Config.get(
            Tegaki.class, "NAME", "tegaki"
    );
    private static final String API_KEY = Config.get(
            Tegaki.class, "API_KEY", "9bf4adb6-31f0-4834-804f-e040c95d469d"
    );
    private static final String BASE_URL = Config.get(
            Tegaki.class, "BASE_URL", "https://api.tegaki.ai/hwr/v2/"
    );


    private RequestBody buildRequest(BufferedImage image, DocumentConfiguration.Field configuration) {
        RequestBody body = configuration.getTegakiRequest();

        for (RequestBody.Field field : body.fields) {
            if (field.multiLine != null) {
                field.multiLine.imageData = Images.toBase64(image);
            }

            if (field.singleLine != null) {
                field.singleLine.imageData = Images.toBase64(image);
            }

            if (field.boxedCharacters != null) {
                field.boxedCharacters.imageData = Images.toBase64(image);
            }

            if (field.checkbox != null) {
                field.checkbox.imageData = Images.toBase64(image);

                if (field.checkbox.templateData != null) {
                    BufferedImage templateImage = Images.fromBase64(field.checkbox.templateData).orElseThrow(
                            () -> new RuntimeException("Could not read template data")
                    );
                    templateImage = Images.resize(templateImage, image.getWidth(), image.getHeight());
                    field.checkbox.templateData = Images.toBase64(templateImage);
                }
            }
        }

        return body;
    }

    public String startProcessing(BufferedImage image, List<Text> documentText, DocumentConfiguration.Field configuration) {
        RequestBody body = buildRequest(image, configuration);

        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

        HttpResponse<TegakiFieldResponse> response = Unirest.post(BASE_URL + "field")
                .header("Authorization", "apikey " + API_KEY)
                .header("Content-Type", "application/json")
                .body(gson.toJson(body))
                .asObject(TegakiFieldResponse.class);

        if (response.getBody() == null && response.getParsingError().isPresent()) {
            System.out.println(response.getParsingError().get().getOriginalBody());
            return null;
        }

        if (response.getBody().getErrors() != null) {
            System.out.println(response.getBody().toString());
            return null;
        }

        return response.getBody().getRequestId();
    }

    private TegakiStatusResponse getStatus(String processingId) {
        HttpResponse<TegakiStatusResponse> response = Unirest.get(BASE_URL + "request/{id}")
                .routeParam("id", processingId)
                .header("Authorization", "apikey " + API_KEY)
                .asObject(TegakiStatusResponse.class);

        if (response.getBody() == null && response.getParsingError().isPresent()) {
            System.out.println(response.getParsingError().get().getOriginalBody());
            return null;
        }

        return response.getBody();
    }

    private void deleteRequest(String processingId) {
        Unirest.delete(BASE_URL + "/request/{id}")
                .routeParam("id", processingId)
                .header("Authorization", "apikey " + API_KEY)
                .asEmpty();

        Unirest.shutDown();
    }

    @Override
    public Optional<ProviderResult> getValue(String processingId) {
        TegakiStatusResponse statusResponse = null;
        int backoff = 1;

        while (statusResponse == null || statusResponse.getState().equals(TegakiStatusResponse.STATUS_RUNNING)) {
            try {
                TimeUnit.SECONDS.sleep(backoff);
            } catch (InterruptedException ignore) {

            }
            statusResponse = getStatus(processingId);
            backoff = Math.min(12, backoff * 2);
        }

        deleteRequest(processingId);

        return statusResponse.toProviderResult();
    }

    public class RequestBody {
        @Expose
        private String version;
        @Expose
        private List<Field> fields;
        @Expose
        private String name;
        public RequestBody(String version, List<Field> fields, String name) {
            this.version = version;
            this.fields = fields;
            this.name = name;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("version", version)
                    .add("fields", fields)
                    .add("name", name)
                    .toString();
        }

        private class Field {
            @Expose
            private String name;
            @Expose
            private TextField singleLine;
            @Expose
            private TextField multiLine;
            @Expose
            private TextField boxedCharacters;
            @Expose
            private Checkbox checkbox;
            public Field(String name, TextField singleLine, TextField multiLine, TextField boxedCharacters, Checkbox checkbox) {
                this.name = name;
                this.singleLine = singleLine;
                this.multiLine = multiLine;
                this.boxedCharacters = boxedCharacters;
                this.checkbox = checkbox;
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .add("name", name)
                        .add("singleLine", singleLine)
                        .add("multiLine", multiLine)
                        .add("boxedCharacters", boxedCharacters)
                        .add("checkbox", checkbox)
                        .toString();
            }

            private class TextField {
                @Expose
                private String imageData;
                @Expose
                private CharacterTypes characterType;
                @Expose
                private String additionalCharacters;
                @Expose
                private Characters configuration;
                @Expose
                private Integer numberOfBoxes;
                @Expose
                private Integer marginBetweenBoxes;
                public TextField(String imageData, CharacterTypes characterType, String additionalCharacters, Characters configuration, Integer numberOfBoxes, Integer marginBetweenBoxes) {
                    this.imageData = imageData;
                    this.characterType = characterType;
                    this.additionalCharacters = additionalCharacters;
                    this.configuration = configuration;
                    this.numberOfBoxes = numberOfBoxes;
                    this.marginBetweenBoxes = marginBetweenBoxes;
                }

                @Override
                public String toString() {
                    return MoreObjects.toStringHelper(this)
                            .add("imageData", imageData == null ? null : imageData.length())
                            .add("characterType", characterType)
                            .add("additionalCharacters", additionalCharacters)
                            .add("configuration", configuration)
                            .add("numberOfBoxes", numberOfBoxes)
                            .add("marginBetweenBoxes", marginBetweenBoxes)
                            .toString();
                }

                private class CharacterTypes {
                    @Expose
                    private boolean katakana;
                    @Expose
                    private boolean hiragana;
                    @Expose
                    private boolean kanji;
                    @Expose
                    private boolean digits;
                    @Expose
                    private boolean punctuation;
                    @Expose
                    private boolean upperCaseLatin;
                    @Expose
                    private boolean lowerCaseLatin;

                    public CharacterTypes(Boolean katakana, Boolean hiragana, Boolean kanji, Boolean digits, Boolean punctuation, Boolean upperCaseLatin, Boolean lowerCaseLatin) {
                        this.katakana = katakana;
                        this.hiragana = hiragana;
                        this.kanji = kanji;
                        this.digits = digits;
                        this.punctuation = punctuation;
                        this.upperCaseLatin = upperCaseLatin;
                        this.lowerCaseLatin = lowerCaseLatin;
                    }

                    @Override
                    public String toString() {
                        return MoreObjects.toStringHelper(this)
                                .add("hiragana", hiragana)
                                .add("katakana", katakana)
                                .add("kanji", kanji)
                                .add("digits", digits)
                                .add("punctuation", punctuation)
                                .add("upperCaseLatin", upperCaseLatin)
                                .add("lowerCaseLatin", lowerCaseLatin)
                                .toString();
                    }
                }

                private class Characters {
                    @Expose
                    private Boolean languageModel;
                    @Expose
                    private String contentType;

                    public Characters(Boolean languageModel, String contentType) {
                        this.languageModel = languageModel;
                        this.contentType = contentType;
                    }

                    @Override
                    public String toString() {
                        return MoreObjects.toStringHelper(this)
                                .add("languageModel", languageModel)
                                .add("contentType", contentType)
                                .toString();
                    }
                }
            }

            private class Checkbox {
                @Expose
                private String imageData;
                @Expose
                private String templateData;

                public Checkbox(String imageData, String templateData) {
                    this.imageData = imageData;
                    this.templateData = templateData;
                }

                @Override
                public String toString() {
                    return MoreObjects.toStringHelper(this)
                            .add("imageData", imageData == null ? null : imageData.length())
                            .add("templateData", templateData == null ? null : templateData.length())
                            .toString();
                }
            }
        }
    }

    public class TegakiFieldResponse {
        @Expose
        private String requestId;

        @Expose
        private List<TegakiStatusResponse.TegakiStatusResponseErrors> errors;

        public TegakiFieldResponse(String requestId, List<TegakiStatusResponse.TegakiStatusResponseErrors> errors) {
            this.requestId = requestId;
            this.errors = errors;
        }

        public String getRequestId() {
            return requestId;
        }

        public List<TegakiStatusResponse.TegakiStatusResponseErrors> getErrors() {
            return errors;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TegakiFieldResponse)) return false;
            TegakiFieldResponse that = (TegakiFieldResponse) o;
            return Objects.equal(requestId, that.requestId) &&
                    Objects.equal(errors, that.errors);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(requestId, errors);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("requestId", requestId)
                    .add("errors", errors)
                    .toString();
        }
    }

    public class TegakiStatusResponse {
        public static final String STATUS_RUNNING = "RUNNING";
        public static final String STATUS_FAILED = "FAILED";
        public static final String STATUS_COMPLETED = "COMPLETED";
        @Expose
        private String requestId;
        @Expose
        private String state;
        @Expose
        private List<TegakiStatusResponseErrors> errors;
        @Expose
        private List<TegakiStatusResponseResults> results;

        public TegakiStatusResponse(String requestId, String state, List<TegakiStatusResponseErrors> errors, List<TegakiStatusResponseResults> results) {
            this.requestId = requestId;
            this.state = state;
            this.errors = errors;
            this.results = results;
        }

        public Optional<ProviderResult> toProviderResult() {
            if (!STATUS_COMPLETED.equals(getState())) {
                return Optional.empty();
            }

            for (TegakiStatusResponseResults field : results) {
                if (field.checkbox != null) {
                    return Optional.of(new ProviderResult(
                            field.checkbox.getIsChecked().getConfidence(),
                            field.checkbox.getIsChecked().result.toString()
                    ));
                } else if (field.multiLine != null) {
                    double confidence = field.multiLine.stream().mapToDouble(
                            TegakiStatusResponseTextField::getConfidence
                    ).average().orElse(0);
                    String text = field.multiLine.stream().map(
                            TegakiStatusResponseTextField::getText
                    ).collect(Collectors.joining("\n"));

                    return Optional.of(new ProviderResult(
                            confidence,
                            text
                    ));
                } else {
                    TegakiStatusResponseTextField value = field.singleLine;
                    if (field.boxedCharacters != null) value = field.boxedCharacters;

                    return Optional.of(new ProviderResult(
                            value.getConfidence(),
                            value.getText()
                    ));
                }
            }

            return Optional.empty();
        }

        public String getRequestId() {
            return requestId;
        }

        public String getState() {
            return state;
        }

        public List<TegakiStatusResponseErrors> getErrors() {
            return errors;
        }

        public List<TegakiStatusResponseResults> getResults() {
            return results;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TegakiStatusResponse)) return false;
            TegakiStatusResponse that = (TegakiStatusResponse) o;
            return Objects.equal(requestId, that.requestId) &&
                    Objects.equal(state, that.state) &&
                    Objects.equal(errors, that.errors) &&
                    Objects.equal(results, that.results);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(requestId, state, errors, results);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("requestId", requestId)
                    .add("state", state)
                    .add("errors", errors)
                    .add("results", results)
                    .toString();
        }

        public class TegakiStatusResponseErrors {
            @Expose
            private Integer code;

            @Expose
            private String message;

            @Expose
            private String fieldId;

            @Expose
            private String details;

            public TegakiStatusResponseErrors(Integer code, String message, String fieldId, String details) {
                this.code = code;
                this.message = message;
                this.fieldId = fieldId;
                this.details = details;
            }

            public Integer getCode() {
                return code;
            }

            public String getMessage() {
                return message;
            }

            public String getFieldId() {
                return fieldId;
            }

            public String getDetails() {
                return details;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof TegakiStatusResponseErrors)) return false;
                TegakiStatusResponseErrors that = (TegakiStatusResponseErrors) o;
                return Objects.equal(code, that.code) &&
                        Objects.equal(message, that.message) &&
                        Objects.equal(fieldId, that.fieldId) &&
                        Objects.equal(details, that.details);
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(code, message, fieldId, details);
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .add("code", code)
                        .add("message", message)
                        .add("fieldId", fieldId)
                        .add("details", details)
                        .toString();
            }
        }

        public class TegakiStatusResponseTextField {
            @Expose
            private String text;

            @Expose
            private Double confidence;

            public TegakiStatusResponseTextField(String text, Double confidence) {
                this.text = text;
                this.confidence = confidence;
            }

            public String getText() {
                return text;
            }

            public Double getConfidence() {
                return confidence;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof TegakiStatusResponseTextField)) return false;
                TegakiStatusResponseTextField that = (TegakiStatusResponseTextField) o;
                return Objects.equal(text, that.text) &&
                        Objects.equal(confidence, that.confidence);
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(text, confidence);
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .add("text", text)
                        .add("confidence", confidence)
                        .toString();
            }
        }

        public class TegakiStatusResponseCheckbox {
            @Expose
            private TegakiStatusResponseCheckboxIsChecked isChecked;

            public TegakiStatusResponseCheckbox(TegakiStatusResponseCheckboxIsChecked isChecked) {
                this.isChecked = isChecked;
            }

            public TegakiStatusResponseCheckboxIsChecked getIsChecked() {
                return isChecked;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof TegakiStatusResponseCheckbox)) return false;
                TegakiStatusResponseCheckbox that = (TegakiStatusResponseCheckbox) o;
                return Objects.equal(isChecked, that.isChecked);
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(isChecked);
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .add("isChecked", isChecked)
                        .toString();
            }
        }

        public class TegakiStatusResponseCheckboxIsChecked {
            @Expose
            private Boolean result;

            @Expose
            private Double confidence;

            public TegakiStatusResponseCheckboxIsChecked(Boolean result, Double confidence) {
                this.result = result;
                this.confidence = confidence;
            }

            public Boolean getResult() {
                return result;
            }

            public Double getConfidence() {
                return confidence;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof TegakiStatusResponseCheckboxIsChecked)) return false;
                TegakiStatusResponseCheckboxIsChecked that = (TegakiStatusResponseCheckboxIsChecked) o;
                return Objects.equal(result, that.result) &&
                        Objects.equal(confidence, that.confidence);
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(result, confidence);
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .add("result", result)
                        .add("confidence", confidence)
                        .toString();
            }
        }

        public class TegakiStatusResponseResults {
            @Expose
            private String fieldId;

            @Expose
            private TegakiStatusResponseTextField singleLine;

            @Expose
            private List<TegakiStatusResponseTextField> multiLine;

            @Expose
            private TegakiStatusResponseCheckbox checkbox;

            @Expose
            private TegakiStatusResponseTextField boxedCharacters;
        }
    }
}
