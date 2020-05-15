package prj.cognitive.processor;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

import prj.cognitive.pdf.PdfDocument;
import prj.cognitive.pdf.elements.Text;
import prj.cognitive.pdf.elements.attrs.PositionalArea;
import prj.cognitive.providers.ProviderResult;
import prj.cognitive.providers.ProviderSystem;
import prj.cognitive.utils.Config;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ProcessedDocument implements Comparable<ProcessedDocument> {
    public static final double MISSING_LABEL_WEIGHT = Config.get(
            ProcessedDocument.class, "MISSING_LABEL_WEIGHT", 1000.0
    );

    public static final boolean ENLARGE_REGIONS_TO_BORDERS = Config.get(
            ProcessedDocument.class, "ENLARGE_REGIONS_TO_BORDERS", false
    );

    private PdfDocument document;
    private Map<String, DocumentConfiguration.Field> fields;
    private Map<String, PositionalArea> labels;
    private Map<String, ProviderResult> results;
    private String groupName;
    private Double score = null;

    public ProcessedDocument(String groupName, PdfDocument document, Map<String, DocumentConfiguration.Field> fields) {
        this.document = document;
        this.fields = fields;
        this.groupName = groupName;

        identifyLabels();
    }

    public Map<String, DocumentConfiguration.Field> getFields() {
        return fields;
    }

    public Map<String, PositionalArea> getLabels() {
        return labels;
    }

    public void identifyLabels() {
        score = 0.0;
        labels = new HashMap<>();

        for (String labelName : fields.keySet()) {
            if (fields.get(labelName).getLabels() == null || fields.get(labelName).getLabels().size() == 0) {
                System.out.println("FOUND NO-LABEL FIELD");
                labels.put(labelName, null);
                continue;
            }

            Optional<MatchedLabel> matchedLabel = fields.get(labelName).getLabels().stream().flatMap(
                    labelText -> document.getStructuredDocument().search(labelText).stream()
            ).map(
                    match -> {
                        PositionalArea scaledTarget = fields.get(labelName).getLabelRegion().scalePage(match.getShape()).scaleTo(match.getShape());
                        System.out.println("MATCHED!");
                        return new MatchedLabel(match, scaledTarget.getDistanceTo(match.getShape()));
                    }
            ).sorted().findFirst();

            if (matchedLabel.isPresent()) {
                System.out.println("FOUND LABEL");
                System.out.println(matchedLabel.get());
                score += matchedLabel.get().getDistance();
                labels.put(labelName, matchedLabel.get().getText().getShape());
            }
        }

        score += (fields.size() - labels.size()) * MISSING_LABEL_WEIGHT;
    }

    private String processField(String fieldName, PositionalArea region) throws IOException {
        return ProviderSystem.get(fields.get(fieldName).getProvider()).startProcessing(
                document.renderRegion(region, ENLARGE_REGIONS_TO_BORDERS),
                document.getStructuredDocument().extractRegion(region),
                fields.get(fieldName)
        );
    }

    public void process() throws IOException {
        results = new HashMap<>();
        Map<String, String> processingIds = new HashMap<>();
        List<Double> xScale = new ArrayList<>();
        List<Double> yScale = new ArrayList<>();
        List<Double> xTranslations = new ArrayList<>();
        List<Double> yTranslations = new ArrayList<>();
        Map<String, PositionalArea> extractRegions = new HashMap<>();

        for (String fieldName : labels.keySet()) {
            PositionalArea extractRegion;

            if (labels.get(fieldName) == null) {
                // TODO: Move this to below so translations are applied and remove from classifier
                extractRegion = fields.get(fieldName).getRegion().scaleTo(
                        document.getStructuredDocument().getPage(
                                fields.get(fieldName).getRegion().getPage().getPageNumber()
                        )
                );
            } else {
                double pageScaleX = labels.get(fieldName).getPage().getShape().getWidth() / fields.get(fieldName).getLabelRegion().getPage().getShape().getWidth();
                double pageScaleY = labels.get(fieldName).getPage().getShape().getHeight() / fields.get(fieldName).getLabelRegion().getPage().getShape().getHeight();
                double scaleX = labels.get(fieldName).getPropWidth() / fields.get(fieldName).getLabelRegion().getPropWidth();
                double scaleY = labels.get(fieldName).getPropHeight() / fields.get(fieldName).getLabelRegion().getPropHeight();

                extractRegion = fields.get(fieldName).getRegion().scalePage(pageScaleX, pageScaleY).scaleTo(pageScaleX, pageScaleY).scaleTo(scaleX, scaleY).translate(
                        labels.get(fieldName)
                );

                extractRegions.put(fieldName, extractRegion);

                xScale.add(scaleX);
                yScale.add(scaleY);
                xTranslations.add(labels.get(fieldName).getX() - fields.get(fieldName).getLabelRegion().getX());
                yTranslations.add(labels.get(fieldName).getY() - fields.get(fieldName).getLabelRegion().getY());

                System.out.println("Scale Factor = " + scaleX + ", " + scaleY);
                System.out.println("Field Label Page Width = " + fields.get(fieldName).getLabelRegion().getPage().getShape().getWidth());
                System.out.println("Field Region Page Width = " + fields.get(fieldName).getRegion().getPage().getShape().getWidth());
                System.out.println("Extract Label Page Width = " + labels.get(fieldName).getPage().getShape().getWidth());
                System.out.println("Extract Region Page Width = " + extractRegion.getPage().getShape().getWidth());

                System.out.println("Label Region for " + fieldName + " = " + labels.get(fieldName).toString());
                System.out.println("Value Region for " + fieldName + " = " + extractRegion.toString());
            }

            processingIds.put(fieldName, processField(fieldName, extractRegion));
        }

        Set<String> missedFields = fields.keySet().stream().filter(
                field -> !labels.keySet().contains(field)
        ).collect(Collectors.toSet());

        if (labels.size() > 0 && missedFields.size() > 0) {
            System.out.println("MISSING FIELDS _ TRYING NOW");
            double avgScaleX = xScale.stream().mapToDouble(n -> n).average().orElse(1.0);
            double avgScaleY = yScale.stream().mapToDouble(n -> n).average().orElse(1.0);
            double avgXTranslations = xTranslations.stream().mapToDouble(n -> n).average().orElse(0.0);
            double avgYTranslations = yTranslations.stream().mapToDouble(n -> n).average().orElse(0.0);

            for (String fieldName : missedFields) {
                double pageScaleX = document.getStructuredDocument().getPage(
                        fields.get(fieldName).getRegion().getPage().getPageNumber()
                ).getShape().getWidth() / fields.get(fieldName).getRegion().getPage().getShape().getWidth();
                double pageScaleY = document.getStructuredDocument().getPage(
                        fields.get(fieldName).getRegion().getPage().getPageNumber()
                ).getShape().getHeight() / fields.get(fieldName).getRegion().getPage().getShape().getHeight();

                PositionalArea extractRegion = fields.get(fieldName).getLabelRegion()
                        .translate(fields.get(fieldName).getRegion())
                        .scalePage(pageScaleX, pageScaleY).scaleTo(pageScaleX, pageScaleY).scaleTo(avgScaleX, avgScaleY)
                        .translate(avgXTranslations, avgYTranslations);

                extractRegions.put(fieldName, extractRegion);
                processingIds.put(fieldName, processField(fieldName, extractRegion));
                System.out.println("SUPPLEMENT " + fieldName);
            }
        }

        for (String fieldName : processingIds.keySet()) {
            results.put(fieldName, ProviderSystem.get(fields.get(fieldName).getProvider()).getValue(
                    processingIds.get(fieldName)
            ).orElse(null).withRegion(extractRegions.get(fieldName)));
        }
    }

    public Map<String, ProviderResult> getResults() {
        if (results == null) {
            try {
                process();
            } catch (IOException ignore) {

            }
        }

        return results;
    }

    public Double getScore() {
        return score;
    }

    public String getGroupName() {
        return groupName;
    }

    @Override
    public int compareTo(ProcessedDocument o) {
        return ComparisonChain.start()
                .compare(getScore(), o.getScore())
                .result();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcessedDocument)) return false;
        ProcessedDocument that = (ProcessedDocument) o;
        return Double.compare(that.score, score) == 0 &&
                Objects.equal(document, that.document) &&
                Objects.equal(groupName, that.groupName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(document, groupName, score);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("groupName", groupName)
                .add("score", score)
                .toString();
    }

    private class MatchedLabel implements Comparable<MatchedLabel> {
        private Text text;
        private Double distance;

        public MatchedLabel(Text text, Double distance) {
            this.text = text;
            this.distance = distance;
        }

        public Text getText() {
            return text;
        }

        public Double getDistance() {
            return distance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MatchedLabel)) return false;
            MatchedLabel that = (MatchedLabel) o;
            return Objects.equal(text, that.text) &&
                    Objects.equal(distance, that.distance);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(text, distance);
        }

        @Override
        public int compareTo(MatchedLabel o) {
            return ComparisonChain.start()
                    .compare(this.distance, o.distance)
                    .result();
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("text", text)
                    .add("distance", distance)
                    .toString();
        }
    }
}
