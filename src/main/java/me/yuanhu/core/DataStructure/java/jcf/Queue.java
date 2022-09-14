package me.yuanhu.core.DataStructure.java.jcf;

/*
队列是数据结构中比较重要的一种类型，它支持 FIFO，尾部添加、头部删除（先进队列的元素先出队列），跟我们生活中的排队类似。

* Queue 是个接口，它提供的 add, offer 方法初衷是希望子类能够禁止添加元素为 null，这样可以避免在查询时返回 null 究竟是正确还是错误。
事实上大多数 Queue 的实现类的确响应了 Queue 接口的规定，比如 ArrayBlockingQueue，PriorityBlockingQueue 等等。
但还是有一些实现类没有这样要求，比如 LinkedList。
虽然 LinkedList 没有禁止添加 null，但是一般情况下 Queue 的实现类都不允许添加 null 元素，为啥呢？因为 poll(), peek() 方法在异常的时候会返回 null，
你添加了 null　以后，当获取时不好分辨究竟是否正确返回。
* */
public interface Queue<E> extends Collection<E> {

    // 增加一个元素到队尾，如果队列已满，则抛出一个IIIegaISlabEepeplian异常
    boolean add(E e);

    // 添加一个元素到队尾并返回true，如果队列已满，则返回false
    boolean offer(E e);

    // 移除并返回队列头部的元素，如果队列为空，则抛出一个NoSuchElementException异常
    E remove();

    // 移除并返问队列头部的元素，如果队列为空，则返回null
    E poll();

    // 返回队列头部的元素，如果队列为空，则抛出一个NoSuchElementException异常
    E element();

    // 返问队列头部的元素，如果队列为空，则返回null
    E peek();

}
