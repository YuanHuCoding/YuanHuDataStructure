package me.yuanhu.core.DataStructure.java.jcf;
import java.util.Iterator;

/*
   NavigableSet扩展了 SortedSet，具有了为给定搜索目标报告最接近匹配项的导航方法。方法 lower、floor、ceiling 和 higher 分别
   返回小于、小于等于、大于等于、大于给定元素的元素，如果不存在这样的元素，则返回 null。
*/
public interface NavigableSet<E> extends SortedSet<E> {

    E lower(E e);

    E floor(E e);

    E ceiling(E e);

    E higher(E e);

    E pollFirst();

    E pollLast();

    Iterator<E> iterator();

    NavigableSet<E> descendingSet();

    Iterator<E> descendingIterator();

    NavigableSet<E> subSet(E fromElement, boolean fromInclusive,
                           E toElement,   boolean toInclusive);

    NavigableSet<E> headSet(E toElement, boolean inclusive);

    NavigableSet<E> tailSet(E fromElement, boolean inclusive);

    SortedSet<E> subSet(E fromElement, E toElement);

    SortedSet<E> headSet(E toElement);

    SortedSet<E> tailSet(E fromElement);
}
