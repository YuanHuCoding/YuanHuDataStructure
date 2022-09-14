package me.yuanhu.core.DataStructure;

public interface IST<Key, Value> {

    // 返回符号表中的元素数量
    int size();

    // 符号表是否为空
    boolean isEmpty();

    // 键key是否在表中有对应的值
    boolean contains(Key key);

    //往集合中插入一条键值对记录，如果value为空，不添加
    void put(Key key, Value val);

    // 根据key查找value，如果没找到返回null
    Value get(Key key);

    // 删除键为key的记录
    void delete(Key key);

    // 返回表中所有键的集合
    Iterable<Key> keys();

}