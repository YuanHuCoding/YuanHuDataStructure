package me.yuanhu.core.DataStructure.stack;

import me.yuanhu.core.DataStructure.IStack;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 顺序栈的实现
 */
public class Stack_Seq<E> implements IStack<E>,Serializable {
    private static final long serialVersionUID = -1L;

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private static final int DEFAULT_CAPACITY = 64;//默认的初始化数组大小

    /**
     * 栈顶指针,-1代表空栈
     */
    private int top=-1;

    private E[] elementData;//数组声明,用于存储元素

    private int size;


    public Stack_Seq(int initialCapacity){
        //申请数组存储空间,元素初始值都是null
        if (initialCapacity > 0) {
            this.elementData = (E[])new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = (E[])new Object[0];
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
        }
    }


    public Stack_Seq(){
        this(DEFAULT_CAPACITY);
    }

    @Override
    public  int size(){
        return size;
    }

    @Override
    public boolean isEmpty() {
        return this.top == -1;
    }

    private void ensureCapacityInternal(int minCapacity) {
        // 如果最低要求的存储能力>ArrayList已有的存储能力，这就表示ArrayList的存储能力不足，因此需要调用 grow();方法进行扩容
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
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }
    /**
     * 添加元素,从栈顶(数组尾部)插入
     * @param data
     */
    @Override
    public void push(E data) {
        ensureCapacityInternal(size + 1);

        //从栈顶添加元素
        elementData[++top]=data;
        size++;
    }


    /**
     * 从栈顶(顺序表尾部)删除
     * @return
     */
    @Override
    public E pop() {
        if(isEmpty())
            throw new StackEmptyException();
        size--;
        return elementData[top--];
    }

    /**
     * 获取栈顶元素的值,不删除
     * @return
     */
    @Override
    public E peek() {
        if(isEmpty())
            throw new StackEmptyException();
        return elementData[top];
    }

    public static void main(String[] args){
//        SeqStack<String> s=new SeqStack<>();
//        s.push("A");
//        s.push("B");
//        s.push("C");
//        System.out.println("size->"+s.size());
//        int l=s.size();//size 在减少,必须先记录
//        for (int i=0;i<l;i++){
//            System.out.println("s.pop->"+s.pop());
//        }

        //System.out.println("s.peek->"+s.peek());

        Stack_Seq<Integer> s=new Stack_Seq<>();
        s.push(1);
        s.push(2);
        s.push(3);
        System.out.println("size->"+s.size());
        int l=s.size();//size 在减少,必须先记录
        for (int i=0;i<l;i++){
            System.out.println("s.pop->"+s.pop());
        }

    }
}