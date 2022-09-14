package me.yuanhu.core.DataStructure.java.jcf;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/*
*      Map也是接口，但没有继承Collection接口。该接口描述了从不重复的键到值的映射。Map接口用于维护键/值对（key/value pairs）。
       特征：它描述了从不重复的键到值的映射。
       两个重要的实现类：HashMap和TreeMap
       1.HashMap，中文叫散列表，基于哈希表实现，特点就是键值对的映射关系。一个key对应一个Value。
       HashMap中元素的排列顺序是不固定的。更加适合于对元素进行插入、删除和定位。
       2.TreeMap，基于红黑树实现。TreeMap中的元素保持着某种固定的顺序。更加适合于对元素的顺序遍历。

        注意：
        可以使用 Map 作为 Map 的值，但禁止使用 Map 作为 Map 的键。因为在这么复杂的 Map 中，equals() 方法和 hashCode() 比较难定义。
* */
public interface Map<K,V> {

    // Query Operations

    int size();

    boolean isEmpty();

    boolean containsKey(Object key);

    boolean containsValue(Object value);

    V get(Object key);


    // Modification Operations

    V put(K key, V value);

    V remove(Object key);


    // Bulk Operations

    void putAll(Map<? extends K, ? extends V> m);

    void clear();


    // Views

    Set<K> keySet();

    Collection<V> values();

    Set<Entry<K, V>> entrySet();


    interface Entry<K, V> {

        K getKey();

        V getValue();

        V setValue(V value);

        boolean equals(Object o);

        int hashCode();

    }

    // Comparison and hashing


    boolean equals(Object o);

    int hashCode();

    // Defaultable methods

    default V getOrDefault(Object key, V defaultValue) {
        V v;
        return (((v = get(key)) != null) || containsKey(key))
                ? v
                : defaultValue;
    }

    default void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
//        for (Map.Entry<K, V> entry : entrySet()) {
        Iterator<Entry<K, V>> it = entrySet().iterator();
        while (it.hasNext()) {
            Entry<K, V> entry = it.next();
            K k;
            V v;
            try {
                k = entry.getKey();
                v = entry.getValue();
            } catch (IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }
            action.accept(k, v);
        }
    }

    default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);
//        for (Map.Entry<K, V> entry : entrySet()) {
        Iterator<Entry<K, V>> it = entrySet().iterator();
        while (it.hasNext()) {
            Entry<K, V> entry = it.next();
            K k;
            V v;
            try {
                k = entry.getKey();
                v = entry.getValue();
            } catch (IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }

            // ise thrown from function is not x cme.
            v = function.apply(k, v);

            try {
                entry.setValue(v);
            } catch (IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }
        }
    }

    default V putIfAbsent(K key, V value) {
        V v = get(key);
        if (v == null) {
            v = put(key, value);
        }

        return v;
    }

    default boolean remove(Object key, Object value) {
        Object curValue = get(key);
        if (!Objects.equals(curValue, value) ||
                (curValue == null && !containsKey(key))) {
            return false;
        }
        remove(key);
        return true;
    }

    default boolean replace(K key, V oldValue, V newValue) {
        Object curValue = get(key);
        if (!Objects.equals(curValue, oldValue) ||
                (curValue == null && !containsKey(key))) {
            return false;
        }
        put(key, newValue);
        return true;
    }

    default V replace(K key, V value) {
        V curValue;
        if (((curValue = get(key)) != null) || containsKey(key)) {
            curValue = put(key, value);
        }
        return curValue;
    }

    /*
     * 如果指定的key不存在，则通过指定的K -> V计算出新的值设置为key的值
     * */
    default V computeIfAbsent(K key,
                              Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v;
        if ((v = get(key)) == null) {
            V newValue;
            if ((newValue = mappingFunction.apply(key)) != null) {
                put(key, newValue);
                return newValue;
            }
        }

        return v;
    }

    /*
    * 如果指定的key存在，则根据旧的key和value计算新的值newValue, 如果newValue不为null，则设置key新的值为newValue, 如果newValue为null, 则删除该key的值
    * */
    default V computeIfPresent(K key,
                               BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue;
        if ((oldValue = get(key)) != null) {
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue != null) {
                put(key, newValue);
                return newValue;
            } else {
                remove(key);
                return null;
            }
        } else {
            return null;
        }
    }

    //compute方法是computeIfAbsent与computeIfPresent的综合体。
    default V compute(K key,
                      BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = get(key);

        V newValue = remappingFunction.apply(key, oldValue);
        if (newValue == null) {
            // delete mapping
            if (oldValue != null || containsKey(key)) {
                // something to remove
                remove(key);
                return null;
            } else {
                // nothing to do. Leave things as they were.
                return null;
            }
        } else {
            // add or replace old mapping
            put(key, newValue);
            return newValue;
        }
    }

    /*
    * 如果指定的key不存在，则设置指定的value值，否则根据key的旧的值oldvalue，value计算出新的值newValue, 如果newValue为null, 则删除该key，否则设置key的新值newValue。
    * */
    default V merge(K key, V value,
                    BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        V oldValue = get(key);
        V newValue = (oldValue == null) ? value :
                remappingFunction.apply(oldValue, value);
        if (newValue == null) {
            remove(key);
        } else {
            put(key, newValue);
        }
        return newValue;
    }
}
