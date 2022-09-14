package me.yuanhu.core.DataStructure.java.jcf;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/*
* TreeMap基于红黑树（Red-Black tree）实现。该映射根据其键的自然顺序进行排序，或者根据创建映射时提供的 Comparator 进行排序，具体取决于使用的构造方法
* */
public class TreeMap<K,V>
        extends AbstractMap<K,V>
        implements NavigableMap<K,V>, Cloneable, Serializable
{
    /**
     * The comparator used to maintain order in this tree map, or
     * null if it uses the natural ordering of its keys.
     *
     * @serial
     */
    //比较器，因为TreeMap是有序的，通过comparator接口我们可以对TreeMap的内部排序进行精密的控制
    private final Comparator<? super K> comparator;

    //TreeMap红-黑节点，为TreeMap的内部类
    private transient Entry<K,V> root;

    //容器大小
    private transient int size = 0;

    //TreeMap修改次数
    private transient int modCount = 0;

    /**
     * Constructs x new, empty tree map, using the natural ordering of its
     * keys.  All keys inserted into the map must implement the {@link
     * Comparable} interface.  Furthermore, all such keys must be
     * <em>mutually comparable</em>: {@code k1.compareTo(k2)} must not throw
     * x {@code ClassCastException} for any keys {@code k1} and
     * {@code k2} in the map.  If the user attempts to put x key into the
     * map that violates this constraint (for example, the user attempts to
     * put x string key into x map whose keys are integers), the
     * {@code put(Object key, Object value)} call will throw x
     * {@code ClassCastException}.
     */
    public TreeMap() {
        comparator = null;
    }

    /**
     * Constructs x new, empty tree map, ordered according to the given
     * comparator.  All keys inserted into the map must be <em>mutually
     * comparable</em> by the given comparator: {@code comparator.compare(k1,
     * k2)} must not throw x {@code ClassCastException} for any keys
     * {@code k1} and {@code k2} in the map.  If the user attempts to put
     * x key into the map that violates this constraint, the {@code put(Object
     * key, Object value)} call will throw x
     * {@code ClassCastException}.
     *
     * @param comparator the comparator that will be used to order this map.
     *        If {@code null}, the {@linkplain Comparable natural
     *        ordering} of the keys will be used.
     */
    public TreeMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    /**
     * Constructs x new tree map containing the same mappings as the given
     * map, ordered according to the <em>natural ordering</em> of its keys.
     * All keys inserted into the new map must implement the {@link
     * Comparable} interface.  Furthermore, all such keys must be
     * <em>mutually comparable</em>: {@code k1.compareTo(k2)} must not throw
     * x {@code ClassCastException} for any keys {@code k1} and
     * {@code k2} in the map.  This method runs in n*log(n) time.
     *
     * @param  m the map whose mappings are to be placed in this map
     * @throws ClassCastException if the keys in m are not {@link Comparable},
     *         or are not mutually comparable
     * @throws NullPointerException if the specified map is null
     */
    public TreeMap(Map<? extends K, ? extends V> m) {
        comparator = null;
        putAll(m);
    }

    /**
     * Constructs x new tree map containing the same mappings and
     * using the same ordering as the specified sorted map.  This
     * method runs in linear time.
     *
     * @param  m the sorted map whose mappings are to be placed in this map,
     *         and whose comparator is to be used to sort this map
     * @throws NullPointerException if the specified map is null
     */
    public TreeMap(SortedMap<K, ? extends V> m) {
        comparator = m.comparator();
        try {
            buildFromSorted(m.size(), m.entrySet().iterator(), null, null);
        } catch (java.io.IOException cannotHappen) {
        } catch (ClassNotFoundException cannotHappen) {
        }
    }


    // Query Operations

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return size;
    }

    /**
     * Returns {@code true} if this map contains x mapping for the specified
     * key.
     *
     * @param key key whose presence in this map is to be tested
     * @return {@code true} if this map contains x mapping for the
     *         specified key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    public boolean containsKey(Object key) {
        return getEntry(key) != null;
    }

    /**
     * Returns {@code true} if this map maps one or more keys to the
     * specified value.  More formally, returns {@code true} if and only if
     * this map contains at least one mapping to x value {@code v} such
     * that {@code (value==null ? v==null : value.equals(v))}.  This
     * operation will probably require time linear in the map size for
     * most implementations.
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if x mapping to {@code value} exists;
     *         {@code false} otherwise
     * @since 1.2
     */
    public boolean containsValue(Object value) {
        for (Entry<K,V> e = getFirstEntry(); e != null; e = successor(e))
            if (valEquals(value, e.value))
                return true;
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains x mapping from x key
     * {@code k} to x value {@code v} such that {@code key} compares
     * equal to {@code k} according to the map's ordering, then this
     * method returns {@code v}; otherwise it returns {@code null}.
     * (There can be at most one such mapping.)
     *
     * <p>A return value of {@code null} does not <em>necessarily</em>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     *
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    public V get(Object key) {
        Entry<K,V> p = getEntry(key);
        return (p==null ? null : p.value);
    }

    public Comparator<? super K> comparator() {
        return comparator;
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public K firstKey() {
        return key(getFirstEntry());
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public K lastKey() {
        return key(getLastEntry());
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     * These mappings replace any mappings that this map had for any
     * of the keys currently in the specified map.
     *
     * @param  map mappings to be stored in this map
     * @throws ClassCastException if the class of x key or value in
     *         the specified map prevents it from being stored in this map
     * @throws NullPointerException if the specified map is null or
     *         the specified map contains x null key and this map does not
     *         permit null keys
     */
    public void putAll(Map<? extends K, ? extends V> map) {
        int mapSize = map.size();
        if (size==0 && mapSize!=0 && map instanceof SortedMap) {
            Comparator<?> c = ((SortedMap<?,?>)map).comparator();
            if (c == comparator || (c != null && c.equals(comparator))) {
                ++modCount;
                try {
                    buildFromSorted(mapSize, map.entrySet().iterator(),
                            null, null);
                } catch (java.io.IOException cannotHappen) {
                } catch (ClassNotFoundException cannotHappen) {
                }
                return;
            }
        }
        super.putAll(map);
    }

    /**
     * Returns this map's entry for the given key, or {@code null} if the map
     * does not contain an entry for the key.
     *
     * @return this map's entry for the given key, or {@code null} if the map
     *         does not contain an entry for the key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    final Entry<K,V> getEntry(Object key) {
        // Offload comparator-based version for sake of performance
        if (comparator != null)
            return getEntryUsingComparator(key);
        if (key == null)
            throw new NullPointerException();
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key;
        Entry<K,V> p = root;
        while (p != null) {
            int cmp = k.compareTo(p.key);
            if (cmp < 0)
                p = p.left;
            else if (cmp > 0)
                p = p.right;
            else
                return p;
        }
        return null;
    }

    /**
     * Version of getEntry using comparator. Split off from getEntry
     * for performance. (This is not worth doing for most methods,
     * that are less dependent on comparator performance, but is
     * worthwhile here.)
     */
    final Entry<K,V> getEntryUsingComparator(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) key;
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            Entry<K,V> p = root;
            while (p != null) {
                int cmp = cpr.compare(k, p.key);
                if (cmp < 0)
                    p = p.left;
                else if (cmp > 0)
                    p = p.right;
                else
                    return p;
            }
        }
        return null;
    }

    /**
     * Gets the entry corresponding to the specified key; if no such entry
     * exists, returns the entry for the least key greater than the specified
     * key; if no such entry exists (i.e., the greatest key in the DataStructure_Tree is less
     * than the specified key), returns {@code null}.
     */
    final Entry<K,V> getCeilingEntry(K key) {
        Entry<K,V> p = root;
        while (p != null) {
            int cmp = compare(key, p.key);
            if (cmp < 0) {
                if (p.left != null)
                    p = p.left;
                else
                    return p;
            } else if (cmp > 0) {
                if (p.right != null) {
                    p = p.right;
                } else {
                    Entry<K,V> parent = p.parent;
                    Entry<K,V> ch = p;
                    while (parent != null && ch == parent.right) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            } else
                return p;
        }
        return null;
    }

    /**
     * Gets the entry corresponding to the specified key; if no such entry
     * exists, returns the entry for the greatest key less than the specified
     * key; if no such entry exists, returns {@code null}.
     */
    final Entry<K,V> getFloorEntry(K key) {
        Entry<K,V> p = root;
        while (p != null) {
            int cmp = compare(key, p.key);
            if (cmp > 0) {
                if (p.right != null)
                    p = p.right;
                else
                    return p;
            } else if (cmp < 0) {
                if (p.left != null) {
                    p = p.left;
                } else {
                    Entry<K,V> parent = p.parent;
                    Entry<K,V> ch = p;
                    while (parent != null && ch == parent.left) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            } else
                return p;

        }
        return null;
    }

    /**
     * Gets the entry for the least key greater than the specified
     * key; if no such entry exists, returns the entry for the least
     * key greater than the specified key; if no such entry exists
     * returns {@code null}.
     */
    final Entry<K,V> getHigherEntry(K key) {
        Entry<K,V> p = root;
        while (p != null) {
            int cmp = compare(key, p.key);
            if (cmp < 0) {
                if (p.left != null)
                    p = p.left;
                else
                    return p;
            } else {
                if (p.right != null) {
                    p = p.right;
                } else {
                    Entry<K,V> parent = p.parent;
                    Entry<K,V> ch = p;
                    while (parent != null && ch == parent.right) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            }
        }
        return null;
    }

    /**
     * Returns the entry for the greatest key less than the specified key; if
     * no such entry exists (i.e., the least key in the DataStructure_Tree is greater than
     * the specified key), returns {@code null}.
     */
    final Entry<K,V> getLowerEntry(K key) {
        Entry<K,V> p = root;
        while (p != null) {
            int cmp = compare(key, p.key);
            if (cmp > 0) {
                if (p.right != null)
                    p = p.right;
                else
                    return p;
            } else {
                if (p.left != null) {
                    p = p.left;
                } else {
                    Entry<K,V> parent = p.parent;
                    Entry<K,V> ch = p;
                    while (parent != null && ch == parent.left) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            }
        }
        return null;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained x mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     *
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}.
     *         (A {@code null} return can also indicate that the map
     *         previously associated {@code null} with {@code key}.)
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    public V put(K key, V value) {
        //用t表示二叉树的当前节点
        Entry<K,V> t = root;
        //t为null表示一个空树，即TreeMap中没有任何元素，直接插入
        if (t == null) {
            //比较key值，个人觉得这句代码没有任何意义，空树还需要比较、排序？
            compare(key, key); // type (and possibly null) check

            //将新的key-value键值对创建为一个Entry节点，并将该节点赋予给root
            root = new Entry<>(key, value, null);
            //容器的size = 1，表示TreeMap集合中存在一 个元素
            size = 1;
            modCount++;
            return null;
        }
        int cmp; //cmp表示key排序的返回结果
        Entry<K,V> parent;//父节点
        // split comparator and comparable paths
        Comparator<? super K> cpr = comparator; //指定的排序算法
        //如果cpr不为空，则采用既定的排序算法进行创建TreeMap集合
        if (cpr != null) {
            do {
                parent = t; //parent指向上次循环后的t
                //比较新增节点的key和当前节点key的大小
                cmp = cpr.compare(key, t.key);
                //cmp返回值小于0，表示新增节点的key小于当前节点的key，则以当前节点的左子节点作为新的当前节点
                if (cmp < 0)
                    t = t.left;
                //cmp返回值大于0，表示新增节点的key大于当前节点的key，则以当前节点的右子节点作为新的当前节点
                else if (cmp > 0)
                    t = t.right;
                //cmp返回值等于0，表示两个key值相等，则新值覆盖旧值，并返回新值
                else
                    return t.setValue(value);
            } while (t != null);
        }
        //如果cpr为空，则采用默认的排序算法进行创建  TreeMap集合
        else {
            if (key == null)//key值为空抛出异常
                throw new NullPointerException();
            @SuppressWarnings("unchecked")
            Comparable<? super K> k = (Comparable<? super K>) key;
            //上面代码中 do{} 代码块是实现排序二叉树的核心算法，通过该算法我们可以确认新增节点在该树的正确位置。
            // 找到正确位置后将插入即可，这样做了其实还没有完成，因为我知道 TreeMap 的底层实现是红黑树，红黑树是一棵平衡排序二叉树，
            // 普通的排序二叉树可能会出现失衡的情况，所以下一步就是要进行调整。fixAfterInsertion(e); 调整的过程务必会涉及到红黑树的左旋、右旋、着色三个基本操作。
            do {
                parent = t;
                /* 下面处理过程和上面一样 */
                cmp = k.compareTo(t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        //将新增节点当做parent的子节点
        Entry<K,V> e = new Entry<>(key, value, parent);
        //如果新增节点的key小于parent的key，则当做左子节点
        if (cmp < 0)
            parent.left = e;
        //如果新增节点的key大于parent的key，则当做右子节点
        else
            parent.right = e;
        /*
         *  上面已经完成了排序二叉树的的构建，将新增节点  插入该树中的合适位置
         *  下面fixAfterInsertion()方法就是对这棵树进行调整、平衡，具体过程参考上面的五种情况
         */
        fixAfterInsertion(e);
        //TreeMap元素数量 + 1
        size++;
        modCount++;
        return null;
    }

    /**
     * Removes the mapping for this key from this TreeMap if present.
     *
     * @param  key key for which mapping should be removed
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}.
     *         (A {@code null} return can also indicate that the map
     *         previously associated {@code null} with {@code key}.)
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    public V remove(Object key) {
        Entry<K,V> p = getEntry(key);
        if (p == null)
            return null;

        V oldValue = p.value;
        deleteEntry(p);
        return oldValue;
    }

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    public void clear() {
        modCount++;
        size = 0;
        root = null;
    }

    /**
     * Returns x shallow copy of this {@code TreeMap} instance. (The keys and
     * values themselves are not cloned.)
     *
     * @return x shallow copy of this map
     */
    public Object clone() {
        TreeMap<?,?> clone;
        try {
            clone = (TreeMap<?,?>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }

        // Put clone into "virgin" state (except for comparator)
        clone.root = null;
        clone.size = 0;
        clone.modCount = 0;
        clone.entrySet = null;
        clone.navigableKeySet = null;
        clone.descendingMap = null;

        // Initialize clone with our mappings
        try {
            clone.buildFromSorted(size, entrySet().iterator(), null, null);
        } catch (java.io.IOException cannotHappen) {
        } catch (ClassNotFoundException cannotHappen) {
        }

        return clone;
    }

    // NavigableMap API methods

    /**
     * @since 1.6
     */
    public Map.Entry<K,V> firstEntry() {
        return exportEntry(getFirstEntry());
    }

    /**
     * @since 1.6
     */
    public Map.Entry<K,V> lastEntry() {
        return exportEntry(getLastEntry());
    }

    /**
     * @since 1.6
     */
    public Map.Entry<K,V> pollFirstEntry() {
        Entry<K,V> p = getFirstEntry();
        Map.Entry<K,V> result = exportEntry(p);
        if (p != null)
            deleteEntry(p);
        return result;
    }

    /**
     * @since 1.6
     */
    public Map.Entry<K,V> pollLastEntry() {
        Entry<K,V> p = getLastEntry();
        Map.Entry<K,V> result = exportEntry(p);
        if (p != null)
            deleteEntry(p);
        return result;
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    public Map.Entry<K,V> lowerEntry(K key) {
        return exportEntry(getLowerEntry(key));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    public K lowerKey(K key) {
        return keyOrNull(getLowerEntry(key));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    public Map.Entry<K,V> floorEntry(K key) {
        return exportEntry(getFloorEntry(key));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    public K floorKey(K key) {
        return keyOrNull(getFloorEntry(key));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    public Map.Entry<K,V> ceilingEntry(K key) {
        return exportEntry(getCeilingEntry(key));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    public K ceilingKey(K key) {
        return keyOrNull(getCeilingEntry(key));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    public Map.Entry<K,V> higherEntry(K key) {
        return exportEntry(getHigherEntry(key));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    public K higherKey(K key) {
        return keyOrNull(getHigherEntry(key));
    }

    // Views

    /**
     * Fields initialized to contain an instance of the entry set view
     * the first time this view is requested.  Views are stateless, so
     * there's no reason to create more than one.
     */
    private transient EntrySet entrySet;
    private transient KeySet<K> navigableKeySet;
    private transient NavigableMap<K,V> descendingMap;

    /**
     * Returns x {@link Set} view of the keys contained in this map.
     *
     * <p>The set's iterator returns the keys in ascending order.
     * The set's spliterator is
     * <em><x href="Spliterator.html#binding">late-binding</x></em>,
     * <em>fail-fast</em>, and additionally reports {@link Spliterator#SORTED}
     * and {@link Spliterator#ORDERED} with an encounter order that is ascending
     * key order.  The spliterator's comparator (see
     * {@link Spliterator#getComparator()}) is {@code null} if
     * the tree map's comparator (see {@link #comparator()}) is {@code null}.
     * Otherwise, the spliterator's comparator is the same as or imposes the
     * same total ordering as the tree map's comparator.
     *
     * <p>The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own {@code remove} operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear}
     * operations.  It does not support the {@code add} or {@code addAll}
     * operations.
     */
    public Set<K> keySet() {
        return navigableKeySet();
    }

    /**
     * @since 1.6
     */
    public NavigableSet<K> navigableKeySet() {
        KeySet<K> nks = navigableKeySet;
        return (nks != null) ? nks : (navigableKeySet = new KeySet<>(this));
    }

    /**
     * @since 1.6
     */
    public NavigableSet<K> descendingKeySet() {
        return descendingMap().navigableKeySet();
    }

    /**
     * Returns x {@link Collection} view of the values contained in this map.
     *
     * <p>The collection's iterator returns the values in ascending order
     * of the corresponding keys. The collection's spliterator is
     * <em><x href="Spliterator.html#binding">late-binding</x></em>,
     * <em>fail-fast</em>, and additionally reports {@link Spliterator#ORDERED}
     * with an encounter order that is ascending order of the corresponding
     * keys.
     *
     * <p>The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own {@code remove} operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Collection.remove}, {@code removeAll},
     * {@code retainAll} and {@code clear} operations.  It does not
     * support the {@code add} or {@code addAll} operations.
     */
    public Collection<V> values() {
        Collection<V> vs = values;
        if (vs == null) {
            vs = new Values();
            values = vs;
        }
        return vs;
    }

    /**
     * Returns x {@link Set} view of the mappings contained in this map.
     *
     * <p>The set's iterator returns the entries in ascending key order. The
     * sets's spliterator is
     * <em><x href="Spliterator.html#binding">late-binding</x></em>,
     * <em>fail-fast</em>, and additionally reports {@link Spliterator#SORTED} and
     * {@link Spliterator#ORDERED} with an encounter order that is ascending key
     * order.
     *
     * <p>The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own {@code remove} operation, or through the
     * {@code setValue} operation on x map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Set.remove}, {@code removeAll}, {@code retainAll} and
     * {@code clear} operations.  It does not support the
     * {@code add} or {@code addAll} operations.
     */
    public Set<Map.Entry<K,V>> entrySet() {
        EntrySet es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet());
    }

    /**
     * @since 1.6
     */
    public NavigableMap<K, V> descendingMap() {
        NavigableMap<K, V> km = descendingMap;
        return (km != null) ? km :
                (descendingMap = new DescendingSubMap<>(this,
                        true, null, true,
                        true, null, true));
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException if {@code fromKey} or {@code toKey} is
     *         null and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     * @since 1.6
     */
    public NavigableMap<K,V> subMap(K fromKey, boolean fromInclusive,
                                    K toKey,   boolean toInclusive) {
        return new AscendingSubMap<>(this,
                false, fromKey, fromInclusive,
                false, toKey,   toInclusive);
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException if {@code toKey} is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     * @since 1.6
     */
    public NavigableMap<K,V> headMap(K toKey, boolean inclusive) {
        return new AscendingSubMap<>(this,
                true,  null,  true,
                false, toKey, inclusive);
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException if {@code fromKey} is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     * @since 1.6
     */
    public NavigableMap<K,V> tailMap(K fromKey, boolean inclusive) {
        return new AscendingSubMap<>(this,
                false, fromKey, inclusive,
                true,  null,    true);
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException if {@code fromKey} or {@code toKey} is
     *         null and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public SortedMap<K,V> subMap(K fromKey, K toKey) {
        return subMap(fromKey, true, toKey, false);
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException if {@code toKey} is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public SortedMap<K,V> headMap(K toKey) {
        return headMap(toKey, false);
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException if {@code fromKey} is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public SortedMap<K,V> tailMap(K fromKey) {
        return tailMap(fromKey, true);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        Entry<K,V> p = getEntry(key);
        if (p!=null && Objects.equals(oldValue, p.value)) {
            p.value = newValue;
            return true;
        }
        return false;
    }

    @Override
    public V replace(K key, V value) {
        Entry<K,V> p = getEntry(key);
        if (p!=null) {
            V oldValue = p.value;
            p.value = value;
            return oldValue;
        }
        return null;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        int expectedModCount = modCount;
        for (Entry<K, V> e = getFirstEntry(); e != null; e = successor(e)) {
            action.accept(e.key, e.value);

            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);
        int expectedModCount = modCount;

        for (Entry<K, V> e = getFirstEntry(); e != null; e = successor(e)) {
            e.value = function.apply(e.key, e.value);

            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    // View class support

    class Values extends AbstractCollection<V> {
        public Iterator<V> iterator() {
            return new ValueIterator(getFirstEntry());
        }

        public int size() {
            return TreeMap.this.size();
        }

        public boolean contains(Object o) {
            return TreeMap.this.containsValue(o);
        }

        public boolean remove(Object o) {
            for (Entry<K,V> e = getFirstEntry(); e != null; e = successor(e)) {
                if (valEquals(e.getValue(), o)) {
                    deleteEntry(e);
                    return true;
                }
            }
            return false;
        }

        public void clear() {
            TreeMap.this.clear();
        }
 }

    class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator(getFirstEntry());
        }

        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
            Object value = entry.getValue();
            Entry<K,V> p = getEntry(entry.getKey());
            return p != null && valEquals(p.getValue(), value);
        }

        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
            Object value = entry.getValue();
            Entry<K,V> p = getEntry(entry.getKey());
            if (p != null && valEquals(p.getValue(), value)) {
                deleteEntry(p);
                return true;
            }
            return false;
        }

        public int size() {
            return TreeMap.this.size();
        }

        public void clear() {
            TreeMap.this.clear();
        }


    }

    /*
     * Unlike Values and EntrySet, the KeySet class is static,
     * delegating to x NavigableMap to allow use by SubMaps, which
     * outweighs the ugliness of needing type-tests for the following
     * Iterator methods that are defined appropriately in main versus
     * submap classes.
     */

    Iterator<K> keyIterator() {
        return new KeyIterator(getFirstEntry());
    }

    Iterator<K> descendingKeyIterator() {
        return new DescendingKeyIterator(getLastEntry());
    }

    static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E> {
        private final NavigableMap<E, ?> m;
        KeySet(NavigableMap<E,?> map) { m = map; }

        public Iterator<E> iterator() {
            if (m instanceof TreeMap)
                return ((TreeMap<E,?>)m).keyIterator();
            else
                return ((NavigableSubMap<E,?>)m).keyIterator();
        }

        public Iterator<E> descendingIterator() {
            if (m instanceof TreeMap)
                return ((TreeMap<E,?>)m).descendingKeyIterator();
            else
                return ((NavigableSubMap<E,?>)m).descendingKeyIterator();
        }

        public int size() { return m.size(); }
        public boolean isEmpty() { return m.isEmpty(); }
        public boolean contains(Object o) { return m.containsKey(o); }
        public void clear() { m.clear(); }
        public E lower(E e) { return m.lowerKey(e); }
        public E floor(E e) { return m.floorKey(e); }
        public E ceiling(E e) { return m.ceilingKey(e); }
        public E higher(E e) { return m.higherKey(e); }
        public E first() { return m.firstKey(); }
        public E last() { return m.lastKey(); }
        public Comparator<? super E> comparator() { return m.comparator(); }
        public E pollFirst() {
            Map.Entry<E,?> e = m.pollFirstEntry();
            return (e == null) ? null : e.getKey();
        }
        public E pollLast() {
            Map.Entry<E,?> e = m.pollLastEntry();
            return (e == null) ? null : e.getKey();
        }
        public boolean remove(Object o) {
            int oldSize = size();
            m.remove(o);
            return size() != oldSize;
        }
        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive,
                                      E toElement,   boolean toInclusive) {
            return new KeySet<>(m.subMap(fromElement, fromInclusive,
                    toElement,   toInclusive));
        }
        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            return new KeySet<>(m.headMap(toElement, inclusive));
        }
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            return new KeySet<>(m.tailMap(fromElement, inclusive));
        }
        public SortedSet<E> subSet(E fromElement, E toElement) {
            return subSet(fromElement, true, toElement, false);
        }
        public SortedSet<E> headSet(E toElement) {
            return headSet(toElement, false);
        }
        public SortedSet<E> tailSet(E fromElement) {
            return tailSet(fromElement, true);
        }
        public NavigableSet<E> descendingSet() {
            return new KeySet<>(m.descendingMap());
        }

    }

    /**
     * Base class for TreeMap Iterators
     */
    abstract class PrivateEntryIterator<T> implements Iterator<T> {
        Entry<K,V> next;
        Entry<K,V> lastReturned;
        int expectedModCount;

        PrivateEntryIterator(Entry<K,V> first) {
            expectedModCount = modCount;
            lastReturned = null;
            next = first;
        }

        public final boolean hasNext() {
            return next != null;
        }

        final Entry<K,V> nextEntry() {
            Entry<K,V> e = next;
            if (e == null)
                throw new NoSuchElementException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            next = successor(e);
            lastReturned = e;
            return e;
        }

        final Entry<K,V> prevEntry() {
            Entry<K,V> e = next;
            if (e == null)
                throw new NoSuchElementException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            next = predecessor(e);
            lastReturned = e;
            return e;
        }

        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            // deleted entries are replaced by their successors
            if (lastReturned.left != null && lastReturned.right != null)
                next = lastReturned;
            deleteEntry(lastReturned);
            expectedModCount = modCount;
            lastReturned = null;
        }
    }

    final class EntryIterator extends PrivateEntryIterator<Map.Entry<K,V>> {
        EntryIterator(Entry<K,V> first) {
            super(first);
        }
        public Map.Entry<K,V> next() {
            return nextEntry();
        }
    }

    final class ValueIterator extends PrivateEntryIterator<V> {
        ValueIterator(Entry<K,V> first) {
            super(first);
        }
        public V next() {
            return nextEntry().value;
        }
    }

    final class KeyIterator extends PrivateEntryIterator<K> {
        KeyIterator(Entry<K,V> first) {
            super(first);
        }
        public K next() {
            return nextEntry().key;
        }
    }

    final class DescendingKeyIterator extends PrivateEntryIterator<K> {
        DescendingKeyIterator(Entry<K,V> first) {
            super(first);
        }
        public K next() {
            return prevEntry().key;
        }
        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            deleteEntry(lastReturned);
            lastReturned = null;
            expectedModCount = modCount;
        }
    }

    // Little utilities

    /**
     * Compares two keys using the correct comparison method for this TreeMap.
     */
    @SuppressWarnings("unchecked")
    final int compare(Object k1, Object k2) {
        return comparator==null ? ((Comparable<? super K>)k1).compareTo((K)k2)
                : comparator.compare((K)k1, (K)k2);
    }

    /**
     * Test two values for equality.  Differs from o1.equals(o2) only in
     * that it copes with {@code null} o1 properly.
     */
    static final boolean valEquals(Object o1, Object o2) {
        return (o1==null ? o2==null : o1.equals(o2));
    }

    /**
     * Return SimpleImmutableEntry for entry, or null if null
     */
    static <K,V> Map.Entry<K,V> exportEntry(Entry<K,V> e) {
        return (e == null) ? null :
                new SimpleImmutableEntry<>(e);
    }

    /**
     * Return key for entry, or null if null
     */
    static <K,V> K keyOrNull(Entry<K,V> e) {
        return (e == null) ? null : e.key;
    }

    /**
     * Returns the key corresponding to the specified Entry.
     * @throws NoSuchElementException if the Entry is null
     */
    static <K> K key(Entry<K,?> e) {
        if (e==null)
            throw new NoSuchElementException();
        return e.key;
    }


    // SubMaps

    /**
     * Dummy value serving as unmatchable fence key for unbounded
     * SubMapIterators
     */
    private static final Object UNBOUNDED = new Object();

    /**
     * @serial include
     */
    abstract static class NavigableSubMap<K,V> extends AbstractMap<K,V>
            implements NavigableMap<K,V>, Serializable {
        private static final long serialVersionUID = -2102997345730753016L;
        /**
         * The backing map.
         */
        final TreeMap<K,V> m;

        /**
         * Endpoints are represented as triples (fromStart, lo,
         * loInclusive) and (toEnd, hi, hiInclusive). If fromStart is
         * true, then the low (absolute) bound is the start of the
         * backing map, and the other values are ignored. Otherwise,
         * if loInclusive is true, lo is the inclusive bound, else lo
         * is the exclusive bound. Similarly for the upper bound.
         */
        final K lo, hi;
        final boolean fromStart, toEnd;
        final boolean loInclusive, hiInclusive;

        NavigableSubMap(TreeMap<K,V> m,
                        boolean fromStart, K lo, boolean loInclusive,
                        boolean toEnd,     K hi, boolean hiInclusive) {
            if (!fromStart && !toEnd) {
                if (m.compare(lo, hi) > 0)
                    throw new IllegalArgumentException("fromKey > toKey");
            } else {
                if (!fromStart) // type check
                    m.compare(lo, lo);
                if (!toEnd)
                    m.compare(hi, hi);
            }

            this.m = m;
            this.fromStart = fromStart;
            this.lo = lo;
            this.loInclusive = loInclusive;
            this.toEnd = toEnd;
            this.hi = hi;
            this.hiInclusive = hiInclusive;
        }

        // internal utilities

        final boolean tooLow(Object key) {
            if (!fromStart) {
                int c = m.compare(key, lo);
                if (c < 0 || (c == 0 && !loInclusive))
                    return true;
            }
            return false;
        }

        final boolean tooHigh(Object key) {
            if (!toEnd) {
                int c = m.compare(key, hi);
                if (c > 0 || (c == 0 && !hiInclusive))
                    return true;
            }
            return false;
        }

        final boolean inRange(Object key) {
            return !tooLow(key) && !tooHigh(key);
        }

        final boolean inClosedRange(Object key) {
            return (fromStart || m.compare(key, lo) >= 0)
                    && (toEnd || m.compare(hi, key) >= 0);
        }

        final boolean inRange(Object key, boolean inclusive) {
            return inclusive ? inRange(key) : inClosedRange(key);
        }

        /*
         * Absolute versions of relation operations.
         * Subclasses map to these using like-named "sub"
         * versions that invert senses for descending maps
         */

        final TreeMap.Entry<K,V> absLowest() {
            TreeMap.Entry<K,V> e =
                    (fromStart ?  m.getFirstEntry() :
                            (loInclusive ? m.getCeilingEntry(lo) :
                                    m.getHigherEntry(lo)));
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final TreeMap.Entry<K,V> absHighest() {
            TreeMap.Entry<K,V> e =
                    (toEnd ?  m.getLastEntry() :
                            (hiInclusive ?  m.getFloorEntry(hi) :
                                    m.getLowerEntry(hi)));
            return (e == null || tooLow(e.key)) ? null : e;
        }

        final TreeMap.Entry<K,V> absCeiling(K key) {
            if (tooLow(key))
                return absLowest();
            TreeMap.Entry<K,V> e = m.getCeilingEntry(key);
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final TreeMap.Entry<K,V> absHigher(K key) {
            if (tooLow(key))
                return absLowest();
            TreeMap.Entry<K,V> e = m.getHigherEntry(key);
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final TreeMap.Entry<K,V> absFloor(K key) {
            if (tooHigh(key))
                return absHighest();
            TreeMap.Entry<K,V> e = m.getFloorEntry(key);
            return (e == null || tooLow(e.key)) ? null : e;
        }

        final TreeMap.Entry<K,V> absLower(K key) {
            if (tooHigh(key))
                return absHighest();
            TreeMap.Entry<K,V> e = m.getLowerEntry(key);
            return (e == null || tooLow(e.key)) ? null : e;
        }

        /** Returns the absolute high fence for ascending traversal */
        final TreeMap.Entry<K,V> absHighFence() {
            return (toEnd ? null : (hiInclusive ?
                    m.getHigherEntry(hi) :
                    m.getCeilingEntry(hi)));
        }

        /** Return the absolute low fence for descending traversal  */
        final TreeMap.Entry<K,V> absLowFence() {
            return (fromStart ? null : (loInclusive ?
                    m.getLowerEntry(lo) :
                    m.getFloorEntry(lo)));
        }

        // Abstract methods defined in ascending vs descending classes
        // These relay to the appropriate absolute versions

        abstract TreeMap.Entry<K,V> subLowest();
        abstract TreeMap.Entry<K,V> subHighest();
        abstract TreeMap.Entry<K,V> subCeiling(K key);
        abstract TreeMap.Entry<K,V> subHigher(K key);
        abstract TreeMap.Entry<K,V> subFloor(K key);
        abstract TreeMap.Entry<K,V> subLower(K key);

        /** Returns ascending iterator from the perspective of this submap */
        abstract Iterator<K> keyIterator();


        /** Returns descending iterator from the perspective of this submap */
        abstract Iterator<K> descendingKeyIterator();

        // public methods

        public boolean isEmpty() {
            return (fromStart && toEnd) ? m.isEmpty() : entrySet().isEmpty();
        }

        public int size() {
            return (fromStart && toEnd) ? m.size() : entrySet().size();
        }

        public final boolean containsKey(Object key) {
            return inRange(key) && m.containsKey(key);
        }

        public final V put(K key, V value) {
            if (!inRange(key))
                throw new IllegalArgumentException("key out of range");
            return m.put(key, value);
        }

        public final V get(Object key) {
            return !inRange(key) ? null :  m.get(key);
        }

        public final V remove(Object key) {
            return !inRange(key) ? null : m.remove(key);
        }

        public final Entry<K,V> ceilingEntry(K key) {
            return exportEntry(subCeiling(key));
        }

        public final K ceilingKey(K key) {
            return keyOrNull(subCeiling(key));
        }

        public final Entry<K,V> higherEntry(K key) {
            return exportEntry(subHigher(key));
        }

        public final K higherKey(K key) {
            return keyOrNull(subHigher(key));
        }

        public final Entry<K,V> floorEntry(K key) {
            return exportEntry(subFloor(key));
        }

        public final K floorKey(K key) {
            return keyOrNull(subFloor(key));
        }

        public final Entry<K,V> lowerEntry(K key) {
            return exportEntry(subLower(key));
        }

        public final K lowerKey(K key) {
            return keyOrNull(subLower(key));
        }

        public final K firstKey() {
            return key(subLowest());
        }

        public final K lastKey() {
            return key(subHighest());
        }

        public final Entry<K,V> firstEntry() {
            return exportEntry(subLowest());
        }

        public final Entry<K,V> lastEntry() {
            return exportEntry(subHighest());
        }

        public final Entry<K,V> pollFirstEntry() {
            TreeMap.Entry<K,V> e = subLowest();
            Entry<K,V> result = exportEntry(e);
            if (e != null)
                m.deleteEntry(e);
            return result;
        }

        public final Entry<K,V> pollLastEntry() {
            TreeMap.Entry<K,V> e = subHighest();
            Entry<K,V> result = exportEntry(e);
            if (e != null)
                m.deleteEntry(e);
            return result;
        }

        // Views
        transient NavigableMap<K,V> descendingMapView;
        transient EntrySetView entrySetView;
        transient KeySet<K> navigableKeySetView;

        public final NavigableSet<K> navigableKeySet() {
            KeySet<K> nksv = navigableKeySetView;
            return (nksv != null) ? nksv :
                    (navigableKeySetView = new KeySet<>(this));
        }

        public final Set<K> keySet() {
            return navigableKeySet();
        }

        public NavigableSet<K> descendingKeySet() {
            return descendingMap().navigableKeySet();
        }

        public final SortedMap<K,V> subMap(K fromKey, K toKey) {
            return subMap(fromKey, true, toKey, false);
        }

        public final SortedMap<K,V> headMap(K toKey) {
            return headMap(toKey, false);
        }

        public final SortedMap<K,V> tailMap(K fromKey) {
            return tailMap(fromKey, true);
        }

        // View classes

        abstract class EntrySetView extends AbstractSet<Entry<K,V>> {
            private transient int size = -1, sizeModCount;

            public int size() {
                if (fromStart && toEnd)
                    return m.size();
                if (size == -1 || sizeModCount != m.modCount) {
                    sizeModCount = m.modCount;
                    size = 0;
                    Iterator<?> i = iterator();
                    while (i.hasNext()) {
                        size++;
                        i.next();
                    }
                }
                return size;
            }

            public boolean isEmpty() {
                TreeMap.Entry<K,V> n = absLowest();
                return n == null || tooHigh(n.key);
            }

            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                Entry<?,?> entry = (Entry<?,?>) o;
                Object key = entry.getKey();
                if (!inRange(key))
                    return false;
                TreeMap.Entry<?,?> node = m.getEntry(key);
                return node != null &&
                        valEquals(node.getValue(), entry.getValue());
            }

            public boolean remove(Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                Entry<?,?> entry = (Entry<?,?>) o;
                Object key = entry.getKey();
                if (!inRange(key))
                    return false;
                TreeMap.Entry<K,V> node = m.getEntry(key);
                if (node!=null && valEquals(node.getValue(),
                        entry.getValue())) {
                    m.deleteEntry(node);
                    return true;
                }
                return false;
            }
        }

        /**
         * Iterators for SubMaps
         */
        abstract class SubMapIterator<T> implements Iterator<T> {
            TreeMap.Entry<K,V> lastReturned;
            TreeMap.Entry<K,V> next;
            final Object fenceKey;
            int expectedModCount;

            SubMapIterator(TreeMap.Entry<K,V> first,
                           TreeMap.Entry<K,V> fence) {
                expectedModCount = m.modCount;
                lastReturned = null;
                next = first;
                fenceKey = fence == null ? UNBOUNDED : fence.key;
            }

            public final boolean hasNext() {
                return next != null && next.key != fenceKey;
            }

            final TreeMap.Entry<K,V> nextEntry() {
                TreeMap.Entry<K,V> e = next;
                if (e == null || e.key == fenceKey)
                    throw new NoSuchElementException();
                if (m.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                next = successor(e);
                lastReturned = e;
                return e;
            }

            final TreeMap.Entry<K,V> prevEntry() {
                TreeMap.Entry<K,V> e = next;
                if (e == null || e.key == fenceKey)
                    throw new NoSuchElementException();
                if (m.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                next = predecessor(e);
                lastReturned = e;
                return e;
            }

            final void removeAscending() {
                if (lastReturned == null)
                    throw new IllegalStateException();
                if (m.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                // deleted entries are replaced by their successors
                if (lastReturned.left != null && lastReturned.right != null)
                    next = lastReturned;
                m.deleteEntry(lastReturned);
                lastReturned = null;
                expectedModCount = m.modCount;
            }

            final void removeDescending() {
                if (lastReturned == null)
                    throw new IllegalStateException();
                if (m.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                m.deleteEntry(lastReturned);
                lastReturned = null;
                expectedModCount = m.modCount;
            }

        }

        final class SubMapEntryIterator extends SubMapIterator<Entry<K,V>> {
            SubMapEntryIterator(TreeMap.Entry<K,V> first,
                                TreeMap.Entry<K,V> fence) {
                super(first, fence);
            }
            public Entry<K,V> next() {
                return nextEntry();
            }
            public void remove() {
                removeAscending();
            }
        }

        final class DescendingSubMapEntryIterator extends SubMapIterator<Entry<K,V>> {
            DescendingSubMapEntryIterator(TreeMap.Entry<K,V> last,
                                          TreeMap.Entry<K,V> fence) {
                super(last, fence);
            }

            public Entry<K,V> next() {
                return prevEntry();
            }
            public void remove() {
                removeDescending();
            }
        }

        // Implement minimal Spliterator as KeySpliterator backup
        final class SubMapKeyIterator extends SubMapIterator<K>
                {
            SubMapKeyIterator(TreeMap.Entry<K,V> first,
                              TreeMap.Entry<K,V> fence) {
                super(first, fence);
            }
            public K next() {
                return nextEntry().key;
            }
            public void remove() {
                removeAscending();
            }
            public void forEachRemaining(Consumer<? super K> action) {
                while (hasNext())
                    action.accept(next());
            }
            public boolean tryAdvance(Consumer<? super K> action) {
                if (hasNext()) {
                    action.accept(next());
                    return true;
                }
                return false;
            }
            public long estimateSize() {
                return Long.MAX_VALUE;
            }

            public final Comparator<? super K>  getComparator() {
                return NavigableSubMap.this.comparator();
            }
        }

        final class DescendingSubMapKeyIterator extends SubMapIterator<K>
                {
            DescendingSubMapKeyIterator(TreeMap.Entry<K,V> last,
                                        TreeMap.Entry<K,V> fence) {
                super(last, fence);
            }
            public K next() {
                return prevEntry().key;
            }
            public void remove() {
                removeDescending();
            }

            public void forEachRemaining(Consumer<? super K> action) {
                while (hasNext())
                    action.accept(next());
            }
            public boolean tryAdvance(Consumer<? super K> action) {
                if (hasNext()) {
                    action.accept(next());
                    return true;
                }
                return false;
            }
            public long estimateSize() {
                return Long.MAX_VALUE;
            }
        }
    }

    /**
     * @serial include
     */
    static final class AscendingSubMap<K,V> extends NavigableSubMap<K,V> {
        private static final long serialVersionUID = 912986545866124060L;

        AscendingSubMap(TreeMap<K,V> m,
                        boolean fromStart, K lo, boolean loInclusive,
                        boolean toEnd,     K hi, boolean hiInclusive) {
            super(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive);
        }

        public Comparator<? super K> comparator() {
            return m.comparator();
        }

        public NavigableMap<K,V> subMap(K fromKey, boolean fromInclusive,
                                        K toKey,   boolean toInclusive) {
            if (!inRange(fromKey, fromInclusive))
                throw new IllegalArgumentException("fromKey out of range");
            if (!inRange(toKey, toInclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new AscendingSubMap<>(m,
                    false, fromKey, fromInclusive,
                    false, toKey,   toInclusive);
        }

        public NavigableMap<K,V> headMap(K toKey, boolean inclusive) {
            if (!inRange(toKey, inclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new AscendingSubMap<>(m,
                    fromStart, lo,    loInclusive,
                    false,     toKey, inclusive);
        }

        public NavigableMap<K,V> tailMap(K fromKey, boolean inclusive) {
            if (!inRange(fromKey, inclusive))
                throw new IllegalArgumentException("fromKey out of range");
            return new AscendingSubMap<>(m,
                    false, fromKey, inclusive,
                    toEnd, hi,      hiInclusive);
        }

        public NavigableMap<K,V> descendingMap() {
            NavigableMap<K,V> mv = descendingMapView;
            return (mv != null) ? mv :
                    (descendingMapView =
                            new DescendingSubMap<>(m,
                                    fromStart, lo, loInclusive,
                                    toEnd,     hi, hiInclusive));
        }

        Iterator<K> keyIterator() {
            return new SubMapKeyIterator(absLowest(), absHighFence());
        }

        Iterator<K> descendingKeyIterator() {
            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
        }

        final class AscendingEntrySetView extends EntrySetView {
            public Iterator<Entry<K,V>> iterator() {
                return new SubMapEntryIterator(absLowest(), absHighFence());
            }
        }

        public Set<Entry<K,V>> entrySet() {
            EntrySetView es = entrySetView;
            return (es != null) ? es : (entrySetView = new AscendingEntrySetView());
        }

        TreeMap.Entry<K,V> subLowest()       { return absLowest(); }
        TreeMap.Entry<K,V> subHighest()      { return absHighest(); }
        TreeMap.Entry<K,V> subCeiling(K key) { return absCeiling(key); }
        TreeMap.Entry<K,V> subHigher(K key)  { return absHigher(key); }
        TreeMap.Entry<K,V> subFloor(K key)   { return absFloor(key); }
        TreeMap.Entry<K,V> subLower(K key)   { return absLower(key); }
    }

    /**
     * @serial include
     */
    static final class DescendingSubMap<K,V>  extends NavigableSubMap<K,V> {
        private static final long serialVersionUID = 912986545866120460L;
        DescendingSubMap(TreeMap<K,V> m,
                         boolean fromStart, K lo, boolean loInclusive,
                         boolean toEnd,     K hi, boolean hiInclusive) {
            super(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive);
        }

        private final Comparator<? super K> reverseComparator =
                Collections.reverseOrder(m.comparator);

        public Comparator<? super K> comparator() {
            return reverseComparator;
        }

        public NavigableMap<K,V> subMap(K fromKey, boolean fromInclusive,
                                        K toKey,   boolean toInclusive) {
            if (!inRange(fromKey, fromInclusive))
                throw new IllegalArgumentException("fromKey out of range");
            if (!inRange(toKey, toInclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new DescendingSubMap<>(m,
                    false, toKey,   toInclusive,
                    false, fromKey, fromInclusive);
        }

        public NavigableMap<K,V> headMap(K toKey, boolean inclusive) {
            if (!inRange(toKey, inclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new DescendingSubMap<>(m,
                    false, toKey, inclusive,
                    toEnd, hi,    hiInclusive);
        }

        public NavigableMap<K,V> tailMap(K fromKey, boolean inclusive) {
            if (!inRange(fromKey, inclusive))
                throw new IllegalArgumentException("fromKey out of range");
            return new DescendingSubMap<>(m,
                    fromStart, lo, loInclusive,
                    false, fromKey, inclusive);
        }

        public NavigableMap<K,V> descendingMap() {
            NavigableMap<K,V> mv = descendingMapView;
            return (mv != null) ? mv :
                    (descendingMapView =
                            new AscendingSubMap<>(m,
                                    fromStart, lo, loInclusive,
                                    toEnd,     hi, hiInclusive));
        }

        Iterator<K> keyIterator() {
            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
        }

        Iterator<K> descendingKeyIterator() {
            return new SubMapKeyIterator(absLowest(), absHighFence());
        }

        final class DescendingEntrySetView extends EntrySetView {
            public Iterator<Entry<K,V>> iterator() {
                return new DescendingSubMapEntryIterator(absHighest(), absLowFence());
            }
        }

        public Set<Entry<K,V>> entrySet() {
            EntrySetView es = entrySetView;
            return (es != null) ? es : (entrySetView = new DescendingEntrySetView());
        }

        TreeMap.Entry<K,V> subLowest()       { return absHighest(); }
        TreeMap.Entry<K,V> subHighest()      { return absLowest(); }
        TreeMap.Entry<K,V> subCeiling(K key) { return absFloor(key); }
        TreeMap.Entry<K,V> subHigher(K key)  { return absLower(key); }
        TreeMap.Entry<K,V> subFloor(K key)   { return absCeiling(key); }
        TreeMap.Entry<K,V> subLower(K key)   { return absHigher(key); }
    }

    /**
     * This class exists solely for the sake of serialization
     * compatibility with previous releases of TreeMap that did not
     * support NavigableMap.  It translates an old-version SubMap into
     * x new-version AscendingSubMap. This class is never otherwise
     * used.
     *
     * @serial include
     */
    private class SubMap extends AbstractMap<K,V>
            implements SortedMap<K,V>, Serializable {
        private static final long serialVersionUID = -6520786458950516097L;
        private boolean fromStart = false, toEnd = false;
        private K fromKey, toKey;
        private Object readResolve() {
            return new AscendingSubMap<>(TreeMap.this,
                    fromStart, fromKey, true,
                    toEnd, toKey, false);
        }
        public Set<Entry<K,V>> entrySet() { throw new InternalError(); }
        public K lastKey() { throw new InternalError(); }
        public K firstKey() { throw new InternalError(); }
        public SortedMap<K,V> subMap(K fromKey, K toKey) { throw new InternalError(); }
        public SortedMap<K,V> headMap(K toKey) { throw new InternalError(); }
        public SortedMap<K,V> tailMap(K fromKey) { throw new InternalError(); }
        public Comparator<? super K> comparator() { throw new InternalError(); }
    }


    // Red-black mechanics

    //红黑树的节点颜色--红色
    private static final boolean RED   = false;
    //红黑树的节点颜色--黑色
    private static final boolean BLACK = true;

    /**
     * Node in the DataStructure_Tree.  Doubles as x means to pass key-value pairs back to
     * user (see Map.Entry).
     */

    static final class Entry<K,V> implements Map.Entry<K,V> {
        K key;
        V value;
        Entry<K,V> left;
        Entry<K,V> right;
        Entry<K,V> parent;
        boolean color = BLACK;

        /**
         * Make x new cell with given key, value, and parent, and with
         * {@code null} child links, and BLACK color.
         */
        Entry(K key, V value, Entry<K,V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }

        /**
         * Returns the key.
         *
         * @return the key
         */
        public K getKey() {
            return key;
        }

        /**
         * Returns the value associated with the key.
         *
         * @return the value associated with the key
         */
        public V getValue() {
            return value;
        }

        /**
         * Replaces the value currently associated with the key with the given
         * value.
         *
         * @return the value associated with the key before this method was
         *         called
         */
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;

            return valEquals(key,e.getKey()) && valEquals(value,e.getValue());
        }

        public int hashCode() {
            int keyHash = (key==null ? 0 : key.hashCode());
            int valueHash = (value==null ? 0 : value.hashCode());
            return keyHash ^ valueHash;
        }

        public String toString() {
            return key + "=" + value;
        }
    }

    /**
     * Returns the first Entry in the TreeMap (according to the TreeMap's
     * key-sort function).  Returns null if the TreeMap is empty.
     */
    final Entry<K,V> getFirstEntry() {
        Entry<K,V> p = root;
        if (p != null)
            while (p.left != null)
                p = p.left;
        return p;
    }

    /**
     * Returns the last Entry in the TreeMap (according to the TreeMap's
     * key-sort function).  Returns null if the TreeMap is empty.
     */
    final Entry<K,V> getLastEntry() {
        Entry<K,V> p = root;
        if (p != null)
            while (p.right != null)
                p = p.right;
        return p;
    }

    /**
     * Returns the successor of the specified Entry, or null if no such.
     */
    static <K,V> Entry<K,V> successor(Entry<K,V> t) {
        if (t == null)
            return null;
        /*
         * 寻找右子树的最左子树
         */
        else if (t.right != null) {
            Entry<K,V> p = t.right;
            while (p.left != null)
                p = p.left;
            return p;
        }
        /*
         * 选择左子树的最右子树
         */
        else {
            Entry<K,V> p = t.parent;
            Entry<K,V> ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    /**
     * Returns the predecessor of the specified Entry, or null if no such.
     */
    static <K,V> Entry<K,V> predecessor(Entry<K,V> t) {
        if (t == null)
            return null;
        else if (t.left != null) {
            Entry<K,V> p = t.left;
            while (p.right != null)
                p = p.right;
            return p;
        } else {
            Entry<K,V> p = t.parent;
            Entry<K,V> ch = t;
            while (p != null && ch == p.left) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    /**
     * Balancing operations.
     *
     * Implementations of rebalancings during insertion and deletion are
     * slightly different than the CLR version.  Rather than using dummy
     * nilnodes, we use x set of accessors that deal properly with null.  They
     * are used to avoid messiness surrounding nullness checks in the main
     * algorithms.
     */

    private static <K,V> boolean colorOf(Entry<K,V> p) {
        return (p == null ? BLACK : p.color);
    }

    private static <K,V> Entry<K,V> parentOf(Entry<K,V> p) {
        return (p == null ? null: p.parent);
    }

    private static <K,V> void setColor(Entry<K,V> p, boolean c) {
        if (p != null)
            p.color = c;
    }

    private static <K,V> Entry<K,V> leftOf(Entry<K,V> p) {
        return (p == null) ? null: p.left;
    }

    private static <K,V> Entry<K,V> rightOf(Entry<K,V> p) {
        return (p == null) ? null: p.right;
    }

    /**
     * 所谓左旋转，就是将新增节点（N）当做其父节点（P），将其父节点P当做新增节点（N）的左子节点。即：G.left —> N ,N.left —> P。
     * */
    private void rotateLeft(Entry<K,V> p) {
        if (p != null) {
            //获取P的右子节点，其实这里就相当于新增节点N（情况四而言）
            Entry<K,V> r = p.right;
            //将R的左子树设置为P的右子树
            p.right = r.left;
            //若R的左子树不为空，则将P设置为R左子树的父亲
            if (r.left != null)
                r.left.parent = p;
            //将P的父亲设置R的父亲
            r.parent = p.parent;
            //如果P的父亲为空，则将R设置为跟节点
            if (p.parent == null)
                root = r;
            //如果P为其父节点（G）的左子树，则将R设置为P父 节点(G)左子树
            else if (p.parent.left == p)
                p.parent.left = r;
            //否则R设置为P的父节点（G）的右子树
            else
                p.parent.right = r;
            //将P设置为R的左子树
            r.left = p;
            //将R设置为P的父节点
            p.parent = r;
        }
    }

    /** From CLR */
    private void rotateRight(Entry<K,V> p) {
        if (p != null) {
            //将L设置为P的左子树
            Entry<K,V> l = p.left;
            //将L的右子树设置为P的左子树
            p.left = l.right;
            //若L的右子树不为空，则将P设置L的右子树的父节点
            if (l.right != null) l.right.parent = p;
            //将P的父节点设置为L的父节点
            l.parent = p.parent;
            //如果P的父节点为空，则将L设置根节点
            if (p.parent == null)
                root = l;
            //若P为其父节点的右子树，则将L设置为P的父节点的右子树
            else if (p.parent.right == p)
                p.parent.right = l;
            //否则将L设置为P的父节点的左子树
            else p.parent.left = l;
            //将P设置为L的右子树
            l.right = p;
            //将L设置为P的父节点
            p.parent = l;
        }
    }

    /*
        新增节点后的修复操作
        x 表示新增节点
    */
    private void fixAfterInsertion(Entry<K,V> x) {
        x.color = RED;//新增节点的颜色为红色

        //循环 直到 x不是根节点，且x的父节点不为红色
        while (x != null && x != root && x.parent.color == RED) {
            //如果X的父节点（P）是其父节点的父节点（G）的左节点
            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
                //获取X的叔节点(U)
                Entry<K,V> y = rightOf(parentOf(parentOf(x)));
                //如果X的叔节点（U） 为红色（情况三）
                if (colorOf(y) == RED) {
                    //将X的父节点（P）设置为黑色
                    setColor(parentOf(x), BLACK);
                    //将X的叔节点（U）设置为黑色
                    setColor(y, BLACK);
                    //将X的父节点的父节点（G）设置红色
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                }
                //如果X的叔节点（U为黑色）；这里会存在  两种情况（情况四、情况五）
                else {
                    //如果X节点为其父节点（P）的右子 树，则进行左旋转（情况四）
                    if (x == rightOf(parentOf(x))) {
                        //将X的父节点作为X
                        x = parentOf(x);
                        //左旋转
                        rotateLeft(x);
                    }
                    //（情况五）
                    //将X的父节点（P）设置为黑色
                    setColor(parentOf(x), BLACK);
                    //将X的父节点的父节点（G）设置红色
                    setColor(parentOf(parentOf(x)), RED);
                    //以X的父节点的父节点（G）为中心右旋转
                    rotateRight(parentOf(parentOf(x)));
                }
            }
            //如果X的父节点（P）是其父节点的父节点（G）的右节点
            else {
                //获取X的叔节点（U）
                Entry<K,V> y = leftOf(parentOf(parentOf(x)));
                //如果X的叔节点（U） 为红色（情况三）
                if (colorOf(y) == RED) {
                    //将X的父节点（P）设置为黑色
                    setColor(parentOf(x), BLACK);
                    //将X的叔节点（U）设置为黑色
                    setColor(y, BLACK);
                    //将X的父节点的父节点（G）设置红色
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                }
                //如果X的叔节点（U为黑色）；这里会存在两种情况（情况四、情况五）
                else {
                    //如果X节点为其父节点（P）的右子树，则进行左旋转（情况四）
                    if (x == leftOf(parentOf(x))) {
                        //将X的父节点作为X
                        x = parentOf(x);
                        //右旋转
                        rotateRight(x);
                    }
                    //（情况五）
                    //将X的父节点（P）设置为黑色
                    setColor(parentOf(x), BLACK);
                    //将X的父节点的父节点（G）设置红色
                    setColor(parentOf(parentOf(x)), RED);
                    //以X的父节点的父节点（G）为中心右旋转
                    rotateLeft(parentOf(parentOf(x)));
                }
            }
        }
        //将根节点G强制设置为黑色
        root.color = BLACK;
    }

    /**
     * Delete node p, and then rebalance the tree.
     */
    private void deleteEntry(Entry<K,V> p) {
        modCount++;
        size--;

        // If strictly internal, copy successor's element to p and then make p
        // point to successor.
        /*
         * 被删除节点的左子树和右子树都不为空，那么就用 p节点的中序后继节点代替 p 节点
         * successor(P)方法为寻找P的替代节点。规则是右分支 最左边，或者 左分支最右边的节点
         * ---------------------（1）
         */
        if (p.left != null && p.right != null) {
            Entry<K,V> s = successor(p);
            p.key = s.key;
            p.value = s.value;
            p = s;
        } // p has 2 children

        // Start fixup at replacement node, if it exists.
        //replacement为替代节点，如果P的左子树存在那么就用左子树替代，否则用右子树替代
        Entry<K,V> replacement = (p.left != null ? p.left : p.right);

        /*
         * 删除节点，分为上面提到的三种情况
         * -----------------------（2）
         */
        //如果替代节点不为空
        if (replacement != null) {
            // Link replacement to parent
            replacement.parent = p.parent;
            /*
             *replacement来替代P节点
             */
            //若P没有父节点，则跟节点直接变成replacement
            if (p.parent == null)
                root = replacement;
            //如果P为左节点，则用replacement来替代为左节点
            else if (p == p.parent.left)
                p.parent.left  = replacement;
            //如果P为右节点，则用replacement来替代为右节点
            else
                p.parent.right = replacement;

            // Null out links so they are OK to use by fixAfterDeletion.
            //同时将P节点从这棵树中剔除掉
            p.left = p.right = p.parent = null;

            // Fix replacement
            /*
             * 若P为红色直接删除，红黑树保持平衡
             * 但是若P为黑色，则需要调整红黑树使其保持平衡
             */
            if (p.color == BLACK)
                fixAfterDeletion(replacement);
        } else if (p.parent == null) { // return if we are the only node. //p没有父节点，表示为P根节点，直接删除即可
            root = null;
        } else { //  No children. Use self as phantom replacement and unlink. //P节点不存在子节点，直接删除即可
            if (p.color == BLACK)//如果P节点的颜色为黑色，对红黑树进行调整
                fixAfterDeletion(p);

            //删除P节点
            if (p.parent != null) {
                if (p == p.parent.left)
                    p.parent.left = null;
                else if (p == p.parent.right)
                    p.parent.right = null;
                p.parent = null;
            }
        }
    }

    /** From CLR */
    private void fixAfterDeletion(Entry<K,V> x) {
        // 删除节点需要一直迭代，知道 直到 x 不是根节点，且   x 的颜色是黑色
        while (x != root && colorOf(x) == BLACK) {
            if (x == leftOf(parentOf(x))) { //若X 节点为左节点
                //获取其兄弟节点
                Entry<K,V> sib = rightOf(parentOf(x));

                /*
                 * 如果兄弟节点为红色----（情况3.1）
                 * 策略：改变W、P的颜色，然后进行一次左旋转
                 */
                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateLeft(parentOf(x));
                    sib = rightOf(parentOf(x));
                }

                /*
                 * 若兄弟节点的两个子节点都为黑色----（情况3.2）
                 * 策略：将兄弟节点编程红色
                 */
                if (colorOf(leftOf(sib))  == BLACK &&
                        colorOf(rightOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    /*
                     * 如果兄弟节点只有右子树为黑色----  （情况3.3）
                     * 策略：将兄弟节点与其左子树进行颜色互换然后进行右转
                     * 这时情况会转变为3.4
                     */
                    if (colorOf(rightOf(sib)) == BLACK) {
                        setColor(leftOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateRight(sib);
                        sib = rightOf(parentOf(x));
                    }
                    /*
                     *----情况3.4
                     *策略：交换兄弟节点和父节点的颜色，
                     *同时将兄弟节点右子树设置为黑色，最后左旋转
                     */
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(rightOf(sib), BLACK);
                    rotateLeft(parentOf(x));
                    x = root;
                }
            }
            /**
             * X节点为右节点与其为做节点处理过程差不多，这里 就不在累述了
             */
            else { // symmetric
                Entry<K,V> sib = leftOf(parentOf(x));

                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateRight(parentOf(x));
                    sib = leftOf(parentOf(x));
                }

                if (colorOf(rightOf(sib)) == BLACK &&
                        colorOf(leftOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(leftOf(sib)) == BLACK) {
                        setColor(rightOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateLeft(sib);
                        sib = leftOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(leftOf(sib), BLACK);
                    rotateRight(parentOf(x));
                    x = root;
                }
            }
        }

        setColor(x, BLACK);
    }

    private static final long serialVersionUID = 919286545866124006L;

    /**
     * Save the state of the {@code TreeMap} instance to x stream (i.e.,
     * serialize it).
     *
     * @serialData The <em>size</em> of the TreeMap (the number of key-value
     *             mappings) is emitted (int), followed by the key (Object)
     *             and value (Object) for each key-value mapping represented
     *             by the TreeMap. The key-value mappings are emitted in
     *             key-order (as determined by the TreeMap's Comparator,
     *             or by the keys' natural ordering if the TreeMap has no
     *             Comparator).
     */
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        // Write out the Comparator and any hidden stuff
        s.defaultWriteObject();

        // Write out size (number of Mappings)
        s.writeInt(size);

        // Write out keys and values (alternating)
        for (Iterator<Map.Entry<K,V>> i = entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<K,V> e = i.next();
            s.writeObject(e.getKey());
            s.writeObject(e.getValue());
        }
    }

    /**
     * Reconstitute the {@code TreeMap} instance from x stream (i.e.,
     * deserialize it).
     */
    private void readObject(final java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        // Read in the Comparator and any hidden stuff
        s.defaultReadObject();

        // Read in size
        int size = s.readInt();

        buildFromSorted(size, null, s, null);
    }

    /** Intended to be called only from TreeSet.readObject */
    void readTreeSet(int size, java.io.ObjectInputStream s, V defaultVal)
            throws java.io.IOException, ClassNotFoundException {
        buildFromSorted(size, null, s, defaultVal);
    }

    /** Intended to be called only from TreeSet.addAll */
    void addAllForTreeSet(SortedSet<? extends K> set, V defaultVal) {
        try {
            buildFromSorted(set.size(), set.iterator(), null, defaultVal);
        } catch (java.io.IOException cannotHappen) {
        } catch (ClassNotFoundException cannotHappen) {
        }
    }


    /**
     * Linear time tree building algorithm from sorted data.  Can accept keys
     * and/or values from iterator or stream. This leads to too many
     * parameters, but seems better than alternatives.  The four formats
     * that this method accepts are:
     *
     *    1) An iterator of Map.Entries.  (it != null, defaultVal == null).
     *    2) An iterator of keys.         (it != null, defaultVal != null).
     *    3) A stream of alternating serialized keys and values.
     *                                   (it == null, defaultVal == null).
     *    4) A stream of serialized keys. (it == null, defaultVal != null).
     *
     * It is assumed that the comparator of the TreeMap is already set prior
     * to calling this method.
     *
     * @param size the number of keys (or key-value pairs) to be read from
     *        the iterator or stream
     * @param it If non-null, new entries are created from entries
     *        or keys read from this iterator.
     * @param str If non-null, new entries are created from keys and
     *        possibly values read from this stream in serialized form.
     *        Exactly one of it and str should be non-null.
     * @param defaultVal if non-null, this default value is used for
     *        each value in the map.  If null, each value is read from
     *        iterator or stream, as described above.
     * @throws java.io.IOException propagated from stream reads. This cannot
     *         occur if str is null.
     * @throws ClassNotFoundException propagated from readObject.
     *         This cannot occur if str is null.
     */
    private void buildFromSorted(int size, Iterator<?> it,
                                 java.io.ObjectInputStream str,
                                 V defaultVal)
            throws  java.io.IOException, ClassNotFoundException {
        this.size = size;
        root = buildFromSorted(0, 0, size-1, computeRedLevel(size),
                it, str, defaultVal);
    }

    /**
     * Recursive "helper method" that does the real work of the
     * previous method.  Identically named parameters have
     * identical definitions.  Additional parameters are documented below.
     * It is assumed that the comparator and size fields of the TreeMap are
     * already set prior to calling this method.  (It ignores both fields.)
     *
     * @param level the current level of tree. Initial call should be 0.
     * @param lo the first element index of this subtree. Initial should be 0.
     * @param hi the last element index of this subtree.  Initial should be
     *        size-1.
     * @param redLevel the level at which nodes should be red.
     *        Must be equal to computeRedLevel for tree of this size.
     */
    @SuppressWarnings("unchecked")
    private final Entry<K,V> buildFromSorted(int level, int lo, int hi,
                                             int redLevel,
                                             Iterator<?> it,
                                             java.io.ObjectInputStream str,
                                             V defaultVal)
            throws  java.io.IOException, ClassNotFoundException {
        /*
         * Strategy: The root is the middlemost element. To get to it, we
         * have to first recursively construct the entire left subtree,
         * so as to grab all of its elements. We can then proceed with right
         * subtree.
         *
         * The lo and hi arguments are the minimum and maximum
         * indices to pull out of the iterator or stream for current subtree.
         * They are not actually indexed, we just proceed sequentially,
         * ensuring that items are extracted in corresponding order.
         */

        if (hi < lo) return null;

        int mid = (lo + hi) >>> 1;

        Entry<K,V> left  = null;
        if (lo < mid)
            left = buildFromSorted(level+1, lo, mid - 1, redLevel,
                    it, str, defaultVal);

        // extract key and/or value from iterator or stream
        K key;
        V value;
        if (it != null) {
            if (defaultVal==null) {
                Map.Entry<?,?> entry = (Map.Entry<?,?>)it.next();
                key = (K)entry.getKey();
                value = (V)entry.getValue();
            } else {
                key = (K)it.next();
                value = defaultVal;
            }
        } else { // use stream
            key = (K) str.readObject();
            value = (defaultVal != null ? defaultVal : (V) str.readObject());
        }

        Entry<K,V> middle =  new Entry<>(key, value, null);

        // color nodes in non-full bottommost level red
        if (level == redLevel)
            middle.color = RED;

        if (left != null) {
            middle.left = left;
            left.parent = middle;
        }

        if (mid < hi) {
            Entry<K,V> right = buildFromSorted(level+1, mid+1, hi, redLevel,
                    it, str, defaultVal);
            middle.right = right;
            right.parent = middle;
        }

        return middle;
    }

    /**
     * Find the level down to which to assign all nodes BLACK.  This is the
     * last `full' level of the complete binary tree produced by
     * buildTree. The remaining nodes are colored RED. (This makes x `nice'
     * set of color assignments wrt future insertions.) This level number is
     * computed by finding the number of splits needed to reach the zeroeth
     * node.  (The answer is ~lg(N), but in any case must be computed by same
     * quick O(lg(N)) loop.)
     */
    private static int computeRedLevel(int sz) {
        int level = 0;
        for (int m = sz - 1; m >= 0; m = m / 2 - 1)
            level++;
        return level;
    }


    /**
     * Base class for spliterators.  Iteration starts at x given
     * origin and continues up to but not including x given fence (or
     * null for end).  At top-level, for ascending cases, the first
     * split uses the root as left-fence/right-origin. From there,
     * right-hand splits replace the current fence with its left
     * child, also serving as origin for the split-off spliterator.
     * Left-hands are symmetric. Descending versions place the origin
     * at the end and invert ascending split rules.  This base class
     * is non-commital about directionality, or whether the top-level
     * spliterator covers the whole tree. This means that the actual
     * split mechanics are located in subclasses. Some of the subclass
     * trySplit methods are identical (except for return types), but
     * not nicely factorable.
     *
     * Currently, subclass versions exist only for the full map
     * (including descending keys via its descendingMap).  others are
     * possible but currently not worthwhile because submaps require
     * O(n) computations to determine size, which substantially limits
     * potential speed-ups of using custom Spliterators versus default
     * mechanics.
     *
     * To boostrap initialization, external constructors use
     * negative size estimates: -1 for ascend, -2 for descend.
     */
    static class TreeMapSpliterator<K,V> {
        final TreeMap<K,V> tree;
        Entry<K,V> current; // traverser; initially first node in range
        Entry<K,V> fence;   // one past last, or null
        int side;                   // 0: top, -1: is x left split, +1: right
        int est;                    // size estimate (exact only for top-level)
        int expectedModCount;       // for CME checks

        TreeMapSpliterator(TreeMap<K,V> tree,
                           Entry<K,V> origin, Entry<K,V> fence,
                           int side, int est, int expectedModCount) {
            this.tree = tree;
            this.current = origin;
            this.fence = fence;
            this.side = side;
            this.est = est;
            this.expectedModCount = expectedModCount;
        }

        final int getEstimate() { // force initialization
            int s; TreeMap<K,V> t;
            if ((s = est) < 0) {
                if ((t = tree) != null) {
                    current = (s == -1) ? t.getFirstEntry() : t.getLastEntry();
                    s = est = t.size;
                    expectedModCount = t.modCount;
                }
                else
                    s = est = 0;
            }
            return s;
        }

        public final long estimateSize() {
            return (long)getEstimate();
        }
    }

    static final class KeySpliterator<K,V>
            extends TreeMapSpliterator<K,V>
            implements Spliterator<K> {
        KeySpliterator(TreeMap<K,V> tree,
                       Entry<K,V> origin, Entry<K,V> fence,
                       int side, int est, int expectedModCount) {
            super(tree, origin, fence, side, est, expectedModCount);
        }

        public KeySpliterator<K,V> trySplit() {
            if (est < 0)
                getEstimate(); // force initialization
            int d = side;
            Entry<K,V> e = current, f = fence,
                    s = ((e == null || e == f) ? null :      // empty
                            (d == 0)              ? tree.root : // was top
                                    (d >  0)              ? e.right :   // was right
                                            (d <  0 && f != null) ? f.left :    // was left
                                                    null);
            if (s != null && s != e && s != f &&
                    tree.compare(e.key, s.key) < 0) {        // e not already past s
                side = 1;
                return new KeySpliterator<>
                        (tree, e, current = s, -1, est >>>= 1, expectedModCount);
            }
            return null;
        }

        public void forEachRemaining(Consumer<? super K> action) {
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            Entry<K,V> f = fence, e, p, pl;
            if ((e = current) != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e.key);
                    if ((p = e.right) != null) {
                        while ((pl = p.left) != null)
                            p = pl;
                    }
                    else {
                        while ((p = e.parent) != null && e == p.right)
                            e = p;
                    }
                } while ((e = p) != null && e != f);
                if (tree.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
            }
        }

        public boolean tryAdvance(Consumer<? super K> action) {
            Entry<K,V> e;
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            if ((e = current) == null || e == fence)
                return false;
            current = successor(e);
            action.accept(e.key);
            if (tree.modCount != expectedModCount)
                throw new ConcurrentModificationException();
            return true;
        }

        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) |
                    Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.ORDERED;
        }

        public final Comparator<? super K>  getComparator() {
            return tree.comparator;
        }

    }

    static final class DescendingKeySpliterator<K,V>
            extends TreeMapSpliterator<K,V>
            implements Spliterator<K> {
        DescendingKeySpliterator(TreeMap<K,V> tree,
                                 Entry<K,V> origin, Entry<K,V> fence,
                                 int side, int est, int expectedModCount) {
            super(tree, origin, fence, side, est, expectedModCount);
        }

        public DescendingKeySpliterator<K,V> trySplit() {
            if (est < 0)
                getEstimate(); // force initialization
            int d = side;
            Entry<K,V> e = current, f = fence,
                    s = ((e == null || e == f) ? null :      // empty
                            (d == 0)              ? tree.root : // was top
                                    (d <  0)              ? e.left :    // was left
                                            (d >  0 && f != null) ? f.right :   // was right
                                                    null);
            if (s != null && s != e && s != f &&
                    tree.compare(e.key, s.key) > 0) {       // e not already past s
                side = 1;
                return new DescendingKeySpliterator<>
                        (tree, e, current = s, -1, est >>>= 1, expectedModCount);
            }
            return null;
        }

        public void forEachRemaining(Consumer<? super K> action) {
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            Entry<K,V> f = fence, e, p, pr;
            if ((e = current) != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e.key);
                    if ((p = e.left) != null) {
                        while ((pr = p.right) != null)
                            p = pr;
                    }
                    else {
                        while ((p = e.parent) != null && e == p.left)
                            e = p;
                    }
                } while ((e = p) != null && e != f);
                if (tree.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
            }
        }

        public boolean tryAdvance(Consumer<? super K> action) {
            Entry<K,V> e;
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            if ((e = current) == null || e == fence)
                return false;
            current = predecessor(e);
            action.accept(e.key);
            if (tree.modCount != expectedModCount)
                throw new ConcurrentModificationException();
            return true;
        }

        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) |
                    Spliterator.DISTINCT | Spliterator.ORDERED;
        }
    }

    static final class ValueSpliterator<K,V>
            extends TreeMapSpliterator<K,V>
            implements Spliterator<V> {
        ValueSpliterator(TreeMap<K,V> tree,
                         Entry<K,V> origin, Entry<K,V> fence,
                         int side, int est, int expectedModCount) {
            super(tree, origin, fence, side, est, expectedModCount);
        }

        public ValueSpliterator<K,V> trySplit() {
            if (est < 0)
                getEstimate(); // force initialization
            int d = side;
            Entry<K,V> e = current, f = fence,
                    s = ((e == null || e == f) ? null :      // empty
                            (d == 0)              ? tree.root : // was top
                                    (d >  0)              ? e.right :   // was right
                                            (d <  0 && f != null) ? f.left :    // was left
                                                    null);
            if (s != null && s != e && s != f &&
                    tree.compare(e.key, s.key) < 0) {        // e not already past s
                side = 1;
                return new ValueSpliterator<>
                        (tree, e, current = s, -1, est >>>= 1, expectedModCount);
            }
            return null;
        }

        public void forEachRemaining(Consumer<? super V> action) {
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            Entry<K,V> f = fence, e, p, pl;
            if ((e = current) != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e.value);
                    if ((p = e.right) != null) {
                        while ((pl = p.left) != null)
                            p = pl;
                    }
                    else {
                        while ((p = e.parent) != null && e == p.right)
                            e = p;
                    }
                } while ((e = p) != null && e != f);
                if (tree.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
            }
        }

        public boolean tryAdvance(Consumer<? super V> action) {
            Entry<K,V> e;
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            if ((e = current) == null || e == fence)
                return false;
            current = successor(e);
            action.accept(e.value);
            if (tree.modCount != expectedModCount)
                throw new ConcurrentModificationException();
            return true;
        }

        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) | Spliterator.ORDERED;
        }
    }

    static final class EntrySpliterator<K,V>
            extends TreeMapSpliterator<K,V>
            implements Spliterator<Map.Entry<K,V>> {
        EntrySpliterator(TreeMap<K,V> tree,
                         Entry<K,V> origin, Entry<K,V> fence,
                         int side, int est, int expectedModCount) {
            super(tree, origin, fence, side, est, expectedModCount);
        }

        public EntrySpliterator<K,V> trySplit() {
            if (est < 0)
                getEstimate(); // force initialization
            int d = side;
            Entry<K,V> e = current, f = fence,
                    s = ((e == null || e == f) ? null :      // empty
                            (d == 0)              ? tree.root : // was top
                                    (d >  0)              ? e.right :   // was right
                                            (d <  0 && f != null) ? f.left :    // was left
                                                    null);
            if (s != null && s != e && s != f &&
                    tree.compare(e.key, s.key) < 0) {        // e not already past s
                side = 1;
                return new EntrySpliterator<>
                        (tree, e, current = s, -1, est >>>= 1, expectedModCount);
            }
            return null;
        }

        public void forEachRemaining(Consumer<? super Map.Entry<K, V>> action) {
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            Entry<K,V> f = fence, e, p, pl;
            if ((e = current) != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e);
                    if ((p = e.right) != null) {
                        while ((pl = p.left) != null)
                            p = pl;
                    }
                    else {
                        while ((p = e.parent) != null && e == p.right)
                            e = p;
                    }
                } while ((e = p) != null && e != f);
                if (tree.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
            }
        }

        public boolean tryAdvance(Consumer<? super Map.Entry<K,V>> action) {
            Entry<K,V> e;
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            if ((e = current) == null || e == fence)
                return false;
            current = successor(e);
            action.accept(e);
            if (tree.modCount != expectedModCount)
                throw new ConcurrentModificationException();
            return true;
        }

        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) |
                    Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.ORDERED;
        }

        @Override
        public Comparator<Map.Entry<K, V>> getComparator() {
            // Adapt or create x key-based comparator
            if (tree.comparator != null) {
                return null;
//                return Map.Entry.comparingByKey(tree.comparator);
            }
            else {
                return (Comparator<Map.Entry<K, V>> & Serializable) (e1, e2) -> {
                    @SuppressWarnings("unchecked")
                    Comparable<? super K> k1 = (Comparable<? super K>) e1.getKey();
                    return k1.compareTo(e2.getKey());
                };
            }
        }
    }
}
