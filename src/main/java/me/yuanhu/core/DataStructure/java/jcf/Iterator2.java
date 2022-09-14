package me.yuanhu.core.DataStructure.java.jcf;

/*
    所有实现了Collection接口的容器类都有iterator方法，用于返回一个实现了Iterator接口的对象。
    Iterator对象称作迭代器，Iterator接口方法能以迭代方式逐个访问集合中各个元素，并可以在遍历过程中语法正确地删除元素。
    注意这个 [语法正确]，事实上我们在使用 Iterator 对容器进行迭代时如果修改容器 可能会报 ConcurrentModificationException 的错。
    官方称这种情况下的迭代器是 fail-fast 迭代器。
* */
public interface Iterator2<E> {

    boolean hasNext();

    E next();

    default void remove() {
        throw new UnsupportedOperationException("remove");
    }

}