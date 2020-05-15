package prj.cognitive.processor;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import prj.cognitive.pdf.elements.attrs.PositionalArea;
import prj.cognitive.providers.SimpleWeb;
import prj.cognitive.providers.Tegaki;

import java.util.List;
import java.util.Map;

public class DocumentConfiguration {
    @Expose
    private String languages;
    @Expose
    private Map<String, Map<String, Field>> groups;
    @Expose
    private String name;

    public DocumentConfiguration(String languages, Map<String, Map<String, Field>> groups, String name) {
        this.languages = languages;
        this.groups = groups;
        this.name = name;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public Map<String, Map<String, Field>> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, Map<String, Field>> groups) {
        this.groups = groups;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentConfiguration)) return false;
        DocumentConfiguration that = (DocumentConfiguration) o;
        return Objects.equal(languages, that.languages) &&
                Objects.equal(groups, that.groups) &&
                Objects.equal(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(languages, groups, name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("languages", languages)
                .add("groups", groups)
                .add("name", name)
                .toString();
    }

    public static class Field {
        @Expose
        private List<String> labels;

        @Expose
        private PositionalArea labelRegion;

        @Expose
        private PositionalArea region;

        @Expose
        private String provider;

        @Expose
        private Tegaki.RequestBody tegakiRequest;

        @Expose
        private SimpleWeb.Configuration simpleWeb;

        public Field(List<String> labels, PositionalArea labelRegion, PositionalArea region, String provider, Tegaki.RequestBody tegakiRequest, SimpleWeb.Configuration simpleWeb) {
            this.labels = labels;
            this.labelRegion = labelRegion;
            this.region = region;
            this.provider = provider;
            this.tegakiRequest = tegakiRequest;
            this.simpleWeb = simpleWeb;
        }

        public List<String> getLabels() {
            return labels;
        }

        public PositionalArea getLabelRegion() {
            return labelRegion;
        }

        public PositionalArea getRegion() {
            return region;
        }

        public String getProvider() {
            return provider;
        }

        public Tegaki.RequestBody getTegakiRequest() {
            return tegakiRequest;
        }

        public SimpleWeb.Configuration getSimpleWeb() {
            return simpleWeb;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Field)) return false;
            Field field = (Field) o;
            return Objects.equal(labels, field.labels) &&
                    Objects.equal(labelRegion, field.labelRegion) &&
                    Objects.equal(region, field.region) &&
                    Objects.equal(provider, field.provider) &&
                    Objects.equal(tegakiRequest, field.tegakiRequest) &&
                    Objects.equal(simpleWeb, field.simpleWeb);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(labels, labelRegion, region, provider, tegakiRequest, simpleWeb);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("labels", labels)
                    .add("labelRegion", labelRegion)
                    .add("region", region)
                    .add("provider", provider)
                    .add("tegakiRequest", tegakiRequest)
                    .add("simpleWeb", simpleWeb)
                    .toString();
        }
    }
}
