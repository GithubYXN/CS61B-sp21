package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int size;
    private double maxLoad;
    private int capacity;

    private static final int INIT_SIZE = 16;
    private static final double LOAD_FACTOR = 0.75;

    /** Constructors */
    public MyHashMap() {
        this.capacity = INIT_SIZE;
        this.buckets = new Collection[capacity];
        init(capacity);
        this.size = 0;
        this.maxLoad = LOAD_FACTOR;
    }

    public MyHashMap(int initialSize) {
        this.capacity = initialSize;
        this.buckets = new Collection[capacity];
        init(capacity);
        this.size = 0;
        this.maxLoad = LOAD_FACTOR;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.capacity = initialSize;
        this.buckets = new Collection[capacity];
        init(capacity);
        this.size = 0;
        this.maxLoad = maxLoad;
    }

    private void init(int capacity) {
        for (int i = 0; i < capacity; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<Node>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        MyHashMap<K, V> table = new MyHashMap<>(tableSize);
        for (int i = 0; i < capacity; i++) {
            for (Node node : buckets[i]) {
                table.put(node.key, node.value);
            }
        }
        return table.buckets;
    }

    private int hash(K key, int capacity) {
        int h = key.hashCode();
        if (h < 0) {
            h = -h;
        }
        return h % capacity;
    }

    @Override
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            buckets[i] = createBucket();
        }
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        if (key == null) {
            return null;
        }
        int h = hash(key, capacity);
        for (Node node : buckets[h]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            return;
        }
        int h = hash(key, capacity);
        if (containsKey(key)) {
            for (Node node : buckets[h]) {
                if (node.key.equals(key)) {
                    node.value = value;
                    return;
                }
            }
        }
        buckets[h].add(createNode(key, value));
        size += 1;

        double loadFactor = size / (double) capacity;
        if (loadFactor > maxLoad) {
            int newCapacity = capacity + (capacity >> 1);
            resize(newCapacity);
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<K>();
        for (K key : this) {
            keys.add(key);
        }
        return keys;
    }

    @Override
    public V remove(K key) {
        return remove(key, get(key));
    }

    @Override
    public V remove(K key, V value) {
        if (key == null || !containsKey(key)) {
            return null;
        }
        int h = hash(key, capacity);
        for (Node node : buckets[h]) {
            if (node.key.equals(key)) {
                buckets[h].remove(node);
            }
        }
        size -= 1;
        return value;
    }

    @Override
    public Iterator<K> iterator() {
        return new myHMIterator();
    }

    private void resize(int newCapacity) {
        buckets = createTable(newCapacity);
        capacity = newCapacity;
    }

    public class myHMIterator implements Iterator<K> {
        private int idx;
        private Object[] keys;

        public myHMIterator() {
            keys = new Object[size];
            init();
            idx = 0;
        }

        private void init() {
            idx = 0;
            for (int i = 0; i < capacity; i++) {
                for (Node node : buckets[i]) {
                    keys[idx++] = node.key;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return idx < size;
        }

        @Override
        public K next() {
            return (K) keys[idx++];
        }
    }
}
