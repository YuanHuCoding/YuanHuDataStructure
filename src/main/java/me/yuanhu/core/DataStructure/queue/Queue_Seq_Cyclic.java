package me.yuanhu.core.DataStructure.queue;

import me.yuanhu.core.DataStructure.IQueue;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * 循环顺序队列
 *
 * 其中front、rear的下标的取值范围是0~size-1，不会造成假溢出。
 * front=(front+1)%size;//队头下标
 * rear=(rear+1)%size;
 * 约定队列满的条件为front=(rear+1)%size,注意此时队列中仍有一个空的位置，此处留一个空位主要用于避免与队列空的条件front=rear相同
 */
public class Queue_Seq_Cyclic<E> implements IQueue<E>,Serializable {

    private static final long serialVersionUID = -1L;

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private static final int DEFAULT_CAPACITY = 16;

    private Object elementData[];

    private int front, rear;

    private int size;

    public Queue_Seq_Cyclic() {
        this(DEFAULT_CAPACITY);
    }

    public Queue_Seq_Cyclic(int capacity) {
        elementData = new Object[capacity];
        front = rear = 0;
    }

    public void ensureCapacity(int capacity) {
        ensureCapacityInternal(capacity);
    }

    private void ensureCapacityInternal(int minCapacity) {
//        //判断是否满队
//        if (this.front == (this.rear + 1) % this.elementData.length) {
//            ensureCapacity(elementData.length * 2 + 1);
//        }

        if (minCapacity - elementData.length > 0) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        //当ArrayList扩容的时候，首先会设置新的存储能力为原来的1.5倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0) //申请的容量minCapacity比准备扩容旧容量的1.5 倍还大，就使用申请的容量minCapacity
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)//新的容量居然超出了 MAX_ARRAY_SIZE
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is x win:
        // minCapacity 一般跟元素个数 size 很接近，所以新建的数组容量为 newCapacity 更宽松些
        Object[] copy = new Object[newCapacity];
        System.arraycopy(this.elementData, this.front, copy, 0,this.size);
        this.elementData=copy;
        //恢复front,rear指向
        this.front = 0;
        this.rear = this.size;
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) elementData[index];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return this.front == this.rear;
    }

    @Override
    public void clear() {
        for (int i = this.front; i != this.rear; i = (i + 1) % elementData.length) {
            elementData[i] = null;
        }
        //复位
        this.front = this.rear = 0;
        size = 0;
    }

    @Override
    public Object[] toArray() {
        Object[] copy = new Object[this.size];
        System.arraycopy(this.elementData, this.front, copy, 0,this.size);
        return copy;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        T[] r = a.length >= size ? a :
                (T[])java.lang.reflect.Array
                        .newInstance(a.getClass().getComponentType(), size);
        System.arraycopy(this.elementData, this.front, r, 0,this.size);
        return r;
    }

    /**
     * data 入队,添加成功返回true,否则返回false,可扩容
     *
     * @param data
     * @return
     */
    @Override
    public boolean add(E data) {
        ensureCapacityInternal(size + 1);
        //添加data
        elementData[this.rear] = data;
        //更新rear指向下一个空元素的位置
        this.rear = (this.rear + 1) % elementData.length;
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
        //队满抛出异常
        if (this.front == (this.rear + 1) % this.elementData.length) {
            throw new IllegalArgumentException("The capacity of CyclicSeqQueue has reached its maximum");
        }

        //添加data
        elementData[this.rear] = data;
        //更新rear指向下一个空元素的位置
        this.rear = (this.rear + 1) % elementData.length;
        size++;
        return true;
    }

    /**
     * 出队,执行删除操作,返回队头元素,若队列为空,返回null
     *
     * @return
     */
    @Override
    public E poll() {
        E temp = elementData(this.front);
        this.front = (this.front + 1) % this.elementData.length;
        size--;
        return temp;
    }

    /**
     * 出队,执行删除操作,若队列为空,抛出异常:NoSuchElementException
     *
     * @return
     */
    @Override
    public E remove() {
        if (isEmpty()) {
            throw new NoSuchElementException("The CyclicSeqQueue is empty");
        }
        return poll();
    }


    /**
     * 返回队头元素,不执行删除操作,若队列为空,抛出异常:NoSuchElementException
     *
     * @return
     */
    @Override
    public E element() {
        if (isEmpty()) {
            throw new NoSuchElementException("The CyclicSeqQueue is empty");
        }
        return peek();
    }

    /**
     * 返回队头元素,不执行删除操作,若队列为空,返回null
     *
     * @return
     */
    @Override
    public E peek() {
        return elementData(front);
    }

    public static void main(String[] args){
        IQueue<Integer> s =new Queue_Seq_Cyclic<>();
        for(int i=0;i<66;i++){
            s.add(i);
        }
        System.out.println("size->"+s.size());

    }
}