package prj.cognitive.pdf.elements;

import prj.cognitive.pdf.elements.attrs.PositionalArea;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.gson.annotations.Expose;

import java.util.Optional;
import java.util.stream.Stream;

public class Page extends Node implements Comparable<Node> {
    @Expose
    private int pageNumber;

    public Page() {
        super();
    }

    public Page(Document parent) {
        super(parent);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Stream<Text> search(String term, double threshold) {
        return children.stream().map(
                (text) -> ((Text) text).search(term, threshold)
        ).filter(Optional::isPresent).map(Optional::get);
    }

    public Stream<Text> extractRegion(PositionalArea region) {
        return children.parallelStream().map(
                (text) -> ((Text) text).extractRegion(region)
        ).filter(Optional::isPresent).map(Optional::get);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Page)) return false;
        Page page = (Page) o;
        return pageNumber == page.pageNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pageNumber);
    }

    public int compareTo(Node that) {
        if (!(that instanceof Page)) return super.compareTo(that);

        Page thatPage = (Page) that;

        return ComparisonChain.start()
                .compare(this.pageNumber, thatPage.pageNumber)
                .result();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("pageNumber", pageNumber)
                .add("children", children)
                .add("shape", shape)
                .toString();
    }
}
