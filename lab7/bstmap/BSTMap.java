package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class Node {
        private K key;
        private V value;
        private Node left, right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }

        Node get(K k) {
            if (k == null) {
                return null;
            }
            if (k.equals(key)) {
                return this;
            }

            int cmp = k.compareTo(key);
            if (cmp < 0 && left != null) {
                return left.get(k);
            } else if (cmp > 0 && right != null) {
                return right.get(k);
            }
            return null;
        }
    }

    private Node root;
    private int size;

    public BSTMap() {
        root = null;
        size = 0;
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if (root == null) {
            return false;
        }
        return root.get(key) != null;
    }

    @Override
    public V get(K key) {
        if (root == null) {
            return null;
        }
        Node target = root.get(key);
        if (target == null) {
            return null;
        }
        return target.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }

    private Node put(Node n, K k, V v) {
        if (n == null) {
            size += 1;
            return new Node(k, v);
        }
        int cmp = k.compareTo(n.key);
        if (cmp < 0) {
            n.left = put(n.left, k, v);
        } else if (cmp > 0) {
            n.right = put(n.right, k, v);
        } else {
            n.value = v;
        }
        return n;
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }
}
