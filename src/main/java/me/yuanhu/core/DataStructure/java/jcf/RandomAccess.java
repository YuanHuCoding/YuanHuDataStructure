package me.yuanhu.core.DataStructure.java.jcf;

/**
 RandomAccess 接口是一个标志接口，本身并没有提供任何方法，任务凡是通过调用 RandomAccess 接口的对象都可以认为是支持快速随机访问的对象。
 此接口的主要目的是标识那些可支持快速随机访问的 List 实现。任何一个基于数组的 List 实现都实现了 RaodomAccess 接口，而基于链表的实现则都没有。
 因为只有数组能够进行快速的随机访问，而对链表的随机访问需要进行链表的遍历。因此，此接口的好处是，可以在应用程序中知道正在处理的 List 对象
 是否可以进行快速随机访问，从而针对不同的 List 进行不同的操作，以提高程序的性能。
 */
public interface RandomAccess {
}
