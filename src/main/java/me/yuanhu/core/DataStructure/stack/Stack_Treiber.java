package me.yuanhu.core.DataStructure.stack;

import me.yuanhu.core.DataStructure.IStack;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;
/**
 * https://www.sczyh30.com/posts/Concurrency/concurrency-treiber-stack/
 * Concurrent stack implementation
 * Treiber's Algorithm
 * 我们使用了 AtomicReference 来实现 Treiber Stack。每当我们 push 进去一个元素的时候，我们首先根据要添加的元素创建
 * 一个 Node，然后获取原栈顶结点，并将新结点的下一个结点指向原栈顶结点。此时我们使用 CAS 操作来更改栈顶结点，如果此时的
 * 栈顶和之前的相同，代表 CAS 操作成功，那么就把新插入的元素设为栈顶；如果此时的栈顶和之前的不同（即其他线程改变了栈顶结点），
 * CAS 操作失败，那么需要重复上述操作（更新当前的栈顶元素并且重设 next），直到成功。pop 操作的原理也相似。
 */
public class Stack_Treiber<E> implements IStack<E>, Serializable {

    private static final long serialVersionUID = -1L;

    private AtomicReference<Node<E>> top = new AtomicReference<>();

    private int size;

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return top.get()==null;
    }

    @Override
    public void push(E elem) {
        Node<E> newHead = new Node<>(elem);
        Node<E> oldHead;
        do {
            oldHead = top.get();
            newHead.next = oldHead;
        } while (!top.compareAndSet(oldHead, newHead));
        this.size++;
    }

    @Override
    public E pop() {
        Node<E> oldHead;
        Node<E> newHead;
        do {
            oldHead = top.get();
            if (oldHead == null) {
                throw new StackEmptyException();
            }
            newHead = oldHead.next;
        } while (!top.compareAndSet(oldHead, newHead));
        this.size--;
        return oldHead.item;
    }

    @Override
    public E peek() {
        Node<E> head =top.get();
        if(head==null) {
            throw new StackEmptyException();
        }
        return head.item;
    }

    /*
    java允许我们在一个类里面定义静态类。比如内部类（nested class）。把nested class封闭起来的类叫外部类。
    在java中，我们不能用static修饰顶级类（top level class）。只有内部类可以为static。
     静态内部类和非静态内部类之间到底有什么不同呢？下面是两者间主要的不同。
    （1）内部静态类不需要有指向外部类的引用。但非静态内部类需要持有对外部类的引用。
    （2）非静态内部类能够访问外部类的静态和非静态成员。静态类不能访问外部类的非静态成员。他只能访问外部类的静态成员。
    （3）一个非静态内部类不能脱离外部类实体被创建，一个非静态内部类可以访问外部类的数据和方法，因为他就在外部类里面。
    * */
    private static class Node<E> {
        public final E item;
        public Node<E> next;
        public Node(E item) {
            this.item = item;
        }
    }

}
