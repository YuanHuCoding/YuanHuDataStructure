package me.yuanhu.core.DataStructure;

public interface IOrderedST<Key extends Comparable<Key>, Value> extends IST<Key, Value> {

    // 返回符号表中的元素数量
    int size();

    // 符号表是否为空
    boolean isEmpty();

    // 键key是否在表中有对应的值
    boolean contains(Key key);

    //往集合中插入一条键值对记录，如果value为空，不添加
    //寻找键,找到修改值,没有找到则新创建一个键值对
    void put(Key key, Value val);

    // 根据key查找value，如果没找到返回null
    Value get(Key key);

    // 删除键为key的记录
    void delete(Key key);


    //================有序符号表额外的api===========

    //最小的键
    Key min();

    //最大的键
    Key max();

    //小于等于key的键的数量
    Key floor(Key key);

    //大于等于key的键的数量
    Key ceiling(Key key);

    //获取key的位置
    int rank(Key key);

    //排位为i的键
    Key select(int i);

    //删除最小的键
    void deleteMin();

    //删除最大的键
    void deleteMax();

    //[lo,hi]之间键的数量
    int size(Key lo, Key hi);

    //[lo,hi]之间所有的键，已排序
    Iterable<Key> keys(Key lo, Key hi);

    //有序表中所有的键，已排序
    Iterable<Key> keys();

}