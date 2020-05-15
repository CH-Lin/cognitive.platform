package prj.cognitive.pdf.elements;

import prj.cognitive.pdf.elements.attrs.PositionalArea;
import prj.cognitive.utils.OccurrenceCounter;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;

import java.util.Optional;

public class Text extends Node {
    protected OccurrenceCounter<Double> fontSizeCounter = new OccurrenceCounter<>();

    protected OccurrenceCounter<Double> spaceWidthCounter = new OccurrenceCounter<>();

    protected OccurrenceCounter<String> fontNameCounter = new OccurrenceCounter<>();

    @Expose
    protected String value;

    @Expose
    protected Double fontSize;

    @Expose
    protected Double spaceWidth;

    @Expose
    protected String fontName;

    public Text() {

    }

    public Text(Page parent) {
        this.parent = parent;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getFontSize() {
        return fontSize;
    }

    public double getSpaceWidth() {
        return spaceWidth;
    }

    public String getFontName() {
        return fontName;
    }

    public void addCharacter(Character character) {
        fontSizeCounter.add(character.fontSize);
        spaceWidthCounter.add(character.spaceWidth);
        fontNameCounter.add(character.fontName);

        fontSize = fontSizeCounter.getMostCommon().orElse(null);
        spaceWidth = spaceWidthCounter.getMostCommon().orElse(null);
        fontName = fontNameCounter.getMostCommon().orElse(null);

        if (shape == null) {
            shape = character.getShape();
        } else {
            shape = shape.expand(character.getShape());
        }

        appendChild(character);
    }

    public Optional<Text> search(String term, double threshold) {
        int position = value.toLowerCase().indexOf(term.toLowerCase());

        if (position == -1) {
            return Optional.empty();
        }

        Text result = new Text();
        result.setValue(value.substring(position, position + term.length()));
        for (int i = position; i < position + term.length(); i++) {
            result.addCharacter((Character) children.get(i));
        }

        return Optional.of(result);
    }

    public Optional<Text> extractRegion(PositionalArea region) {
        Text result = new Text();
        StringBuilder regionValueBuilder = new StringBuilder();

        for (Node node : children) {
            Character character = (Character) node;

            if (character.getShape().overlaps(region)) {
                result.addCharacter(character);
                regionValueBuilder.append(character.getValue());
            }
        }

        String regionValue = regionValueBuilder.toString();
        if (regionValue.length() == 0) {
            return Optional.empty();
        } else {
            result.setValue(regionValue);
            return Optional.of(result);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fontSize", fontSize)
                .add("spaceWidth", spaceWidth)
                .add("fontName", fontName)
                .add("value", value)
                .add("shape", shape)
                .add("children", children)
                .toString() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Text)) return false;
        Text text = (Text) o;
        return Double.compare(text.fontSize, fontSize) == 0 &&
                Double.compare(text.spaceWidth, spaceWidth) == 0 &&
                Objects.equal(fontName, text.fontName) &&
                Objects.equal(value, text.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fontSize, spaceWidth, fontName, value);
    }
}
