package me.yuanhu.core.DataStructure.symboltable;

import me.yuanhu.core.DataStructure.IST;
import me.yuanhu.core.DataStructure.java.jcf.LinkedList;
import me.yuanhu.core.DataStructure.java.jcf.Queue;

//基于 无序链表（顺序查找）的符号表
public class SequentialSearchST<Key, Value> implements IST<Key, Value> {
    private int n;           // number of key-value pairs
    private Node first;      //链表首节点

    // a helper linked list data type
    private class Node {
        private Key key;
        private Value val;
        private Node next;

        public Node(Key key, Value val, Node next)  {
            this.key  = key;
            this.val  = val;
            this.next = next;
        }
    }

    public SequentialSearchST() {
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
        for (Node x = first; x != null; x = x.next) {
            if (key.equals(x.key))
                return x.val;// 命中
        }
        return null;// 未命中
    }

    @Override
    public void put(Key key, Value val) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        if (val == null) {
            delete(key);
            return;
        }

        for (Node x = first; x != null; x = x.next) {
            if (key.equals(x.key)) {
                x.val = val;// 命中，替换
                return;
            }
        }
        first = new Node(key, val, first);// 未命中，新建节点
        n++;
    }

    @Override
    public void delete(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");
        first = delete(first, key);
    }

    // delete key in linked list beginning at Node x
    // warning: function call stack too large if table is large
    private Node delete(Node x, Key key) {
        if (x == null) return null;
        if (key.equals(x.key)) {
            n--;
            return x.next;
        }
        x.next = delete(x.next, key);
        return x;
    }

    @Override
    public Iterable<Key> keys()  {
        Queue<Key> queue = new LinkedList<>();
        for (Node x = first; x != null; x = x.next)
            queue.add(x.key);
        return queue;
    }

    public static void main(String[] args) {
        SequentialSearchST<String, Integer> st = new SequentialSearchST<>();
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