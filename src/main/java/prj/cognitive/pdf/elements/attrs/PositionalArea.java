package prj.cognitive.pdf.elements.attrs;

import prj.cognitive.pdf.elements.Page;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.gson.annotations.Expose;

import java.awt.*;

public class PositionalArea implements Comparable<PositionalArea> {
    private static final int PRECISION = 10000;

    @Expose
    private double x;

    @Expose
    private double y;

    private Page page;

    @Expose
    private double width;

    @Expose
    private double height;

    @Expose
    private Integer pageNumber;

    @Expose
    private Double pageWidth;

    @Expose
    private Double pageHeight;

    public PositionalArea(double x, double y, Page page, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        setPage(page);
    }

    public PositionalArea() {

    }

    private Rectangle getRectangle() {
        return new Rectangle(
                (int) getX() * PRECISION,
                (int) getY() * PRECISION,
                (int) getWidth() * PRECISION,
                (int) getHeight() * PRECISION
        );
    }

    public boolean overlaps(PositionalArea other) {
        return getRectangle().intersects(other.getRectangle());
    }

    public boolean contains(PositionalArea other) {
        return getRectangle().contains(other.getRectangle());
    }

    public PositionalArea expand(PositionalArea other) {
        Rectangle rectangle = getRectangle().union(other.getRectangle());
        return new PositionalArea(
                rectangle.getX() / PRECISION,
                rectangle.getY() / PRECISION,
                getPage(),
                rectangle.getWidth() / PRECISION,
                rectangle.getHeight() / PRECISION
        );
    }

    public PositionalArea scalePage(double ratioX, double ratioY) {
        Page scaledPage = null;

        if (getPage() != null) {
            page.getShape().setPage(null);
            scaledPage = new Page();
            scaledPage.setParent(page.getParent());
            scaledPage.setPageNumber(page.getPageNumber());
            scaledPage.setShape(page.getShape().scaleTo(ratioX, ratioY, false));

            scaledPage.getShape().setPage(scaledPage);
            page.getShape().setPage(page);
        }

        return new PositionalArea(
                getX(),
                getY(),
                scaledPage,
                getWidth(),
                getHeight()
        );
    }

    public PositionalArea scaleTo(double ratioX, double ratioY, boolean checkBounds) {
        double scaledWidth = getWidth() * ratioX;
        double scaledHeight = getHeight() * ratioY;
        double scaledX = getX() * ratioX;
        double scaledY = getY() * ratioY;

        if (getPage() != null && checkBounds) {
            scaledWidth = Math.min(scaledWidth, getPage().getShape().getWidth() - scaledX);
            scaledHeight = Math.min(scaledHeight, getPage().getShape().getHeight() - scaledY);
        }

        return new PositionalArea(
                scaledX,
                scaledY,
                getPage(),
                scaledWidth,
                scaledHeight
        );
    }

    public PositionalArea translate(PositionalArea reference) {
        return translate(
                reference.getX() + reference.getWidth(),
                reference.getY() + reference.getHeight()
        );
    }

    public PositionalArea translate(double x, double y) {
        return new PositionalArea(
                this.getX() + x,
                this.getY() + y,
                this.getPage(),
                this.getWidth(),
                this.getHeight()
        );
    }

    public PositionalArea scalePage(PositionalArea other) {
        return scalePage(
                other.getPage().getShape().getWidth() / this.getPage().getShape().getWidth(),
                other.getPage().getShape().getHeight() / this.getPage().getShape().getHeight()
        );
    }

    public PositionalArea scalePage(Page otherPage) {
        return scalePage(
                otherPage.getShape().getWidth() / getPage().getShape().getWidth(),
                otherPage.getShape().getHeight() / getPage().getShape().getHeight()
        );
    }

    public PositionalArea scaleTo(PositionalArea other) {
        return scaleTo(
                other.getWidth() / this.getWidth(),
                other.getHeight() / this.getHeight()
        );
    }

    public PositionalArea scaleTo(Page otherPage) {
        return scaleTo(
                otherPage.getShape().getWidth() / getPage().getShape().getWidth(),
                otherPage.getShape().getHeight() / getPage().getShape().getHeight()
        );
    }

    public PositionalArea scaleTo(double ratioX, double ratioY) {
        return scaleTo(ratioX, ratioY, true);
    }

    public PositionalArea getCenterPoint() {
        return new PositionalArea(
                getX() + getWidth() / 2.0,
                getY() + getHeight() / 2.0,
                page,
                0,
                0
        );
    }

    public double getDistanceTo(PositionalArea other) {
        return Math.sqrt(
                Math.pow(this.getX() - other.getX(), 2) + Math.pow(this.getY() - other.getY(), 2)
        );
    }

    public double getPropWidth() {
        return getWidth() / getPage().getShape().getWidth();
    }

    public double getPropHeight() {
        return getHeight() / getPage().getShape().getHeight();
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

    public Page getPage() {
        if (page == null && pageNumber != null && pageHeight != null && pageWidth != null) {
            page = new Page();
            page.setPageNumber(pageNumber);
            page.setShape(new PositionalArea(
                    0, 0, null, pageWidth, pageHeight
            ));
            page.getShape().page = page;
        }

        return page;
    }

    public void setPage(Page page) {
        this.page = page;
        if (this.page != null) {
            this.pageNumber = page.getPageNumber();

            if (this.page.getShape() != null) {
                this.pageWidth = page.getShape().getWidth();
                this.pageHeight = page.getShape().getHeight();
            }
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositionalArea)) return false;
        PositionalArea that = (PositionalArea) o;
        return Double.compare(that.getX(), this.getX()) == 0 &&
                Double.compare(that.getY(), this.getY()) == 0 &&
                Double.compare(that.getWidth(), this.getWidth()) == 0 &&
                Double.compare(that.getHeight(), this.getHeight()) == 0 &&
                Objects.equal(that.getPage(), this.getPage());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getX(), getY(), getPage(), getWidth(), getHeight());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("x", x)
                .add("y", y)
                .add("page", getPage() == null ? null : getPage().getPageNumber())
                .add("width", width)
                .add("height", height)
                .toString();
    }

    public int compareTo(PositionalArea that) {
        return ComparisonChain.start()
                .compare(this.getPage(), that.getPage())
                .compare(this.y, that.y)
                .compare(this.x, that.x)
                .compare(this.height, that.height)
                .compare(this.width, that.width)
                .result();
    }
}