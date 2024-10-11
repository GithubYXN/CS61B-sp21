package deque;

import afu.org.checkerframework.checker.oigj.qual.O;

public class LinkedListDeque<T> implements Deque<T> {
    public class Node {
        public T data;
        public Node next;
        public Node prev;

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
        for (Node cur = sentinel.next; cur != null; cur = cur.next) {
            if (cnt == index) {
                return cur.data;
            }
            cnt += 1;
        }

        return null;
    }
}
