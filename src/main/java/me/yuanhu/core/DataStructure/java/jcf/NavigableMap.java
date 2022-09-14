package me.yuanhu.core.DataStructure.java.jcf;

/*
*  NavigableMap扩展了 SortedMap，具有了针对给定搜索目标返回最接近匹配项的导航方法。方法 lowerEntry、floorEntry、ceilingEntry
*  和 higherEntry 分别返回与小于、小于等于、大于等于、大于给定键的键关联的 Map.Entry 对象，如果不存在这样的键，则返回 null。
*  类似地，方法 lowerKey、floorKey、ceilingKey 和 higherKey 只返回关联的键。所有这些方法是为查找条目而不是遍历条目而设计的。
* */
public interface NavigableMap<K,V> extends SortedMap<K,V> {

    Entry<K,V> lowerEntry(K key);

    K lowerKey(K key);

    Entry<K,V> floorEntry(K key);

    K floorKey(K key);

    Entry<K,V> ceilingEntry(K key);

    K ceilingKey(K key);

    Entry<K,V> higherEntry(K key);

    K higherKey(K key);

    Entry<K,V> firstEntry();

    Entry<K,V> lastEntry();

    Entry<K,V> pollFirstEntry();

    Entry<K,V> pollLastEntry();

    NavigableMap<K,V> descendingMap();

    NavigableSet<K> navigableKeySet();

    NavigableSet<K> descendingKeySet();

    NavigableMap<K,V> subMap(K fromKey, boolean fromInclusive,
                             K toKey,   boolean toInclusive);

    NavigableMap<K,V> headMap(K toKey, boolean inclusive);

    NavigableMap<K,V> tailMap(K fromKey, boolean inclusive);

    SortedMap<K,V> subMap(K fromKey, K toKey);

    SortedMap<K,V> headMap(K toKey);

    SortedMap<K,V> tailMap(K fromKey);
}
