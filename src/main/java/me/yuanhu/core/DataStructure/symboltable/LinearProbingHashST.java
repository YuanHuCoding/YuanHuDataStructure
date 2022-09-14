package me.yuanhu.core.DataStructure.symboltable;

import me.yuanhu.core.DataStructure.IST;
import me.yuanhu.core.DataStructure.java.jcf.LinkedList;
import me.yuanhu.core.DataStructure.java.jcf.Queue;

//基于 散列表(线性探测法) 的符号表
public class LinearProbingHashST<Key, Value> implements IST<Key, Value> {
    private static final int INIT_CAPACITY = 4;

    private int n;           // number of key-value pairs in the symbol table // 符号表中键值对的总数
    private int m;           // size of linear probing table  // 线性探测表的大小
    private Key[] keys;      // the keys  // 键
    private Value[] vals;    // the values  // 值

    public LinearProbingHashST() {
        this(INIT_CAPACITY);
    }

    public LinearProbingHashST(int capacity) {
        m = capacity;
        n = 0;
        keys = (Key[])   new Object[m];
        vals = (Value[]) new Object[m];
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

    // Java 中的 hashCode() 实现了哈希函数，默认使用对象的内存地址值。
    // 在使用 hashCode() 时，应当结合除留余数法来使用。
    // 因为内存地址是 32 位整数，我们只需要 31 位的非负整数，因此应当屏蔽符号位之后再使用除留余数法。
    // 0x7FFFFFFF 是32位最大带符号整数 System.out.println(0x7fffffff);打印出来的是2147483647
    // hash function for keys - returns value between 0 and M-1
    private int hash(Key key) {
        return (key.hashCode() & 0x7fffffff) % m;
    }

    // resizes the hash table to the given capacity by re-hashing all of the keys
    private void resize(int capacity) {
        LinearProbingHashST<Key, Value> temp = new LinearProbingHashST<Key, Value>(capacity);
        for (int i = 0; i < m; i++) {
            if (keys[i] != null) {
                temp.put(keys[i], vals[i]);
            }
        }
        keys = temp.keys;
        vals = temp.vals;
        m    = temp.m;
    }

    @Override
    public void put(Key key, Value val) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");

        if (val == null) {
            delete(key);
            return;
        }

        // double table size if 50% full  // 将m加倍
        if (n >= m/2) resize(2*m);

        int i;
        for (i = hash(key); keys[i] != null; i = (i + 1) % m) {
            if (keys[i].equals(key)) {
                vals[i] = val;
                return;
            }
        }
        keys[i] = key;
        vals[i] = val;
        n++;
    }

    @Override
    public Value get(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        for (int i = hash(key); keys[i] != null; i = (i + 1) % m)
            if (keys[i].equals(key))
                return vals[i];
        return null;
    }

    //删除操作应当将右侧所有相邻的键值对重新插入散列表中。
    @Override
    public void delete(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");
        if (!contains(key)) return;// 不存在，直接返回

        // find position i of key
        int i = hash(key);
        while (!key.equals(keys[i])) {
            i = (i + 1) % m;
        }

        // delete key and associated value
        keys[i] = null;
        vals[i] = null;

        // rehash all keys in same cluster // 重新散列所有键,将之后相连的键值对重新插入
        i = (i + 1) % m;
        while (keys[i] != null) {
            // delete keys[i] an vals[i] and reinsert
            Key   keyToRehash = keys[i];
            Value valToRehash = vals[i];
            keys[i] = null;
            vals[i] = null;
            n--;
            put(keyToRehash, valToRehash);
            i = (i + 1) % m;
        }

        n--;

        // halves size of array if it's 12.5% full or less   // 减小数组的大小
        if (n > 0 && n <= m/8) resize(m/2);

        assert check();
    }

    @Override
    public Iterable<Key> keys() {
        Queue<Key> queue = new LinkedList<>();
        for (int i = 0; i < m; i++)
            if (keys[i] != null) queue.add(keys[i]);
        return queue;
    }

    // integrity check - don't check after each put() because
    // integrity not maintained during a delete()
    private boolean check() {

        // check that hash table is at most 50% full
        if (m < 2*n) {
            System.err.println("Hash table size m = " + m + "; array size n = " + n);
            return false;
        }

        // check that each key in table can be found by get()
        for (int i = 0; i < m; i++) {
            if (keys[i] == null) continue;
            else if (get(keys[i]) != vals[i]) {
                System.err.println("get[" + keys[i] + "] = " + get(keys[i]) + "; vals[i] = " + vals[i]);
                return false;
            }
        }
        return true;
    }


    public static void main(String[] args) {
        IST<String, Integer> st = new LinearProbingHashST<>();
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
