package me.yuanhu.core.DataStructure.stack;


import me.yuanhu.core.DataStructure.IStack;

import java.io.Serializable;
import java.util.EmptyStackException;

/**
 * 链栈(无头结点单向链表)
 * 用链表的首元结点来当栈顶，仅需要一个指针空间，链表头指针就是栈顶指针
 */
public class Stack_Linked<E> implements IStack<E>,Serializable {

    private static final long serialVersionUID = -1L;

    private Node<E> top;

    private int size;

    public Stack_Linked() {
        this.top = null;
    }

    @Override
    public int size(){
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.top == null;
    }

    @Override
    public void push(E data) {
        this.top = new Node<E>(data, this.top);// 头插入，新插入结点作为新的栈顶结点，其指针域指向原来的栈顶结点
        this.size++;
    }

    @Override
    public E pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }

        E data = this.top.data;
        this.top = this.top.next; //栈顶指针指向原栈顶结点的后继结点
        this.size--;
        return data;
    }

    @Override
    public E peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return this.top.data;
    }

    @Override
    public String toString(){
        if(isEmpty())               //如果空栈，所以返回();
            return "()";
        StringBuffer buffer = new StringBuffer("(");
        Node<E> node = this.top;        //获得链表的首元结点
        while(node != null){
            buffer.append(node.data.toString());
            if(node.next != null){
                buffer.append(",");
            }
            node = node.next;
        }
        buffer.append(")");
        return buffer.toString();
    }


    /**
     * 节点
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

    //测试
    public static void main(String[] args) {
        Stack_Linked<String> sl = new Stack_Linked<>();
        sl.push("A");
        sl.push("B");
        sl.push("C");
        System.out.println(sl.toString());
        int length=sl.size();
        for (int i = 0; i < length; i++) {
            System.out.println("sl.pop->"+sl.pop());
        }
    }
}