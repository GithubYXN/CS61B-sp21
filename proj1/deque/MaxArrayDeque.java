package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        this.comparator = c;
    }

    public T max() {
        return max(comparator);
    }

    public T max(Comparator<T> c) {
        if (this.isEmpty()) {
            return null;
        }
        T maxElement = get(0);
        for (T item : this) {
            if (c.compare(item, maxElement) > 0) {
                maxElement = item;
            }
        }
        return maxElement;
    }
}
