package me.yuanhu.core.DataStructure.list.single_linked_list;

import me.yuanhu.core.DataStructure.ILinkedList;
import me.yuanhu.core.DataStructure.IList;

/**
 * 循环单链表(独立头结点+尾指针)
 * https://blog.csdn.net/javazejian/article/details/52953190
 */
public class SingleLinkedList_Head_Tail_Cyclic<E> implements ILinkedList<E>, IList<E> {

    //===========数据结构========================================

    protected Node<E> head; //独立头结点
    protected Node<E> tail;//指向尾部的指针


    //===========构造函数========================================

    public SingleLinkedList_Head_Tail_Cyclic() {
        //初始化头结点与尾指针
        this.head = new Node<E>(null);
        this.head.next = this.head;
        this.tail = this.head;
    }

    public SingleLinkedList_Head_Tail_Cyclic(E[] array) {
        this();
        if (array != null && array.length > 0) {
            this.head.next = new Node<>(array[0], head);
            this.tail = this.head.next;
            int i = 1;
            while (i < array.length) {
                this.tail.next = new Node<>(array[i++]);
                this.tail.next.next = this.head;
                this.tail = this.tail.next;
            }
        }
    }

    @Override
    public int size() {
        int length = 0;
        Node<E> p = this.head.next;
        while (p != this.head) {
            p = p.next;
            length++;
        }
        return length;
    }

    @Override
    public boolean isEmpty() {
        return this.head.next == this.head;
    }

    @Override
    public void clear() {
        this.head.next = this.head;
        this.tail = this.head;
    }

    @Override
    public E get(int index) {
        if (index >= 0) {
            int j = 0;
            Node<E> pre = this.head.next;
            while (pre != null && j < index) {
                j++;
                pre = pre.next;
            }
            if (pre != null)
                return pre.data;
        }
        return null;
    }

    @Override
    public E set(int index, E data) {
        if (index >= 0) {
            int j = 0;
            Node<E> p = this.head.next;

            while (p != this.head && j < index) {
                j++;
                p = p.next;
            }

            //如果不是头结点
            if (p != this.head) {
                E old = p.data;
                p.data = data;

                return old;
            }
        }
        return null;
    }

    @Override
    public boolean add(int index, E data) {
        int size = size();
        if (index < 0 || index >= size)
            return false;

        int j = 0;
        Node<E> p = this.head;
        //寻找插入点的位置的前一个结点
        while (p.next != this.head && j < index) {
            p = p.next;
            j++;
        }

        //创建新结点,如果index=3,那么插入的位置就是第4个位置
        Node<E> q = new Node<>(data, p.next);
        p.next = q;
        //更新尾部指向
        if (p == tail) {
            this.tail = q;
        }
        return true;
    }

    @Override
    public boolean add(E data) {
        Node<E> q = new Node<>(data, this.tail.next);
        this.tail.next = q;
        //更新尾部指向
        this.tail = q;
        return true;
    }

    @Override
    public boolean contains(E data) {
        Node<E> p = this.head.next;
        while (p != this.head) {
            if ((data != null && data.equals(p.data)) || (data == null && p.data == null)) {
                return true;
            }
            p = p.next;
        }
        return false;
    }

    @Override
    public E remove(int index) {
        int size = size();
        if (index < 0 || index >= size || isEmpty()) {
            return null;
        }

        int j = 0;
        Node<E> p = this.head.next;

        while (p != this.head && j < index) {
            j++;
            p = p.next;
        }

        if (p != this.head) {
            E old = p.next.data;
            if (tail == p.next) {
                tail = p;
            }
            p.next = p.next.next;
            return old;
        }
        return null;
    }

    @Override
    public boolean remove(E data) {
        boolean isRemove = false;

        //用于记录要删除结点的前一个结点
        Node<E> front = this.head;
        //当前遍历的结点
        Node<E> pre = front.next;
        //查找所有数据相同的结点并删除
        while (pre != this.head) {
            if ((data!=null&&data.equals(pre.data))||(data==null&&pre.data==null)) {
                //如果恰好是尾部结点,则更新rear的指向
                if ((data!=null&&data.equals(tail.data))||(data==null&&tail.data==null)) {
                    this.tail = front;
                }
                //相等则删除pre并更改指针指向
                front.next = pre.next;
                pre = front.next;
                isRemove = true;
            } else {
                front = pre;
                pre = pre.next;
            }
        }
        return isRemove;
    }

    @Override
    public String toString() {
        String str = "(";
        Node<E> p = this.head.next;
        while (p != this.head) {
            str += p.data;
            p = p.next;
            if (p != this.head)
                str += ", ";
        }
        return str + ")";
    }


    /**
     * 单向链表节点
     */
    public class Node<T> {
        public T data;
        public Node<T> next;

        public Node(){
        }

        public Node(T data){
            this.data=data;
        }

        public Node(T data,Node<T> next){
            this.data=data;
            this.next=next;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "data=" + data +
                    ", next=" + next +
                    '}';
        }
    }

    public static void main(String[] args) {

        String[] letters = {"A", "B", "C",null, "E", "F"};
        SingleLinkedList_Head_Tail_Cyclic<String> list = new SingleLinkedList_Head_Tail_Cyclic<>(letters);

        System.out.println("list.get(3)->" + list.get(3));
        System.out.println("list:" + list.toString());

        System.out.println("list.add(4,Y)—>" + list.add(4, "Y"));
        System.out.println("list:" + list.toString());
        System.out.println("list.add(Z)—>" + list.add("Z"));
        System.out.println("list.add(null)—>" + list.add(null));
        System.out.println("list:" + list.toString());


        System.out.println("list.contains(Z)->" + list.contains("Z"));
        System.out.println("list.set(4,P)-->" + list.set(4, "P"));
        System.out.println("list:" + list.toString());

        System.out.println("list.removeAll(Z)->" + list.remove("Z"));
        System.out.println("list.remove(4)-->" + list.remove(4));
        System.out.println("list.removeAll(null)->" + list.remove(null));
        System.out.println("list:" + list.toString());
    }
}