package me.yuanhu.core.DataStructure.tree.heap;


import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.Stream;

//http://nphard.me/2016/07/14/heap/
public class Heap<E> {
    public static void main(String... args) throws Exception {
        Heap<Double> heap = new Heap<Double>(DOUBLE_LITTLE_ROOT);
        Random r = new Random();
        Stream.generate(() -> r.nextDouble()).limit(10).forEach(val -> heap.push(val));
        System.out.println(heap.size);
        while (!heap.isEmpty()) {
            System.out.println(heap.pop());
        }
    }
    public static final Comparator<Integer> INTEGER_LITTLE_ROOT = (x, y) -> ((Integer) x).compareTo((Integer) y);
    public static final Comparator<Integer> INTEGER_BIG_ROOT = (x, y) -> ((Integer) y).compareTo((Integer) x);
    public static final Comparator<Double> DOUBLE_LITTLE_ROOT = (x, y) -> ((Double) x).compareTo((Double) y);
    public static final Comparator<Double> DOUBLE_BIT_ROOT = (x, y) -> ((Double) y).compareTo((Double) x);
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
    private static final Object[] EMPTY_ELEMENTDATA = {};
    private static final int DEFAULT_CAPACITY = 10;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private Comparator<E> cmp;
    private Object[] elementData;
    private int size;
    public Heap(int initialCapacity, Comparator<E> cmp) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        this.cmp = cmp;
        size = 1;
    }
    public Heap(Comparator<E> cmp) {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
        this.cmp = cmp;
        size = 1;
    }
    private void ensureCapacityInternal(int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        ensureExplicitCapacity(minCapacity);
    }
    private void ensureExplicitCapacity(int minCapacity) {
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }
    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0)
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }
    @SuppressWarnings("unchecked")
    public void push(E val) {
        ensureCapacityInternal(size + 1);
        elementData[size] = val;
        int pos = size++;
        while ((pos >> 1) != 0) {
            if (cmp.compare((E) elementData[pos], (E) elementData[pos >> 1]) < 0) {
                swap(elementData, pos, pos >> 1);
                pos = pos >> 1;
            } else
                break;
        }
    }
    public boolean isEmpty() {
        return size == 1;
    }
    @SuppressWarnings("unchecked")
    public E pop() throws Exception {
        if (size == 1)
            throw new Exception("heap is empty");
        E top = (E) elementData[1];
        elementData[1] = elementData[--size];
        int pos = 1;
        while (pos < size) {
            int sonPos = pos << 1;
            if (sonPos >= size)
                break;
            if (cmp.compare((E) elementData[sonPos | 1], (E) elementData[sonPos]) < 0 && (sonPos | 1) < size) {
                sonPos = sonPos | 1;
            }
            if (cmp.compare((E) elementData[pos], (E) elementData[sonPos]) <= 0) {
                break;
            }
            swap(elementData, pos, sonPos);
            pos = sonPos;
        }
        return top;
    }
    private void swap(Object[] elementData, int a, int b) {
        Object tmp = elementData[a];
        elementData[a] = elementData[b];
        elementData[b] = tmp;
    }
    public int size() {
        return size-1;
    }
}