package me.yuanhu.core.DataStructure.java.jcf;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/*
       一个 List 是一个元素有序的、可以重复、可以为 null 的集合（有时候我们也叫它“序列”）。
       在各种 List 中，最好的做法是以 ArrayList 作为默认选择。 当插入、删除频繁时，使用 LinkedList，Vector 总是比 ArrayList 慢，所以要尽量避免使用它.

       List接口同样也继承于Collection接口，但是与Set接口恰恰相反，List接口的集合类中的元素是对象有序且可重复。
       特征：有序且可重复。
       两个重要的实现类：ArrayList和LinkedList
       1.ArrayList特点是有序可重复的,代表长度可以改变的数组。可以对元素进行随机的访问，向ArrayList()中插入与删除元素的速度慢。
       2.LinkedList是一个双向链表结构的。插入和删除速度快，访问速度慢。
       除了具有 Collection 接口必备的 iterator() 方法外，List 还提供一个 listIterator() 方法，返回一个 ListIterator 接口。
       和标准的 Iterator 接口相比，ListIterator 多了一些 add() 之类的方法，允许添加、删除、设定元素、向前或向后遍历等功能。
* */
public interface List<E> extends Collection<E> {
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


    // Bulk Modification Operations

    boolean containsAll(Collection<?> c);

    boolean addAll(Collection<? extends E> c);

    boolean addAll(int index, Collection<? extends E> c);

    boolean removeAll(Collection<?> c);

    boolean retainAll(Collection<?> c);

    @SuppressWarnings({"unchecked", "rawtypes"})
    default void sort(Comparator<? super E> c) {
        Object[] a = this.toArray();
        Arrays.sort(a, (Comparator) c);
        ListIterator<E> i = this.listIterator();
        for (Object e : a) {
            i.next();
            i.set((E) e);
        }
    }

    void clear();


    // Comparison and hashing

    boolean equals(Object o);

    int hashCode();


    // Positional Access Operations

    E get(int index);

    E set(int index, E element);

    void add(int index, E element);

     E remove(int index);


    // Search Operations

    int indexOf(Object o);

    int lastIndexOf(Object o);


    // List Iterators

    ListIterator<E> listIterator();

    ListIterator<E> listIterator(int index);

    // View

    /*
    * 注意！ List.subList 方法并没有像我们想的那样：创建一个新的 List，然后把旧 List 的指定范围子元素拷贝进新 List，根！本！不！是！
    * subList 返回的扔是 List 原来的引用，只不过把开始位置 offset 和 size 改了下，见 List.subList() 在 AbstractList 抽象类中的实现.
    * */
    List<E> subList(int fromIndex, int toIndex);

}
