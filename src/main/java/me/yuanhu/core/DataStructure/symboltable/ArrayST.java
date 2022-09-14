package me.yuanhu.core.DataStructure.symboltable;

import me.yuanhu.core.DataStructure.IST;
import me.yuanhu.core.DataStructure.java.jcf.LinkedList;
import me.yuanhu.core.DataStructure.java.jcf.Queue;

//基于 无序数组 的符号表
public class ArrayST<Key, Value> implements IST<Key, Value>{
    private static final int INIT_SIZE = 8;

    private Value[] vals;   // symbol table values
    private Key[] keys;     // symbol table keys
    private int n = 0;      // number of elements in symbol table

    public ArrayST() {
        keys = (Key[])   new Object[INIT_SIZE];
        vals = (Value[]) new Object[INIT_SIZE];
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

    // 根据参数调整符号表的大小，先定义好新数组的长度，然后将原数组的值赋值给对应新数组的位置，最后将新数组的数组名赋值给原数组。
    private void resize(int capacity) {
        Key[]   tempk = (Key[])   new Object[capacity];
        Value[] tempv = (Value[]) new Object[capacity];
        for (int i = 0; i < n; i++)
            tempk[i] = keys[i];
        for (int i = 0; i < n; i++)
            tempv[i] = vals[i];
        keys = tempk;
        vals = tempv;
    }

    @Override
    public void put(Key key, Value val) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");

        if (val == null) {
            delete(key);
            return;
        }

        // double size of arrays if necessary
        if (n >= vals.length) resize(2*n);

        // add new key and value at the end of array
        vals[n] = val;
        keys[n] = key;
        n++;
    }

    @Override
    public Value get(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        for (int i = 0; i < n; i++)
            if (key.equals(keys[i])) return vals[i];
        return null;
    }

    @Override
    public void delete(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");

        for (int i = 0; i < n; i++) {
            if (key.equals(keys[i])) {
                keys[i] = keys[n-1];//把最后的元素填补过来
                vals[i] = vals[n-1];
                keys[n-1] = null;
                vals[n-1] = null;
                n--;
                if (n > 0 && n == keys.length/4) resize(keys.length/2);
                return;
            }
        }
    }

    @Override
    public Iterable<Key> keys() {
        Queue<Key> queue = new LinkedList<>();
        for (int i = 0; i < n; i++)
            queue.add(keys[i]);
        return queue;
    }


    public static void main(String[] args) {
        IST<String, Integer> st = new ArrayST<>();
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
