package me.yuanhu.core.DataStructure.java.jcf;

import java.util.Iterator;
/*
*   Collection 作为集合的一个根接口，定义了一组对象和它的子类需要实现的 15 个方法
* */
public interface Collection<E> extends Iterable<E> {

    // Query Operations

    int size();

    boolean isEmpty();

    boolean contains(Object o);

    Iterator<E> iterator();

    Object[] toArray();

    /**
     1).参数指定空数组，节省空间
     String[] y = x.toArray(new String[0]);
     2).指定大数组参数浪费时间，采用反射机制
     String[] y = x.toArray(new String[100]);  //假设数组size大于100
     3).姑且认为最好的
     String[] y = x.toArray(new String[x.size()]);

     以下代码会出现ClassCastException
     List list = new ArrayList();
     list.add(new Long(1));
     list.add(new Long(2));
     list.add(new Long(3));
     list.add(new Long(4));
     Long[] l = (Long[])list.toArray();//这个语句会出现ClassCastException
     处理方式如下面代码：
     Long [] l = (Long []) list.toArray(new Long[list.size()]);
     */
    <T> T[] toArray(T[] a);



    // Modification Operations

    boolean add(E e);

    boolean remove(Object o);



    // Bulk Operations

    boolean containsAll(Collection<?> c);

    boolean addAll(Collection<? extends E> c);

    boolean removeAll(Collection<?> c);

    //两个集合求交集，只保留交集数据
    boolean retainAll(Collection<?> c);

    void clear();



    // Comparison and hashing

    boolean equals(Object o);

    int hashCode();

}
