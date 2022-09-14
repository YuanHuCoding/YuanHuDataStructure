package me.yuanhu.core.DataStructure.list.seq_list;

import me.yuanhu.core.DataStructure.IList;
import me.yuanhu.core.DataStructure.ISeqList;

import java.util.Arrays;

/**
 * 顺序表
 */
public class SeqList<E> implements ISeqList<E>,IList<E>,Cloneable {

    //===========数据结构========================================

    private static final int DEFAULT_CAPACITY = 16;     //默认的初始化数组大小
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private E[] elementData;                      //数组声明,用于存储元素
    private int size;                            //顺序表的大小


    //===========构造函数========================================

    public SeqList() {
        this(DEFAULT_CAPACITY);
    }

    public SeqList(int initialCapacity) {
        //申请数组存储空间,元素初始化为null
        if (initialCapacity > 0) {
            this.elementData = (E[])new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = (E[])new Object[0];;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
        }
    }

    /**
     * 传入一个数组初始化顺序表
     *
     * @param datas
     */
    public SeqList(E[] datas) {
        if (datas == null) {
            throw new NullPointerException("datas can't be null!");
        }
        //创建对应容量的数组
        this.elementData = (E[])new Object[datas.length];
        for (int i = 0; i < datas.length; i++) {
            this.elementData[i] = datas[i];
        }
        this.size = datas.length;
    }

    private void ensureCapacityInternal(int minCapacity) {
        // 如果最低要求的存储能力>ArrayList已有的存储能力，这就表示ArrayList的存储能力不足，因此需要调用 grow();方法进行扩容
        if (minCapacity - elementData.length > 0) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        //当ArrayList扩容的时候，首先会设置新的存储能力为原来的1.5倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0) //申请的容量minCapacity比准备扩容旧容量的1.5 倍还大，就使用申请的容量minCapacity
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)//新的容量居然超出了 MAX_ARRAY_SIZE
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is x win:
        // minCapacity 一般跟元素个数 size 很接近，所以新建的数组容量为 newCapacity 更宽松些
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }


    //===========接口方法========================================

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public void clear() {
        // clear to let GC do its work
        for (int i = 0; i < size; i++) {
            elementData[i] = null;
        }
        this.size = 0;
    }

    @Override
    public E get(int index) {
        rangeCheck(index);
        return elementData(index);
    }

    @Override
    public E set(int index, E element) {
        rangeCheck(index);
        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }

    @Override
    public boolean add(int index, E element) {
        rangeCheckForAdd(index);
        ensureCapacityInternal(this.size + 1);

        //插入新值
        this.elementData[index] = element;
        //长度加一
        this.size++;
        //插入成功
        return true;
    }

    @Override
    public boolean add(E element) {
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        elementData[size++] = element;//将要添加的元素放置到相应的数组中
        return true;
    }

    @Override
    public int indexOf(E o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(E o) {
        if (o == null) {
            for (int i = size-1; i >= 0; i--)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = size-1; i >= 0; i--)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }


    @Override
    public boolean contains(E data) {
        return this.indexOf(data) >= 0;
    }


    @Override
    public E remove(int index) {
        rangeCheck(index);

        E oldValue = elementData(index);

        //挨个往前移一位
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                    numMoved);
        //原数组中最后一个元素删掉
        elementData[--size] = null; // clear to let GC do its work

        return oldValue;
    }

    @Override
    public boolean remove(E o) {
        if (o == null) {
            //挨个遍历找到目标
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        //在Java的反射机制中，通过 数组的 class 对象的getComponentType()方法可以取得一个数组的Class对象
        T[] r = a.length >= size ? a : (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        System.arraycopy(this.elementData, 0, r, 0, this.size);
        return r;
    }

    //===========辅助方法========================================


    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) elementData[index];
    }

    private void fastRemove(int index) {
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                    numMoved);
        elementData[--size] = null; // clear to let GC do its work
    }

    /**
     * Checks if the given index is in range.  If not, throws an appropriate
     * runtime exception.  This method does *not* check if the index is
     * negative: It is always used immediately prior to an array access,
     * which throws an ArrayIndexOutOfBoundsException if index is negative.
     */
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * A version of rangeCheck used by add and addAll.
     */
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }


    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     */
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

}