package me.yuanhu.core.DataStructure.collection;

import me.yuanhu.core.DataStructure.ILinkedList;
import me.yuanhu.core.DataStructure.IList;

import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * 排序list的简单实现
 */
public class LinkedList_Sort<E extends Comparable<? extends E>> extends LinkeList<E> implements ILinkedList<E>, IList<E>, Serializable {

    private static final long serialVersionUID = -4783131709270334156L;

    @Override
    public boolean add(E data) {
        if(data==null)
            throw new NullPointerException("data can\'t be null");

        Comparable cmp =data;//这里需要转一下类型,否则idea编辑器上检验不通过.

        if(this.isEmpty() || cmp.compareTo(this.last.prev.data) > 0){
            return super.add(data);//直接尾部添加,last不带数据的尾结点
        }

        Node<E> p=this.first.next;
        //查找插入点
        while (p!=null&&cmp.compareTo(p.data)>0)
            p=p.next;

        Node<E> q=new Node<>(p.prev,data,p);
        p.prev.next=q;
        p.prev=q;

        size++;
        //记录修改
        modCount++;

        return true;
    }

    /**
     * 不根据下标插入,只根据比较大小插入
     * @param index
     * @param data
     */
    @Override
    public boolean add(int index, E data) {
        return this.add(data);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    //测试
    public static void main(String[] args){
        LinkedList_Sort<Integer> list=new LinkedList_Sort<>();
        list.add(50);
        list.add(40);
        list.add(80);
        list.add(20);
        print(list);
    }

    public static void print(LinkedList_Sort mylinkeList){
        for (int i=0;i<mylinkeList.size();i++) {
            System.out.println("i->"+mylinkeList.get(i));
        }
    }
}