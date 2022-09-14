package me.yuanhu.core.DataStructure.queue;


import me.yuanhu.core.DataStructure.IQueue;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * 链式队列的实现
 */
public class Queue_Linked<E> implements IQueue<E>,Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 指向队头和队尾的结点
     * front==null&&rear==null时,队列为空
     */
    private Node<E> front, rear;

    private int size;

    /**
     * 用于控制最大容量,默认128,offer方法使用
     */
    private int maxSize = 128;

    public Queue_Linked() {
        //初始化队列
        this.front = this.rear = null;
    }

    @Override
    public int size() {
        return size;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean isEmpty() {
        return front == null && rear == null;
    }

    @Override
    public void clear() {
        this.front = this.rear = null;
        size = 0;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(E data) {
        Node<E> q = new Node<>(data, null);
        if (this.front == null) {//空队列插入
            this.front = q;
        } else {//非空队列,尾部插入
            this.rear.next = q;
        }
        this.rear = q;
        size++;
        return true;
    }

    /**
     * offer 方法可插入一个元素,这与add 方法不同，
     * 该方法只能通过抛出未经检查的异常使添加元素失败。
     * 而不是出现异常的情况，例如在容量固定（有界）的队列中
     * NullPointerException:data==null时抛出
     * IllegalArgumentException:队满,使用该方法可以使Queue的容量固定
     *
     * @param data
     * @return
     */
    @Override
    public boolean offer(E data) {
        if (data == null)
            throw new NullPointerException("The data can\'t be null");
        if (size >= maxSize)
            throw new IllegalArgumentException("The capacity of LinkedQueue has reached its maxSize:"+maxSize);

        Node<E> q = new Node<>(data, null);
        if (this.front == null) {//空队列插入
            this.front = q;
        } else {//非空队列,尾部插入
            this.rear.next = q;
        }
        this.rear = q;
        size++;
        return false;
    }

    /**
     * 出队,执行删除操作,返回队头元素,若队列为空,返回null
     *
     * @return
     */
    @Override
    public E poll() {
        if (this.isEmpty())
            return null;
        E x = this.front.data;
        this.front = this.front.next;
        if (this.front == null)
            this.rear = null;
        size--;
        return x;
    }

    /**
     * 出队,执行删除操作,若队列为空,抛出异常:NoSuchElementException
     *
     * @return
     */
    @Override
    public E remove() {
        if (isEmpty()) {
            throw new NoSuchElementException("The LinkedQueue is empty");
        }
        E x = this.front.data;
        this.front = this.front.next;
        if (this.front == null)
            this.rear = null;
        size--;
        return x;
    }

    /**
     * 返回队头元素,不执行删除操作,若队列为空,抛出异常:NoSuchElementException
     *
     * @return
     */
    @Override
    public E element() {
        if (isEmpty()) {
            throw new NoSuchElementException("The LinkedQueue is empty");
        }
        return this.front.data;
    }

    /**
     * 返回队头元素,不执行删除操作,若队列为空,返回null
     *
     * @return
     */
    @Override
    public E peek() {
        return this.isEmpty() ? null : this.front.data;
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
}