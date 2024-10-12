package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int head;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        head = -1;
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    public class ArrayDequeIterator implements Iterator<T> {
        private int cnt;
        private int h;

        public ArrayDequeIterator() {
            cnt = 0;
            h = head;
        }

        @Override
        public boolean hasNext() {
            return cnt < size;
        }

        @Override
        public T next() {
            T item = items[h];
            h = (h + 1) % items.length;
            cnt += 1;
            return item;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        ArrayDeque<T> other = (ArrayDeque<T>) o;
        if (other.size() != this.size()) {
            return false;
        }
        for (T item : this) {
            if (!other.contains(item)) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(T x) {
        for (int i = 0; i < size; i++) {
            if (!items[i].equals(x)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addFirst(T item) {
        head = head == -1 ? 0 : ((head - 1) + items.length) % items.length;
        items[head] = item;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        int index = (head + size) % items.length;
        items[index] = item;
        size += 1;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        if (size == 0) {
            System.out.println("The deque is empty.");
            return;
        }
        int cnt = 0, idx = head;
        for ( ; cnt < size - 1; cnt++) {
            System.out.print(items[idx] + " ");
            idx = (idx + 1) % items.length;
        }
        System.out.println(items[idx]);
    }

    @Override
    public T removeFirst() {
        T item = items[head];
        head = head == items.length - 1 ? 0 : head + 1;
        size -= 1;
        return item;
    }

    @Override
    public T removeLast() {
        int index = (head + size) % size;
        T item = items[index];
        size -= 1;
        return item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[(head + index) % size];
    }
}
