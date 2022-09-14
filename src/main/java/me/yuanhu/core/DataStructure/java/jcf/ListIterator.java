package me.yuanhu.core.DataStructure.java.jcf;
import java.util.Iterator;

/*
    ListIterator是一个功能更加强大的迭代器, 它只能用于各种List类型的访问。可以通过调用listIterator()方法产生一个指向List开始处的ListIterator,
    还可以调用listIterator(n)方法创建一个一开始就指向列表索引为n的元素处的ListIterator.

    ListIterator和Iterator的不同点：
    1.使用范围不同，Iterator可以应用于所有的集合，Set、List和Map和这些集合的子类型。而ListIterator只能用于List及其子类型。
    2.ListIterator有add方法，可以向List中添加对象，而Iterator不能。
    3.ListIterator和Iterator都有hasNext()和next()方法，可以实现顺序向后遍历，但是ListIterator有hasPrevious()和previous()方法，可以实现逆向（顺序向前）遍历。Iterator不可以。
    4.ListIterator可以定位当前索引的位置，nextIndex()和previousIndex()可以实现。Iterator没有此功能。
    5.都可实现删除操作，但是ListIterator可以实现对象的修改，set()方法可以实现。Iterator仅能遍历，不能修改。
* */
public interface ListIterator<E> extends Iterator<E> {

    // Query Operations

    boolean hasNext();

    E next();

    //判断游标前面是否有元素;
    boolean hasPrevious();

    //返回游标前面的元素，同时游标前移一位。游标前没有元素就报 java.util.NoSuchElementException 的错，所以使用前最好判断一下;
    E previous();

    //返回游标后边元素的索引位置，初始为 0 ；遍历 N 个元素结束时为 N;
    int nextIndex();

    //返回游标前面元素的位置，初始时为 -1，游标前没有元素就报java.util.NoSuchElementException 错;
    int previousIndex();


    // Modification Operations

    //删除迭代器最后一次操作的元素，注意事项和 set 一样。
    void remove();

    //更新迭代器最后一次操作的元素为 E，也就是更新最后一次调用 next() 或者 previous() 返回的元素。
    //注意，当没有迭代，也就是没有调用 next() 或者 previous() 直接调用 set 时会报 java.lang.IllegalStateException 错;
    void set(E e);

    //在游标 前面 插入一个元素，注意，是前面
    void add(E e);

}