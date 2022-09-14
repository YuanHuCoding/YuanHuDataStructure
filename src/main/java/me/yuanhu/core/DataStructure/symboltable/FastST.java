package me.yuanhu.core.DataStructure.symboltable;

import me.yuanhu.core.DataStructure.IST;
import me.yuanhu.core.DataStructure.java.jcf.LinkedList;
import me.yuanhu.core.DataStructure.java.jcf.Queue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

//仿照Netty的FastThreadLocal，此类实际没多大意义
public class FastST<Key extends FastST.FastSTKey, Value> implements IST<Key, Value> {

     static class FastSTKey<Key>{
        private final int slotIndex = nextVariableIndex();
        private final Key key;

        FastSTKey(Key key) {
            this.key = key;
        }

        public Key getKey() {
            return key;
        }

        public int getSlotIndex() {
            return slotIndex;
        }

    }


    public static final Object UNSET = null;

    static final AtomicInteger nextIndex = new AtomicInteger();


    public FastSTKey key(Object key) {
       return new FastSTKey(key);
    }

    //获取唯一索引
    public static int nextVariableIndex() {
        int index = nextIndex.getAndIncrement();
        if (index < 0) {
            nextIndex.decrementAndGet();
            throw new IllegalStateException("too many indexed variables");
        }
        return index;
    }

    public static int lastVariableIndex() {
        return nextIndex.get() - 1;
    }

    private Set<FastSTKey> keys = new HashSet<>();
    /**
     * 其index就是唯一标记index，
     * value是相对应存储的值
     */
    Value[] vals=(Value[]) new Object[0];//用来存储value的，使用下标的方式直接访问


    @Override
    public int size() {
        return keys.size();
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
    public void put(Key key, Value val) {
        keys.add(key);
        setIndexedVariable(key.getSlotIndex(),val);
    }

    @Override
    public Value get(Key key) {
        return indexedVariable(key.getSlotIndex());
    }

    @Override
    public void delete(Key key) {
        keys.remove(key);
        removeIndexedVariable(key.getSlotIndex());
    }

    @Override
    public Iterable<Key> keys() {
        Queue<Key> queue = new LinkedList<>();
        keys.forEach(item->queue.add((Key) item));
        return queue;
    }


    //获取指定位置的元素
    public Value indexedVariable(int index) {
        Value[] lookup = vals;
        return index < lookup.length? lookup[index] : (Value)UNSET;
    }

    /**
     * @return {@code true} if and only if a new thread-local variable has been created
     */
    //setIndexedVariables方法仅仅当是新增的时候返回true，假设是修改的话，oldValue就不等于UNSET了，则返回false。
    public boolean setIndexedVariable(int index, Value value) {
        Value[] lookup = vals;
        if (index < lookup.length) {
            Value oldValue = lookup[index];
            lookup[index] = value;
            return oldValue == UNSET;
        } else {//如果数组长度不够则扩充数组长度，然后保存。
            expandIndexedVariableTableAndSet(index, value);
            return true;
        }
    }


    private void expandIndexedVariableTableAndSet(int index, Value value) {
        Value[] oldArray = vals;
        final int oldCapacity = oldArray.length;

        /**
         * 计算新数组容量：获取>index的最小的2的n次方的数，例如：1->2 2->4 3->4 4->8 5->8 6->8 7->8 8->16
         * Returns a power of two size for the given target capacity.
         * <pre>
         *
         * {@link java.util.HashMap#tableSizeFor(int)}
         * static final int tableSizeFor(int cap) {
         *   int n = cap - 1;
         *   n |= n >>> 1;
         *   n |= n >>> 2;
         *   n |= n >>> 4;
         *   n |= n >>> 8;
         *   n |= n >>> 16;
         *   return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
         * }
         * </pre>
         */

        int newCapacity = index;
        newCapacity |= newCapacity >>>  1;
        newCapacity |= newCapacity >>>  2;
        newCapacity |= newCapacity >>>  4;
        newCapacity |= newCapacity >>>  8;
        newCapacity |= newCapacity >>> 16;
        newCapacity ++;

        /**
         * 创建新数组并拷贝旧数组的元素到新数组
         */
        Value[] newArray = Arrays.copyOf(oldArray, newCapacity);
        /**
         * 初始化扩容出来的部分的元素
         */
        Arrays.fill(newArray, oldCapacity, newArray.length, UNSET);
        /**
         * 设置变量
         */
        newArray[index] = value;
        /**
         * 将新数组设置给成员变量
         */
        vals = newArray;
    }

    /**
     * 删除指定位置的对象
     */
    public Object removeIndexedVariable(int index) {
        Value[] lookup = vals;
        if (index < lookup.length) {
            // 1、获取旧值
            Object v = lookup[index];
            // 2、设置为UNSET
            lookup[index] = (Value) UNSET;
            // 3、返回旧值
            return v;
        } else {
            return UNSET;
        }
    }

    public boolean isIndexedVariableSet(int index) {
        Value[] lookup = vals;
        return index < lookup.length && lookup[index] != UNSET;
    }



    public static void main(String[] args) {
        FastST<FastSTKey<String>, Integer> st = new FastST<>();

        //除非你的key也是事先定义好，不再变动（类似于ThreadLocal这种场景），否则此类没多大意义。
        FastSTKey  key_A = st.key("A");
        FastSTKey  key_B = st.key("B");
        FastSTKey  key_C = st.key("C");
        st.put(key_A, 1);
        st.put(key_B, 2);
        st.put(key_C, 3);

        System.out.println("size         = " + st.size());
        System.out.println("isEmpty      = " + st.isEmpty());
        System.out.println("contains     = " + st.contains(key_B));
        st.delete(key_B);
        System.out.println("contains     = " + st.contains(key_B));
        for (FastSTKey key : st.keys()) {
            System.out.println(key.getKey() + " " + st.get(key));
        }
    }
}
