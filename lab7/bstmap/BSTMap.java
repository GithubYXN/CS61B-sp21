package bstmap;

import java.util.HashSet;
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
        return new BSTIter();
    }

    public class BSTIter implements Iterator<K> {
        private Object[] items;
        private int idx = 0;

        public BSTIter() {
            items = new Object[size];
            init(root);
            idx = 0;
        }

        private void init(Node root) {
            if (root == null) {
                return;
            }
            init(root.left);
            items[idx++] = root.key;
            init(root.right);
        }

        public boolean hasNext() {
            return idx < size;
        }

        public K next() {
            return (K) items[idx++];
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        keySet(set, root);
        return set;
    }

    private void keySet(Set<K> set, Node n) {
        if (n == null) {
            return;
        }
        keySet(set, n.left);
        set.add(n.key);
        keySet(set, n.right);
    }

    @Override
    public V remove(K key) {
        Node n = root.get(key);
        if (n == null) {
            return null;
        }
        root = remove(key, root);
        size -= 1;
        return n.value;
    }

    @Override
    public V remove(K key, V value) {
        Node n = root.get(key);
        if (n == null || n.value != value) {
            return null;
        }
        root = remove(key, root);
        size -= 1;
        return n.value;
    }

    private Node remove(K key, Node n) {
        if (n == null) {
            return null;
        }
        int cmp = key.compareTo(n.key);
        if (cmp < 0) {
            n.left = remove(key, n.left);
        } else if (cmp > 0) {
            n.right = remove(key, n.right);
        } else {
            if (n.left == null) {
                return n.right;
            } else if (n.right == null) {
                return n.left;
            } else {
                Node leftMax = findLeftMax(n);
                n.value = leftMax.value;
                n.key = leftMax.key;
                n.left = remove(leftMax.key, n.left);
            }
        }
        return n;
    }

    private Node findLeftMax(Node root) {
        Node maxNode = root.left;
        while (maxNode.right != null) {
            maxNode = maxNode.right;
        }
        return maxNode;
    }

    private void inorderTraversal(Node n) {
        if (n == null) {
            return;
        }
        inorderTraversal(n.left);
        System.out.print(n.key + " ");
        inorderTraversal(n.right);
    }

    public void printInOrder() {
        inorderTraversal(root);
        System.out.println();
    }
}
