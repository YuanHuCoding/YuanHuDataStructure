package me.yuanhu.core.DataStructure.java.jcf;

import java.util.Iterator;
/*
    AbstractMap 是 Map 接口的的实现类之一，也是 HashMap, TreeMap, ConcurrentHashMap 等类的父类。
*/
public abstract class AbstractMap<K,V> implements Map<K,V> {

    protected AbstractMap() {
    }

    // Query Operations

    public int size() {
        return entrySet().size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsValue(Object value) {
        Iterator<Entry<K, V>> i = entrySet().iterator();
        if (value == null) {
            while (i.hasNext()) {
                Entry<K, V> e = i.next();
                if (e.getValue() == null)
                    return true;
            }
        } else {
            while (i.hasNext()) {
                Entry<K, V> e = i.next();
                if (value.equals(e.getValue()))
                    return true;
            }
        }
        return false;
    }

    //是否存在指定的 key
    //时间复杂度为 O(n)
    //许多实现类都重写了这个方法
    public boolean containsKey(Object key) {
        //还是迭代器遍历，查找 key，跟 get() 很像啊
        Iterator<Entry<K, V>> i = entrySet().iterator();
        if (key == null) {
            while (i.hasNext()) {
                Entry<K, V> e = i.next();
                if (e.getKey() == null)
                    return true;
            }
        } else {
            while (i.hasNext()) {
                Entry<K, V> e = i.next();
                if (key.equals(e.getKey()))
                    return true;
            }
        }
        return false;
    }

    //时间复杂度为 O(n)
    //许多实现类都重写了这个方法
    public V get(Object key) {
        //使用 Set 迭代器进行遍历，根据 key 查找
        Iterator<Entry<K, V>> i = entrySet().iterator();
        if (key == null) {
            while (i.hasNext()) {
                Entry<K, V> e = i.next();
                if (e.getKey() == null)
                    return e.getValue();
            }
        } else {
            while (i.hasNext()) {
                Entry<K, V> e = i.next();
                if (key.equals(e.getKey()))
                    return e.getValue();
            }
        }
        return null;
    }

    // Modification Operations

    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public V remove(Object key) {
        //获取保存 Map.Entry 集合的迭代器
        Iterator<Entry<K, V>> i = entrySet().iterator();
        Entry<K, V> correctEntry = null;
        //遍历查找，当某个 Entry 的 key 和 指定 key 一致时结束
        if (key == null) {
            while (correctEntry == null && i.hasNext()) {
                Entry<K, V> e = i.next();
                if (e.getKey() == null)
                    correctEntry = e;
            }
        } else {
            while (correctEntry == null && i.hasNext()) {
                Entry<K, V> e = i.next();
                if (key.equals(e.getKey()))
                    correctEntry = e;
            }
        }

        //找到了，返回要删除的值
        V oldValue = null;
        if (correctEntry != null) {
            oldValue = correctEntry.getValue();
            //调用迭代器的 remove 方法
            i.remove();
        }
        return oldValue;
    }


    // Bulk Operations

    public void putAll(Map<? extends K, ? extends V> m) {
        Iterator<? extends Entry<? extends K, ? extends V>> i = m.entrySet().iterator();
        for (Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }

    public void clear() {
        entrySet().clear();
    }


    // Views

    //他们都是 transient, volatile, 分别表示不可序列化、并发环境下变量的修改能够保证线程可见性。
    //需要注意的是 volatile 只能保证可见性，不能保证原子性，需要保证操作是原子性操作，才能保证使用 volatile 关键字的程序在并发时能够正确执行。
    transient Set<K> keySet;
    transient Collection<V> values;


    public Set<K> keySet() {
        Set<K> ks = keySet;
        if (ks == null) {
            //如果成员变量 keySet 为 null,创建个空的 AbstractSet
            ks = new AbstractSet<K>() {
                public Iterator<K> iterator() {
                    return new Iterator<K>() {
                        private Iterator<Entry<K, V>> i = entrySet().iterator();

                        public boolean hasNext() {
                            return i.hasNext();
                        }

                        public K next() {
                            return i.next().getKey();
                        }

                        public void remove() {
                            i.remove();
                        }
                    };
                }

                public int size() {
                    return AbstractMap.this.size();
                }

                public boolean isEmpty() {
                    return AbstractMap.this.isEmpty();
                }

                public void clear() {
                    AbstractMap.this.clear();
                }

                public boolean contains(Object k) {
                    return AbstractMap.this.containsKey(k);
                }
            };
            keySet = ks;
        }
        return ks;
    }

    public Collection<V> values() {
        Collection<V> vals = values;
        if (vals == null) {
            //没有就创建个空的 AbstractCollection 返回
            vals = new AbstractCollection<V>() {
                public Iterator<V> iterator() {
                    return new Iterator<V>() {
                        private Iterator<Entry<K, V>> i = entrySet().iterator();

                        public boolean hasNext() {
                            return i.hasNext();
                        }

                        public V next() {
                            return i.next().getValue();
                        }

                        public void remove() {
                            i.remove();
                        }
                    };
                }

                public int size() {
                    return AbstractMap.this.size();
                }

                public boolean isEmpty() {
                    return AbstractMap.this.isEmpty();
                }

                public void clear() {
                    AbstractMap.this.clear();
                }

                public boolean contains(Object v) {
                    return AbstractMap.this.containsValue(v);
                }
            };
            values = vals;
        }
        return vals;
    }


    /*
     AbstarctMap 中唯一的抽象方法
     当我们要实现一个 不可变的 Map 时，只需要继承这个类，然后实现 entrySet() 方法，这个方法返回一个保存所有 key-value 映射的 set。
     通常这个 Set 不支持 add(), remove() 方法，Set 对应的迭代器也不支持 remove() 方法。
     如果想要实现一个 可变的 Map,我们需要在上述操作外，重写 put() 方法，因为 默认不支持 put 操作.
     而且 entrySet() 返回的 Set 的迭代器，也得实现 remove() 方法，因为 AbstractMap 中的 删除相关操作都需要调用该迭代器的 remove() 方法。
    */
    public abstract Set<Entry<K, V>> entrySet();


    // Comparison and hashing

    public boolean equals(Object o) {
        if (o == this)
            return true;

        //必须是 Map 的实现类
        if (!(o instanceof Map))
            return false;
        //强转为 Map
        //将Object类型强转为Map.Entry类型，这里参数使用“?”而不是“K, V”是因为泛型在运行时类型会被擦除，编译器不知道具体的K,V是什么类型
        Map<?, ?> m = (Map<?, ?>) o;
        //元素个数必须一致
        if (m.size() != size())
            return false;

        try {
            //还是需要一个个遍历，对比
            Iterator<Entry<K, V>> i = entrySet().iterator();
            while (i.hasNext()) {
                //对比每个 Entry 的 key 和 value
                Entry<K, V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if (value == null) {
                    //对比 key, value
                    if (!(m.get(key) == null && m.containsKey(key)))
                        return false;
                } else {
                    if (!value.equals(m.get(key)))
                        return false;
                }
            }
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int h = 0;
        Iterator<Entry<K, V>> i = entrySet().iterator();
        //是所有 Entry 哈希值的和
        while (i.hasNext())
            h += i.next().hashCode();
        return h;
    }

    public String toString() {
        Iterator<Entry<K, V>> i = entrySet().iterator();
        if (!i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (; ; ) {
            Entry<K, V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key == this ? "(this Map)" : key);
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (!i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
    }

    protected Object clone() throws CloneNotSupportedException {
        AbstractMap<?, ?> result = (AbstractMap<?, ?>) super.clone();
        result.keySet = null;
        result.values = null;
        return result;
    }

    /**
     * Utility method for SimpleEntry and SimpleImmutableEntry.
     * Test for equality, checking for nulls.
     * <p>
     * NB: Do not replace with Object.equals until JDK-8015417 is resolved.
     */
    //内部用来测试 SimpleEntry, SimpleImmutableEntry 是否相等的方法
    private static boolean eq(Object o1, Object o2) {
        //这个三目运算符也很简单，只不过需要注意的是尽管这里o1、o2是Object类型，Object类型的equals方法是通过“==”比较的引用，
        // 所以不要认为这里有问题，因为在实际中，o1类型有可能是String，尽管被转为了Object，所以此时在调用equals方法时还是调用的String#equals方法。
        return o1 == null ? o2 == null : o1.equals(o2);
    }



    //表示可变的键值对
    public static class SimpleEntry<K, V>
            implements Entry<K, V>, java.io.Serializable {
        private static final long serialVersionUID = -8499721149061103585L;

        private final K key;
        private V value;

        public SimpleEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public SimpleEntry(Entry<? extends K, ? extends V> entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Entry<?, ?> e = (Entry<?, ?>) o;
            return eq(key, e.getKey()) && eq(value, e.getValue());
        }

        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^
                    (value == null ? 0 : value.hashCode());
        }

        public String toString() {
            return key + "=" + value;
        }

    }

    /**
     * An Entry maintaining an immutable key and value.  This class
     * does not support method <tt>setValue</tt>.  This class may be
     * convenient in methods that return thread-safe snapshots of
     * key-value mappings.
     *
     * @since 1.6
     */
    // 表示一个不可变的键值
    //SimpleEntry 与 SimpleImmutableEntry 唯一的区别就是支持 setValue() 操作。
    public static class SimpleImmutableEntry<K, V>
            implements Entry<K, V>, java.io.Serializable {
        private static final long serialVersionUID = 7138329143949025153L;

        private final K key;
        private final V value;

        public SimpleImmutableEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public SimpleImmutableEntry(Entry<? extends K, ? extends V> entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        //修改值，不可修改的 Entry 默认不支持这个操作
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        //比较指定 Entry 和本地是否相等
        //要求顺序，key-value 必须全相等
        //只要是 Map 的实现类即可，不同实现也可以相等
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Entry<?, ?> e = (Entry<?, ?>) o;
            return eq(key, e.getKey()) && eq(value, e.getValue());
        }

        //哈希值
        //是键的哈希与值的哈希的 异或
        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^
                    (value == null ? 0 : value.hashCode());
        }

        public String toString() {
            return key + "=" + value;
        }

    }

}