package me.yuanhu.core.DataStructure.counter;

//https://www.xuejiashopping.com/41.html
public interface Counter {

    void add(long delta);

    void increment();

    void decrement();

    long value();

}
