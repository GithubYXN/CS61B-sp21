package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private class Node {
        private T data;
        private Node next;
        private Node prev;

        public Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }
    private int size;
    private Node sentinel;

    //create an empty LinkedListDeque
    public LinkedListDeque() {
        sentinel = new Node(null);
        size = 0;
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private Node cur;
        private int cnt;

        public LinkedListDequeIterator() {
            cur = sentinel;
            cnt = 0;
        }

        @Override
        public boolean hasNext() {
            if (cur.next == null) {
                return false;
            }
            return cnt < size;
        }

        @Override
        public T next() {
            T ret = cur.next.data;
            cur = cur.next;
            cnt++;
            return ret;
        }
    }

    @Override
    public void addFirst(T data) {
        if (size == 0) {
            sentinel.next = new Node(data);
            Node cur = sentinel.next;
            cur.prev = cur;
            cur.next = cur;
            size += 1;
        } else {
            Node preFirst = sentinel.next;
            sentinel.next = new Node(data);
            Node cur = sentinel.next;
            cur.next = preFirst;
            cur.prev = preFirst.prev;
            preFirst.prev = cur;
            cur.prev.next = cur;
            size += 1;
        }
    }

    @Override
    public void addLast(T data) {
        if (size == 0) {
            addFirst(data);
        } else {
            Node last = sentinel.next.prev;
            last.next = new Node(data);
            Node newLast = last.next;
            newLast.prev = last;
            newLast.next = sentinel.next;
            sentinel.next.prev = newLast;
            size += 1;
        }
    }

//    @Override
//    public boolean isEmpty() {
//        return size == 0;
//    }

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
        Node cur = sentinel.next;
        while (cur.next != sentinel.next) {
            System.out.print(cur.data + " ");
            cur = cur.next;
        }
        System.out.println(cur.data);
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        } else if (size == 1) {
            Node first = sentinel.next;
            sentinel.next = null;
            size -= 1;

            return first.data;
        } else {
            Node first = sentinel.next;
            sentinel.next = first.next;
            first.next.prev = first.prev;
            first.prev.next = first.next;
            first.next = null;
            size -= 1;

            return first.data;
        }
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        } else if (size == 1) {
            return removeFirst();
        } else {
            Node last = sentinel.next.prev;
            last.prev.next = last.next;
            last.next.prev = last.prev;
            last.next = null;
            last.prev = null;
            size -= 1;

            return last.data;
        }
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        int cnt = 0;
        for (T data : this) {
            if (cnt == index) {
                return data;
            }
            cnt++;
        }

        return null;
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        int cnt = 0;
        for (Node cur = sentinel.next; cur.next != sentinel.next; cur = cur.next) {
            if (cnt == index) {
                return cur.data;
            }
        }
        return null;
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
            if (!this.get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }

}
