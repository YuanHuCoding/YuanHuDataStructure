package me.yuanhu.core.DataStructure.java.jcf;
import java.util.Iterator;

/*
*  Deque 是一个双向队列，也就是既可以先入先出，又可以先入后出，再直白一点就是既可以在头部添加元素又在尾部添加元素，既可以在头部获取元素又可以在尾部获取元素。
  一般场景
      LinkedList 大小可变的链表双端队列，允许元素为 null
      ArrayDeque 大下可变的数组双端队列，不允许 null
  并发场景
      LinkedBlockingDeque 如果队列为空时，获取操作将会阻塞，直到有元素添加

  Deque 与 工作密取
    在并发编程 中，双端队列 Deque 还用于 “工作密取” 模式。
    什么是工作密取呢？
    在 生产者-消费者 模式中，所有消费者都从一个工作队列中取元素，一般使用阻塞队列实现；
    而在 工作密取 模式中，每个消费者有其单独的工作队列，如果它完成了自己双端队列中的全部工作，那么它就可以从其他消费者的双端队列末尾秘密地获取工作。
    工作密取 模式 对比传统的 生产者-消费者 模式，更为灵活，因为多个线程不会因为在同一个工作队列中抢占内容发生竞争。在大多数时候，它们只是访问自己的双端队列。
    即使需要访问另一个队列时，也是从 队列的尾部获取工作，降低了队列上的竞争程度。
* */
public interface Deque<E> extends Queue<E> {

    void addFirst(E e);

    void addLast(E e);

    boolean offerFirst(E e);

    boolean offerLast(E e);

    E removeFirst();

    E removeLast();

    E pollFirst();

    E pollLast();

    E getFirst();

    E getLast();

    E peekFirst();

    E peekLast();

    boolean removeFirstOccurrence(Object o);

    boolean removeLastOccurrence(Object o);

    // *** Queue methods ***

    boolean add(E e);

    boolean offer(E e);

    E remove();

    E poll();

    E element();

    E peek();


    // *** Stack methods ***

    void push(E e);

    E pop();


    // *** Collection methods ***

    boolean remove(Object o);

    boolean contains(Object o);

    int size();

    Iterator<E> iterator();

    Iterator<E> descendingIterator();

}
