package me.yuanhu.core.DataStructure.list.double_linked_list;

import me.yuanhu.core.DataStructure.ILinkedList;
import me.yuanhu.core.DataStructure.IList;

/**
 * 循环双链表(独立头结点,不需要尾部指针)
 * https://blog.csdn.net/javazejian/article/details/53047590
 */
public class DoubleLinkedList_Head_NoTail_Cyclic<E> implements ILinkedList<E>, IList<E> {

    //===========数据结构========================================

    public DNode<E> head; //不带数据的头结点
    //   protected DNode<T> tail; //指向尾部的指针(循环链表可以不需要尾部指针)

    //===========构造函数========================================

    public DoubleLinkedList_Head_NoTail_Cyclic() {
        this.head = new DNode<>();//初始化头结点
        this.head.next = head;
        this.head.prev = head;
    }


    /**
     * 传入一个数组,转换成链表
     *
     * @param array
     */
    public DoubleLinkedList_Head_NoTail_Cyclic(E[] array) {
        this();
        if (array != null && array.length > 0) {
            DNode<E> cur = new DNode<>(array[0]);
            head.next = cur;
            head.prev = cur;
            cur.prev = head;
            cur.next = head;

            int i = 1;
            while (i < array.length) {
                cur.next = new DNode<>(array[i++], cur, head);
                head.prev = cur.next;
                cur = cur.next;
            }
        }
    }

    //===========接口方法========================================

    @Override
    public int size() {
        int length = 0;
        DNode<E> cur = this.head.next;
        while (cur != this.head) {
            length++;
            cur = cur.next;
        }
        return length;
    }

    @Override
    public boolean isEmpty() {
        return this.head.next == head;//循环双链表的后继指针指向自己说明是空链表
    }

    @Override
    public void clear() {
        this.head.prev = head;
        this.head.next = head;
    }

    @Override
    public E get(int index) {
        if (index >= 0) {
            int j = 0;
            DNode<E> cur = this.head.next;
            while (cur != head && j < index) {
                j++;
                cur = cur.next;
            }
            if (cur != head)
                return cur.data;
        }
        return null;
    }

    @Override
    public E set(int index, E data) {
        if (index >= 0) {
            int j = 0;
            DNode<E> cur = this.head.next;

            while (cur != head && j < index) {
                j++;
                cur = cur.next;
            }

            //如果不是头结点
            if (cur != head) {
                E old = cur.data;
                cur.data = data;

                return old;
            }
        }
        return null;
    }

    /**
     * 根据index添加
     * 循环链表中无论是prev还是next都不存在空的情况,因此添加时
     * 无论是头部还是尾部还是中,都视为一种情况对待
     *
     */
    @Override
    public boolean add(int index, E data) {
        int size = size();
        if ( index < 0 || index >= size)
            return false;

        int j = 0;
        DNode<E> cur = this.head;
        //寻找插入点的位置
        while (cur.next != head && j < index) {
            cur = cur.next;
            j++;
        }

        //创建新结点,如果index=3,那么插入的位置就是第4个位置
        DNode<E> q = new DNode<>(data, cur, cur.next);
        cur.next.prev=q;
        cur.next = q;

        return true;
    }

    /**
     * 尾部添加
     */
    @Override
    public boolean add(E data) {
        //创建新结点,让前继指针指向head.pre,后继指针指向head
        DNode<E> cur = new DNode<>(data, head.prev, head);
        //更新tail后继指针的指向
        this.head.prev.next = cur;
        this.head.prev = cur;
        return true;
    }

    @Override
    public boolean contains(E data) {
        DNode<E> cur = this.head.next;
        while (cur != head) {
            if ((data != null && data.equals(cur.data)) || (data == null && cur.data == null)) {
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    @Override
    public E remove(int index) {
        E old = null;
        int size = size();

        if (index < 0 || index >= size)
            return old;

        int j = 0;
        DNode<E> cur = this.head.next;

        while (cur != head && j < index) {
            j++;
            cur = cur.next;
        }

        if (cur != head) {
            old = cur.data;
            cur.prev.next = cur.next;
            cur.next.prev = cur.prev;
        }
        return old;
    }

    @Override
    public boolean remove(E data) {
        boolean isRemove = false;

        DNode<E> cur = this.head.next;
        while (cur != head) {
            if ((data != null && data.equals(cur.data)) || (data == null && cur.data == null)) {
                cur.prev.next = cur.next;
                cur.next.prev = cur.prev;
                isRemove = true;
            }
            cur = cur.next;
        }
        return isRemove;
    }


    @Override
    public String toString() {
        String str = "(";
        DNode<E> cur = this.head.next;
        while (cur != head) {
            str += cur.data;
            cur = cur.next;
            if (cur != head)
                str += ", ";
        }
        return str + ")";
    }


    /**
     * 双链表结点
     */
    public class DNode<T> {

        public T data;
        public DNode<T> prev, next;//前继指针和后继指针

        public DNode(T data, DNode<T> prev, DNode<T> next)
        {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public DNode(T data)
        {
            this(data, null, null);
        }

        public DNode()
        {
            this(null, null, null);
        }

        public String toString()
        {
            return this.data==null? "null":this.data.toString();
        }


    }

    public static void main(String[] args) {

        String[] letters = {"A", "B", "C", "D", null, "Z", "E", "F"};
        DoubleLinkedList_Head_NoTail_Cyclic<String> list = new DoubleLinkedList_Head_NoTail_Cyclic<>(letters);

        System.out.println("init list-->" + list.toString());

        System.out.println("list.get(3)->" + list.get(3));
        System.out.println("list:" + list.toString());

        System.out.println("list.add(4,Y)—>" + list.add(4, "Y"));
        System.out.println("list:" + list.toString());
        System.out.println("list.add(Z)—>" + list.add("Z"));
        System.out.println("list:" + list.toString());


        System.out.println("list.contains(Z)->" + list.contains("Z"));
        System.out.println("list.set(4,P)-->" + list.set(4, "P"));
        System.out.println("list:" + list.toString());


        System.out.println("list.remove(3)-->" + list.remove(3));
        System.out.println("list:" + list.toString());
        System.out.println("list.remove(Z)->" + list.remove("Z"));
        System.out.println("list:" + list.toString());

        System.out.println("list.remove(null)->" + list.remove(null));
        System.out.println("list:" + list.toString());

    }
}