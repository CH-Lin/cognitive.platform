package prj.cognitive.pdf.elements;

import prj.cognitive.pdf.elements.attrs.PositionalArea;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import org.apache.pdfbox.text.TextPosition;

public class Character extends Node {
    @Expose
    protected String value;

    @Expose
    protected Double fontSize;

    @Expose
    protected Double spaceWidth;

    @Expose
    protected String fontName;

    public Character(String value, Double fontSize, Double spaceWidth, String fontName) {
        this.value = value;
        this.fontSize = fontSize;
        this.spaceWidth = spaceWidth;
        this.fontName = fontName;
    }

    public Character(Page currentPage, TextPosition textPosition) {
        this.value = textPosition.getUnicode();
        this.fontName = textPosition.getFont().getName();
        this.fontSize = (double) textPosition.getFontSize();
        this.spaceWidth = (double) textPosition.getWidthOfSpace();

        PositionalArea area = new PositionalArea(
                textPosition.getX(),
                textPosition.getY() - textPosition.getHeight(),
                currentPage,
                textPosition.getWidth(),
                textPosition.getHeight()
        );

        setShape(area);
    }

    public String getValue() {
        return value;
    }

    public Double getFontSize() {
        return fontSize;
    }

    public Double getSpaceWidth() {
        return spaceWidth;
    }

    public String getFontName() {
        return fontName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Character)) return false;
        if (!super.equals(o)) return false;
        Character character = (Character) o;
        return Objects.equal(value, character.value) &&
                Objects.equal(fontSize, character.fontSize) &&
                Objects.equal(spaceWidth, character.spaceWidth) &&
                Objects.equal(fontName, character.fontName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), value, fontSize, spaceWidth, fontName);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("value", value)
                .add("fontSize", fontSize)
                .add("spaceWidth", spaceWidth)
                .add("fontName", fontName)
                .add("children", children)
                .add("shape", shape)
                .toString();
    }
}
