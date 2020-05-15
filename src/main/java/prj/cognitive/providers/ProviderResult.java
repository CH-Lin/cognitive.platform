package prj.cognitive.providers;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;

import prj.cognitive.pdf.elements.attrs.PositionalArea;

public class ProviderResult {
    @Expose
    private double confidence;

    @Expose
    private String text;

    @Expose
    private PositionalArea region;

    public ProviderResult(double confidence, String text) {
        this.confidence = confidence;
        this.text = text;
    }

    public ProviderResult(double confidence, String text, PositionalArea region) {
        this.confidence = confidence;
        this.text = text;
        this.region = region;
    }

    public ProviderResult withRegion(PositionalArea otherRegion) {
        return new ProviderResult(
                confidence,
                text,
                otherRegion
        );
    }

    public double getConfidence() {
        return confidence;
    }

    public String getText() {
        return text;
    }

    public PositionalArea getRegion() {
        return region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProviderResult)) return false;
        ProviderResult that = (ProviderResult) o;
        return Double.compare(that.confidence, confidence) == 0 &&
                Objects.equal(text, that.text) &&
                Objects.equal(region, that.region);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(confidence, text, region);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("confidence", confidence)
                .add("text", text)
                .add("region", region)
                .toString();
    }
}
