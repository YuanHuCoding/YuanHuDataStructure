package me.yuanhu.core.DataStructure.list.single_linked_list;

import me.yuanhu.core.DataStructure.IList;
import me.yuanhu.core.DataStructure.ILinkedList;

/**
 *  单链表(带数据头结点,不含尾指针)
 *  https://blog.csdn.net/javazejian/article/details/52953190
 */
public class SingleLinkedList_DataHead_NoTail<E> implements ILinkedList<E>, IList<E> {

    //===========数据结构========================================

    protected Node<E> head; //带数据头结点

    //===========构造函数========================================

    public SingleLinkedList_DataHead_NoTail() {
    }

    public SingleLinkedList_DataHead_NoTail(Node<E> head) {
        this.head = head;
    }

    /**
     * 传入一个数组,转换成链表
     *
     * @param array
     */
    public SingleLinkedList_DataHead_NoTail(E[] array) {
        this.head = null;
        if (array != null && array.length > 0) {
            this.head = new Node<E>(array[0]);
            Node<E> rear = this.head;
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
        Node<E> p = head;
        while (p != null) {
            size++;
            p = p.next;
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return this.head == null;
    }

    @Override
    public void clear() {
        this.head = null;
    }

    @Override
    public E get(int index) {
        if (this.head != null && index >= 0) {
            int count = 0;
            Node<E> p = this.head;
            //找到对应索引的结点
            while (p != null && count < index) {
                p = p.next;
                count++;
            }
            if (p != null) {
                return p.data;
            }
        }
        return null;
    }

    @Override
    public E set(int index, E data) {
        if (this.head != null && index >= 0) {
            Node<E> pre = this.head;
            int count = 0;
            while (pre != null && count < index) {
                pre = pre.next;
                count++;
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
        //在头部插入
        if (this.head == null || index <= 1) {
            this.head = new Node<E>(data, this.head);
        } else {
            //在尾部或中间插入
            int count = 0;
            Node<E> front = this.head;
            while (front.next != null && count < index - 1) {
                front = front.next;
                count++;
            }
            //尾部添加和中间插入属于同种情况,毕竟当front为尾部结点时front.next=null
            front.next = new Node<E>(data, front.next);
        }
        return true;
    }

    //默认尾部插入
    @Override
    public boolean add(E data) {
        return add(Integer.MAX_VALUE, data);
    }

    @Override
    public E remove(int index) {
        E old = null;
        if (this.head != null && index >= 0) {
            //直接删除的是头结点
            if (index == 0) {
                old = this.head.data;
                this.head = this.head.next;
            } else {
                Node<E> front = this.head;
                int count = 0;
                //查找需要删除结点的前一个结点
                while (front.next != null && count < index - 1) {
                    front = front.next;
                    count++;
                }
                if (front.next != null) {
                    //获取旧值
                    old = front.next.data;
                    //更改指针指向
                    front.next = front.next.next;
                }
            }
        }
        return old;
    }

    @Override
    public boolean remove(E data) {
        boolean isRemove = false;
        if (this.head != null) {
            Node<E> front = this.head;
            Node<E> pre = front.next;
            //查找所有数据相同的结点并删除
            while (pre != null) {
                if ((data != null && data.equals(pre.data)) || (data == null && pre.data == null)) {
                    //更改指针指向
                    front.next = pre.next;
                    pre = front.next;
                    isRemove = true;
                } else {
                    front = pre;
                    pre = pre.next;
                }
            }
            //如果移除的是头结点
            if ((data != null && data.equals(this.head.data)) || (data == null && this.head.data == null)) {
                this.head = this.head.next;
                isRemove = true;
            }
        } else {
            isRemove = false;
        }
        return isRemove;
    }

    @Override
    public boolean contains(E data) {
        if (this.head != null && data != null) {
            Node<E> pre = this.head;
            while (pre != null) {
                if (data == null) {
                    if (pre.data == null) {
                        return true;
                    }
                } else if (data.equals(pre.data)) {
                    return true;
                }
                pre = pre.next;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String str = "(";
        Node<E> pre = this.head;
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

        String[] letters = {"Z", "Z", "A", "B", "B", null, null, "E", "F"};
        SingleLinkedList_DataHead_NoTail<String> list = new SingleLinkedList_DataHead_NoTail<>(letters);

        System.out.println("list.get(3)->" + list.get(3));
        System.out.println("list:" + list.toString());

        System.out.println("list.add(4,Y)—>" + list.add(4, "Y"));
        System.out.println("list.add(Z)—>" + list.add("Z"));
        System.out.println("list.add(null)—>" + list.add(null));
        System.out.println("list:" + list.toString());


        System.out.println("list.contains(Z)->" + list.contains("Z"));
        System.out.println("list.set(4,P)-->" + list.set(4, "P"));
        System.out.println("list:" + list.toString());


        System.out.println("list.removeAll(Z)->" + list.remove("Z"));
//        System.out.println("list.remove(4)-->" + list.remove(4));
        System.out.println("list:" + list.toString());


        System.out.println("list.removeAll(B)->" + list.remove("B"));
        System.out.println("list.removeAll(null)->" + list.remove(null));
        System.out.println("list:" + list.toString());
    }

}