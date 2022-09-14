package me.yuanhu.core.DataStructure.list.double_linked_list;

import me.yuanhu.core.DataStructure.ILinkedList;
import me.yuanhu.core.DataStructure.IList;

/**
 * 双链表(独立头结点+尾指针)
 * https://blog.csdn.net/javazejian/article/details/53047590
 */
public class DoubleLinkedList_Head_Tail<E> implements ILinkedList<E>, IList<E> {

    //===========数据结构========================================
    protected DNode<E> head; //独立头结点
    protected DNode<E> tail; //指向尾部的指针(与头结点不同，尾部指针不是独立的节点)

    //===========构造函数========================================

    public DoubleLinkedList_Head_Tail() {
        this.head = this.tail = new DNode<>(); //初始化头结点
    }

    /**
     * 传入一个数组,转换成链表
     *
     * @param array
     */
    public DoubleLinkedList_Head_Tail(E[] array) {
        this();
        if (array != null && array.length > 0) {
            this.head.next = new DNode<E>(array[0]);
            this.tail = this.head.next;
            this.tail.prev = this.head;
            int i = 1;
            while (i < array.length) {
                this.tail.next = new DNode<E>(array[i++]);
                this.tail.next.prev = this.tail;
                this.tail = this.tail.next;
            }
        }
    }

    //===========接口方法========================================

    @Override
    public int size() {
        int length = 0;
        DNode<E> cur = head.next;
        while (cur != null) {
            length++;
            cur = cur.next;
        }
        return length;
    }

    @Override
    public boolean isEmpty() {
        return head.next == null;
    }

    @Override
    public void clear() {
        this.head.next = null;
        this.tail = this.head;
    }

    @Override
    public E get(int index) {
        if (index >= 0) {
            int j = 0;
            DNode<E> cur = this.head.next;
            while (cur != null && j < index) {
                j++;
                cur = cur.next;
            }
            if (cur != null)
                return cur.data;
        }
        return null;
    }

    @Override
    public E set(int index, E data) {
        E old = null;
        if (index > 0) {
            int j = 0;
            DNode<E> cur = this.head.next;
            //查找需要替换的位置
            while (cur != null && j < index) {
                j++;
                cur = cur.next;
            }
            if (cur != null) {
                old = cur.data;
                //替换数据
                cur.data = data;
            }
        }
        return old;
    }

    @Override
    public boolean add(int index, E data) {
        if (index < 0) index = 0;

        int j = 0;
        DNode<E> cur = this.head;
        //查找要插入结点位置的前一个结点
        while (cur.next != null && j < index) {
            j++;
            cur = cur.next;
        }

        //创建需要插入的结点,并让其前继指针指向cur,后继指针指向cur.next
        DNode<E> q = new DNode<E>(data, cur, cur.next);

        //空双链表插入,需要确保cur.next不为空
        if (cur.next != null) {
            //更改cur.next的前继指针
            cur.next.prev = q;
        }
        //更改cur的后继指针
        cur.next = q;

        //在尾部插入时需要注意更新tail指向
        if (cur == this.tail) {
            this.tail = q;
        }

        return true;
    }

    @Override
    public boolean add(E data) {
        //创建新结点,并把其前继指针指向tail
        DNode<E> q = new DNode<E>(data, tail, null);
        tail.next = q;
        //更新尾部结点
        this.tail = q;
        return true;
    }

    @Override
    public boolean contains(E data) {
        DNode<E> cur = this.head.next;
        while (cur != null) {
            if ((data != null && data.equals(cur.data)) || (data == null && cur.data == null)) {
                return true;
            } else {
                cur = cur.next;
            }
        }
        return false;
    }

    /**
     * 根据下标删除结点
     * 1.头删除
     * 2.中间删除
     * 3.尾部删除,更新tail指向
     */
    @Override
    public E remove(int index) {
        int size = size();
        E temp = null;
        if (index < 0 || index >= size || isEmpty()) {
            return temp;
        }
        DNode<E> cur = this.head;
        int j = 0;
        //头删除/尾删除/中间删除,查找需要删除的结点(要删除的当前结点因此i<=index)
        while (cur != null && j <= index) {
            cur = cur.next;
            j++;
        }
        //当链表只有一个结点时,无需此步
        if (cur.next != null) {
            cur.next.prev = cur.prev;
        }
        cur.prev.next = cur.next;
        //如果是尾结点
        if (cur == this.tail) {
            this.tail = cur.prev;//更新未结点的指向
        }
        temp = cur.data;
        return temp;
    }

    /**
     * 根据data删除结点,无需像单向链表那样去存储要删除结点的前一个结点
     * 1.头删除
     * 2.中间删除
     * 3.尾部删除,更新tail指向
     */
    @Override
    public boolean remove(E data) {
        boolean isRemove = false;
        if (isEmpty())
            return isRemove;

        //注意这里的起点,如果起点为this.head,那么情况区别如同前面的根据index的删除实现
        DNode<E> cur = this.head.next;

        //头删除/尾删除/中间删除(size>1),查找所有需要删除的结点
        while (cur != null) {
            if ((data != null && data.equals(cur.data)) || (data == null && cur.data == null)) {
                if (cur == this.tail) {
                    //如果是尾结点
                    this.tail = this.tail.prev;//更新末结点的指向
                    this.tail.next = null;
                    cur.prev = null;
                } else {
                    //如果是在中间删除,更新前继和后继指针指向
                    cur.prev.next = cur.next;
                    cur.next.prev = cur.prev;
                }
                isRemove = true;
                cur = cur.next;//继续查找
            } else {
                cur = cur.next;
            }
        }
        return isRemove;
    }


    @Override
    public String toString() {
        String str = "(";
        DNode<E> cur = this.head.next;
        while (cur != null) {
            str += cur.data;
            cur = cur.next;
            if (cur != null)
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
//        String[] letters={"A"};
        DoubleLinkedList_Head_Tail<String> list = new DoubleLinkedList_Head_Tail<>(letters);

        System.out.println("list.get(3)->" + list.get(3));
        System.out.println("list:" + list.toString());

        System.out.println("list.add(4,Y)—>" + list.add(0, "Y"));
        System.out.println("list:" + list.toString());
        System.out.println("list.add(Z)—>" + list.add("Z"));
        System.out.println("list:" + list.toString());


        System.out.println("list.contains(Z)->" + list.contains("Z"));
        System.out.println("list.set(4,P)-->" + list.set(4, "P"));
        System.out.println("list:" + list.toString());


        System.out.println("list.remove(6)-->" + list.remove(6));
        System.out.println("list.remove(Z)->" + list.remove("Z"));
        System.out.println("list.remove(null)->" + list.remove(null));
        System.out.println("list:" + list.toString());
    }
}