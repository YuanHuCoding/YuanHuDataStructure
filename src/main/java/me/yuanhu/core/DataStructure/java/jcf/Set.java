package me.yuanhu.core.DataStructure.java.jcf;
import java.util.Iterator;

/*
*   Set接口继承于Collection接口，它没有提供额外的方法，但实现了Set接口的集合类中的元素是无序且不可重复。
    特征：无序且不可重复。
    HashSet:HashSet类按照哈希算法来存取集合中的对象，存取速度比较快
    TreeSet:TreeSet类实现了SortedSet接口，能够对集合中的对象进行排序。
* */
public interface Set<E> extends Collection<E> {

    // Query Operations

    int size();

    boolean isEmpty();

    boolean contains(Object o);

    Iterator<E> iterator();

    Object[] toArray();

    <T> T[] toArray(T[] a);

    // Modification Operations

    boolean add(E e);

    boolean remove(Object o);

    // Bulk Operations

    boolean containsAll(Collection<?> c);

    boolean addAll(Collection<? extends E> c);

    boolean removeAll(Collection<?> c);

    boolean retainAll(Collection<?> c);

    void clear();

    // Comparison and hashing

    boolean equals(Object o);

    int hashCode();

}
