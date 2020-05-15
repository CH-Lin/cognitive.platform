package prj.cognitive.pdf;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;

import java.awt.*;

public class PdfAnnotation {
    @Expose
    public int page;
    @Expose
    private double x;
    @Expose
    private double y;
    @Expose
    private double width;
    @Expose
    private double height;
    @Expose
    private String backgroundColor;
    @Expose
    private String foregroundColor;
    @Expose
    private String fontName;
    @Expose
    private double fontSize = 12.0;
    @Expose
    private String text;
    @Expose
    private double opacity = 1.0;

    public PdfAnnotation(int page, double x, double y, double width, double height, String backgroundColor, String foregroundColor, String fontName, double fontSize, String text, double opacity) {
        this.page = page;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.text = text;
        this.opacity = opacity;
    }

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Color getBackgroundColor() {
        if (backgroundColor == null) return null;

        return Color.decode(backgroundColor);
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getForegroundColor() {
        if (foregroundColor == null) return null;
        return Color.decode(foregroundColor);
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public double getFontSize() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PdfAnnotation)) return false;
        PdfAnnotation that = (PdfAnnotation) o;
        return page == that.page &&
                Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.width, width) == 0 &&
                Double.compare(that.height, height) == 0 &&
                Double.compare(that.fontSize, fontSize) == 0 &&
                Double.compare(that.opacity, opacity) == 0 &&
                Objects.equal(backgroundColor, that.backgroundColor) &&
                Objects.equal(foregroundColor, that.foregroundColor) &&
                Objects.equal(fontName, that.fontName) &&
                Objects.equal(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(page, x, y, width, height, backgroundColor, foregroundColor, fontName, fontSize, text, opacity);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("page", page)
                .add("x", x)
                .add("y", y)
                .add("width", width)
                .add("height", height)
                .add("backgroundColor", backgroundColor)
                .add("foregroundColor", foregroundColor)
                .add("fontName", fontName)
                .add("fontSize", fontSize)
                .add("text", text)
                .add("opacity", opacity)
                .toString();
    }
}
