package prj.cognitive.utils;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.Optional;

public class OccurrenceCounter<T> {
    protected Multiset<T> elements;

    public OccurrenceCounter() {
        elements = HashMultiset.create();
    }

    public void add(T e) {
        this.elements.add(e);
    }

    public Optional<T> getMostCommon() {
        T mostCommon = null;
        int mostCommonCount = 0;

        for (Multiset.Entry<T> entry : elements.entrySet()) {
            if (entry.getCount() > mostCommonCount) {
                mostCommonCount = entry.getCount();
                mostCommon = entry.getElement();
            }
        }

        return Optional.ofNullable(mostCommon);
    }

    public Multiset<T> getElements() {
        return elements;
    }
}
