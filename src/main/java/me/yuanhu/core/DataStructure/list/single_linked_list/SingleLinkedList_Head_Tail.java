package me.yuanhu.core.DataStructure.list.single_linked_list;

import me.yuanhu.core.DataStructure.ILinkedList;
import me.yuanhu.core.DataStructure.IList;

/**
 * 单链表(独立头结点+尾指针)
 * https://blog.csdn.net/javazejian/article/details/52953190
 */
public class SingleLinkedList_Head_Tail<E> implements ILinkedList<E>, IList<E> {

    //===========数据结构========================================

    protected Node<E> headNode; //独立头结点
    protected Node<E> rear;//指向尾部的指针(与头结点不同，尾部指针不是独立的节点)

    //===========构造函数========================================

    public SingleLinkedList_Head_Tail() {
        //初始化头结点与尾指针
        this.headNode = this.rear = new Node<>(null);
    }

    /**
     * 传入一个数组,转换成链表
     *
     * @param array
     */
    public SingleLinkedList_Head_Tail(E[] array) {
        this();
        if (array != null && array.length > 0) {
            this.headNode.next = new Node<E>(array[0]);
            rear = this.headNode.next;
            int i = 1;
            while (i < array.length) {
                rear.next = new Node<E>(array[i++]);
                rear = rear.next;
            }
        }
    }

    //===========接口方法========================================

    @Override
    public int size() {
        int size = 0;
        Node<E> currentNode = headNode.next;
        while (currentNode != null) {
            size++;
            currentNode = currentNode.next;
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return this.headNode.next == null;
    }

    @Override
    public void clear() {
        this.headNode.next = null;
        this.rear = this.headNode;
    }

    @Override
    public E get(int index) {
        if (index >= 0) {
            int j = 0;
            Node<E> pre = this.headNode.next;
            //找到对应索引的结点
            while (pre != null && j < index) {
                pre = pre.next;
                j++;
            }

            if (pre != null) {
                return pre.data;
            }
        }
        return null;
    }

    @Override
    public E set(int index, E data) {
        if (index >= 0) {
            Node<E> pre = this.headNode.next;
            int j = 0;
            while (pre != null && j < index) {
                pre = pre.next;
                j++;
            }

            if (pre != null) {
                E oldData = pre.data;
                pre.data = data;//设置新值
                return oldData;
            }

        }
        return null;
    }

    /**
     * 根据下标添加结点
     * 1.头部插入
     * 2.中间插入
     * 3.末尾插入
     */
    @Override
    public boolean add(int index, E data) {
        if (index < 0)
            throw new NullPointerException("index can\'t less than 0");

        //无需区分位置操作,中间/头部/尾部插入
        int j = 0;
        Node<E> pre = this.headNode;
        while (pre.next != null && j < index) {
            pre = pre.next;
            j++;
        }

        //将新插入的结点的后继指针指向pre.next
        Node<E> q = new Node<E>(data, pre.next);
        //更改指针指向
        pre.next = q;

        //如果是尾指针
        if (pre == this.rear)
            this.rear = q;

        return true;
    }

    @Override
    public boolean add(E data) {
        this.rear.next = new Node<E>(data);
        //更新末尾指针的指向
        this.rear = this.rear.next;
        return true;
    }

    @Override
    public boolean contains(E data) {
        Node<E> pre = this.headNode.next;
        while (pre != null) {
            if ((data != null && data.equals(pre.data)) || (data == null && pre.data == null)) {
                return true;
            }
            pre = pre.next;
        }
        return false;
    }

    @Override
    public E remove(int index) {
        E old = null;

        //包含了头删除或中间删除或尾部删除的情况
        if (index >= 0) {

            Node<E> pre = this.headNode;
            int j = 0;
            //查找需要删除位置的前一个结点
            while (pre.next != null && j < index) {
                pre = pre.next;
                j++;
            }

            //获取到要删除的结点
            Node<E> r = pre.next;

            if (r != null) {
                //获取旧值
                old = r.data;
                //如果恰好是尾部结点,则更新rear的指向
                if (r == this.rear) {
                    this.rear = pre;
                }
                //更改指针指向
                pre.next = r.next;
            }

        }
        return old;
    }

    @Override
    public boolean remove(E data) {
        boolean isRemove = false;
        //用于记录要删除结点的前一个结点
        Node<E> front = this.headNode;
        //当前遍历的结点
        Node<E> pre = front.next;
        //查找所有数据相同的结点并删除
        while (pre != null) {
            if ((data != null && data.equals(pre.data)) || (data == null && pre.data == null)) {
                //如果恰好是尾部结点,则更新rear的指向
                if ((data != null && data.equals(rear.data)) || (data == null && rear.data == null)) {
                    this.rear = front;
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
        Node<E> pre = this.headNode.next;
        while (pre != null) {
            str += pre.data;
            pre = pre.next;
            if (pre != null)
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

        String[] letters = {"A", "B", "C", null, "E", "F"};
        SingleLinkedList_Head_Tail<String> list = new SingleLinkedList_Head_Tail<>(letters);

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

        System.out.println("list.remove(Z)->" + list.remove("Z"));
        System.out.println("list.remove(4)-->" + list.remove(4));

        System.out.println("list.remove(null)->" + list.remove(null));
        System.out.println("list:" + list.toString());
    }

}