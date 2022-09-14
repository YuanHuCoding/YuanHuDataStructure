package me.yuanhu.core.DataStructure.symboltable;

import me.yuanhu.core.DataStructure.IST;
import me.yuanhu.core.DataStructure.java.jcf.LinkedList;
import me.yuanhu.core.DataStructure.java.jcf.Queue;

//基于 散列表(链地址法) 的符号表
public class SeparateChainingHashST<Key, Value> implements IST<Key, Value> {
    private static final int INIT_CAPACITY = 4;

    private int n;                                // number of key-value pairs  // 键值对的总数
    private int m;                                // hash table size  // 散列表的大小
    private SequentialSearchST<Key, Value>[] st;  // array of linked-list symbol tables  // 存放链表对象的数组


    public SeparateChainingHashST() {
        this(INIT_CAPACITY);
    }

    public SeparateChainingHashST(int m) {
        this.m = m; //创建M条链表
        st = (SequentialSearchST<Key, Value>[]) new SequentialSearchST[m];
        for (int i = 0; i < m; i++)
            st[i] = new SequentialSearchST<Key, Value>();
    }

    // resize the hash table to have the given number of chains,
    // rehashing all of the keys
    private void resize(int chains) {
        SeparateChainingHashST<Key, Value> temp = new SeparateChainingHashST<Key, Value>(chains);
        for (int i = 0; i < m; i++) {
            for (Key key : st[i].keys()) {
                temp.put(key, st[i].get(key));
            }
        }
        this.m  = temp.m;
        this.n  = temp.n;
        this.st = temp.st;
    }

    // hash value between 0 and m-1
    private int hash(Key key) {
        return (key.hashCode() & 0x7fffffff) % m;
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
    }

    @Override
    public Value get(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        int i = hash(key);
        return st[i].get(key);
    }

    @Override
    public void put(Key key, Value val) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        if (val == null) {
            delete(key);
            return;
        }

        // double table size if average length of list >= 10
        if (n >= 10*m) resize(2*m);

        int i = hash(key);
        if (!st[i].contains(key)) n++;
        st[i].put(key, val);
    }

    @Override
    public void delete(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");

        int i = hash(key);
        if (st[i].contains(key)) n--;
        st[i].delete(key);

        // halve table size if average length of list <= 2
        if (m > INIT_CAPACITY && n <= 2*m) resize(m/2);
    }

    @Override
    public Iterable<Key> keys() {
        Queue<Key> queue = new LinkedList<>();
        for (int i = 0; i < m; i++) {
            for (Key key : st[i].keys())
                queue.add(key);
        }
        return queue;
    }

    public static void main(String[] args) {
        SeparateChainingHashST<String, Integer> st = new SeparateChainingHashST<>();
        String test = "S E A R C H E X A M P L E";
        String[] keys = test.split(" ");
        for (int i = 0; i < keys.length; i++) {
            st.put(keys[i], i);
        }
        System.out.println("size         = " + st.size());
        System.out.println("isEmpty      = " + st.isEmpty());
        System.out.println("contains     = " + st.contains("S"));
        st.delete("S");
        System.out.println("contains     = " + st.contains("S"));
        for (String key : st.keys()) {
            System.out.println(key + " " + st.get(key));
        }
    }

}