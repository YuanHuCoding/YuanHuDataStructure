package me.yuanhu.core.DataStructure.java.jcf;

import java.util.Comparator;

/*
   SortedMap是一个继承于Map接口的接口。它是一个有序的SortedMap键值映射。
SortedMap的排序方式有两种：自然排序 或者 用户指定比较器。 插入有序 SortedMap 的所有元素都必须实现 Comparable 接口（或者被指定的比较器所接受）。
 */
public interface SortedMap<K,V> extends Map<K,V> {

    Comparator<? super K> comparator();

    SortedMap<K,V> subMap(K fromKey, K toKey);

    SortedMap<K,V> headMap(K toKey);

    SortedMap<K,V> tailMap(K fromKey);

    K firstKey();

    K lastKey();

    Set<K> keySet();

    Collection<V> values();

    Set<Entry<K, V>> entrySet();
}
