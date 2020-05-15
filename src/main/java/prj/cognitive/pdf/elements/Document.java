package prj.cognitive.pdf.elements;

import prj.cognitive.pdf.elements.attrs.PositionalArea;
import prj.cognitive.utils.Config;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Document extends Node {
    private static final Double LABEL_MATCH_THRESHOLD = Config.get(
            Document.class, "LABEL_MATCH_THRESHOLD", 1.0
    );

    private LoadingCache<String, List<Text>> labels = CacheBuilder.newBuilder()
            .maximumSize(500)
            .build(new CacheLoader<String, List<Text>>() {
                @Override
                public List<Text> load(String term) throws Exception {
                    return children.parallelStream().flatMap(
                            (page) -> ((Page) page).search(term, LABEL_MATCH_THRESHOLD)
                    ).collect(Collectors.toList());
                }
            });

    public Document() {
        super();
    }

    public Page getPage(int pageNumber) {
        return (Page) getChildren().get(pageNumber - 1);
    }

    public int getPageCount() {
        return children.size();
    }

    public List<Text> search(String term) {
        try {
            return labels.get(term);
        } catch (ExecutionException exc) {
            return null;
        }
    }

    public List<Text> extractRegion(PositionalArea region) {
        return children.stream().filter(
                (page) -> ((Page) page).getPageNumber() == region.getPage().getPageNumber()
        ).flatMap(
                (page) -> ((Page) page).extractRegion(region)
        ).collect(Collectors.toList());
    }
}
