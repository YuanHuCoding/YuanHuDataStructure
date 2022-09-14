package me.yuanhu.core.DataStructure.stack.Offer;

import me.yuanhu.core.DataStructure.IQueue;
import me.yuanhu.core.DataStructure.stack.Stack_Seq;
import me.yuanhu.core.DataStructure.IStack;

import java.util.NoSuchElementException;

/*
* 用两个栈实现一个队列

小白版
初始化两个栈S1和S2。
S1作为元素的存储空间，S2作为数据的临时缓冲区
入队的时候，将数据压入栈S1中
出队的时候，将S1中的元素依次出栈，并且压入栈S2中，然后将S2中的栈顶元素出栈。
出队之后，将S2中的数据元素倒回到栈S1中

升级版
入队时，先判断S1是否为空，如不为空，说明所有元素都在S1，此时将入队元素直接压入S1；如为空，要将S2的元素逐个“倒回”S1，再压入入队元素。
出队时，先判断S2是否为空，如不为空，直接弹出S2的顶元素并出队；如为空，将S1的元素逐个“倒入”S2，把最后一个元素弹出并出队。
这种升级版可以在每次出队之后不用将栈S2中的元素倒回到栈S1中，对于频繁的出队操作效率更高。

大师版
入队时，将元素压入s1。
出队时，判断s2是否为空，如不为空，则直接弹出顶元素；如为空，则将s1的元素逐个“倒入”s2，把最后一个元素弹出并出队。
这个大师版，避免了反复“倒”栈，仅在需要时才“倒”一次

* */
public class StacksToQueue<T> implements IQueue<T> {

    private static final int CAPACITY = 64;

    private IStack<T> stack1 = null;
    private IStack<T> stack2 = null;

    private int queue_capacity;


    public StacksToQueue(){
        this(CAPACITY);
    }

    public StacksToQueue(int capacity) {
        queue_capacity = capacity;
        stack1 = new Stack_Seq<T>(queue_capacity);
        stack2 = new Stack_Seq<T>(queue_capacity);
    }


    @Override
    public int size() {
        return stack1.size()+ stack2.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean add(T data) {
        //入对的时候将数据元素压入栈S1中
        if (data==null)
            throw new NullPointerException("The data can\'t be null");
        stack1.push(data);
        return true;
    }

    @Override
    public boolean offer(T data) {
        return add(data);
    }

    @Override
    public T poll() {
        if(isEmpty()){
            return null;
        }
       return remove();
    }

    @Override
    public T remove() {
        if(stack2.isEmpty()){
            //如果S1不为空，将S1出栈的元素一次入栈到S2中
            while (!stack1.isEmpty()) {
                stack2.push(stack1.pop());
            }
        }

        //将S2的栈顶元素出栈，即出队。
        T first = stack2.pop();
        return first;
    }

    @Override
    public T peek() {
        T first = poll();
        if(first!=null){
            offer(first);
        }
        return first;
    }

    @Override
    public T element() {
        if(isEmpty()){
            throw new NoSuchElementException("The Queue is empty");
        }
        return peek();
    }

    @Override
    public void clear() {
        while (!stack1.isEmpty()) {
            stack1.pop();
        }
        while (!stack2.isEmpty()) {
            stack2.pop();
        }
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }


    public static void main(String[] args) {
        IQueue<Integer> myQueue = new StacksToQueue<>();
        myQueue.add(1);
        myQueue.add(2);
        myQueue.add(3);
        myQueue.add(4);
        System.out.println(myQueue.poll());
        System.out.println(myQueue.poll());
        myQueue.add(5);
        myQueue.add(6);
        System.out.println(myQueue.poll());
        System.out.println(myQueue.poll());
        System.out.println("size:"+myQueue.size());
        System.out.println("peek:"+myQueue.peek());
    }

}