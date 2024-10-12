package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {

    private T[] items;
    private int size;
    private int head;
    private int length;
    private final int MIN_LEN = 16;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        head = -1;
        length = items.length;
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
            h = (h + 1) % length;
            cnt += 1;
            return item;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (other.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!other.get(i).equals(this.get(i))) {
                return false;
            };
        }
        return true;
    }

//    public int getLength() {
//        return length;
//    }

    @Override
    public void addFirst(T item) {
        if (size == length) {
            int capacity = length + (length >> 1);
            resize(capacity);
            length = capacity;
        }
        head = head == -1 ? 0 : ((head - 1) + length) % length;
        items[head] = item;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size == 0) {
            addFirst(item);
            return;
        }
        if (size == length) {
            int capacity = length + (length >> 1);
            resize(capacity);
            length = capacity;
        }
        int index = (head + size) % length;
        items[index] = item;
        size += 1;
    }

//    @Override
//    public boolean isEmpty() {
//        return size == 0;
//    }

    @Override
    public int size() {
        return size;
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        int idx = 0;
        int H = head;

        for (T item : this) {
            a[idx++] = item;
        }
        items = a;
        head = 0;
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
            idx = (idx + 1) % length;
        }
        System.out.println(items[idx]);
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if (size < length / 4 && length > MIN_LEN) {
            resize(length / 2);
            length /= 2;
        }
        T item = items[head];
        head = head == length - 1 ? 0 : head + 1;
        size -= 1;
        return item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if (size < length / 4 && length > MIN_LEN) {
            resize(length / 2);
            length /= 2;
        }
        int index = (head + size - 1) % length;
        T item = items[index];
        size -= 1;
        return item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[(head + index) % length];
    }

}
