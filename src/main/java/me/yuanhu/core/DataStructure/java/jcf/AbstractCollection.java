package me.yuanhu.core.DataStructure.java.jcf;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/*
    AbstractCollection 是 Java 集合框架中 Collection 接口 的一个直接实现类，
    Collection 下的大多数子类都继承 AbstractCollection ，比如 List 的实现类, Set的实现类。
* */
public abstract class AbstractCollection<E> implements Collection<E> {

    protected AbstractCollection() {
    }

    // Query Operations

    public abstract Iterator<E> iterator();

    public abstract int size();

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(Object o) {
        Iterator<E> it = iterator();
        if (o==null) {
            while (it.hasNext())
                if (it.next()==null)
                    return true;
        } else {
            while (it.hasNext())
                if (o.equals(it.next()))
                    return true;
        }
        return false;
    }

    /**
     * 功能：将集合元素转换为数组
     * 实现：
     * （1）创建一个数组，大小为集合中元素的数量
     * （2）通过迭代器遍历集合，将当前集合中的元素复制到数组中（复制引用）
     * （3）如果集合中元素比预期的少，则调用Arrays.copyOf()方法将数组的元素复制到新数组中，并返回新数组.
     * （4）如果集合中元素比预期的多，则调用finishToArray方法生成新数组，并返回新数组，否则返回（1）中创建的数组
     */
    public Object[] toArray() {
        // Estimate size of array; be prepared to see more or fewer elements
        Object[] r = new Object[size()];
        Iterator<E> it = iterator();
        for (int i = 0; i < r.length; i++) {
            if (!it.hasNext()) // fewer elements than expected
                return Arrays.copyOf(r, i);
            r[i] = it.next();
        }
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    /**
     * 功能：通过泛型约束返回指定类型的数组
     * 实现：
     * （1）如果传入数组的长度大于等于集合的长度，则将当前集合的元素复制到传入的数组中
     * （2）如果传入数组的长度小于集合的大小，则将创建一个新的数组来进行集合元素的存储
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        // Estimate size of array; be prepared to see more or fewer elements
        int size = size();
        T[] r = a.length >= size ? a :
                (T[])java.lang.reflect.Array
                        .newInstance(a.getClass().getComponentType(), size);
        Iterator<E> it = iterator();

        for (int i = 0; i < r.length; i++) {
            //集合元素大小小于数组的长度
            if (!it.hasNext()) { // fewer elements than expected
                if (a == r) {//如果数组是参数中的数组，则将剩余部分的值都设置为null
                    r[i] = null; // null-terminate
                } else if (a.length < i) {//如果传入的数组长度小于集合长度，则通过Arrays.copyOf将之前数组中的元素复制到新数组中
                    return Arrays.copyOf(r, i);
                } else {//如果传入数组的长度比集合大，则将多的元素设置为空
                    System.arraycopy(r, 0, a, 0, i);
                    if (a.length > i) {
                        a[i] = null;
                    }
                }
                return a;
            }
            r[i] = (T)it.next();
        }
        // more elements than expected
        //如果集合元素大小 居然大于 数组的长度
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     *
     * max size
     * 2^31 = 2,147,483,648
     * as the Array it self needs 8 bytes to stores the size  2,147,483,648
     * so
     * 2^31 -8 (for storing size ),
     * so maximum array size is defined as Integer.MAX_VALUE - 8
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Reallocates the array being used within toArray when the iterator
     * returned more elements than expected, and finishes filling it from
     * the iterator.
     *
     * @param r the array, replete with previously stored elements
     * @param it the in-progress iterator over this collection
     * @return array containing the elements in the given array, plus any
     *         further elements returned by the iterator, trimmed to size
     */
    /**
     *  功能：数组扩容
     *  （1）当数组索引指向最后一个元素+1时，对数组进行扩容：即创建一个更长的数组，然后将原数组的内容复制到新数组中
     *  （2）扩容大小：cap + cap/2 +1
     *  （3）扩容前需要先判断数组长度是否溢出
     *  注意：这里的迭代器是从上层的方法（toArray）传过来的，并且这个迭代器已执行了一部分，而不是从头开始迭代的
     */
    @SuppressWarnings("unchecked")
    private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
        int i = r.length;
        while (it.hasNext()) {
            int cap = r.length;
            if (i == cap) {
                int newCap = cap + (cap >> 1) + 1;
                // overflow-conscious code
                if (newCap - MAX_ARRAY_SIZE > 0)
                    newCap = hugeCapacity(cap + 1);
                r = Arrays.copyOf(r, newCap);
            }
            r[i++] = (T)it.next();
        }
        // trim if overallocated
        return (i == r.length) ? r : Arrays.copyOf(r, i);
    }

    /**
     * 判断数组容量是否溢出，最大为整型数据的最大值
     */
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError
                    ("Required array size too large");
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    // Modification Operations

    /**
     未实现
     如果你想修改一个不可变的集合时，抛出 UnsupportedOperationException 是标准的行为，比如 当你用 Collections.unmodifiableXXX() 方法
     对某个集合进行处理后，再调用这个集合的 修改方法（add,remove,set…），都会报这个错；
     因此 AbstractCollection.add(E) 抛出这个错误是遵从标准；
     */
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
        Iterator<E> it = iterator();
        if (o==null) {
            while (it.hasNext()) {
                if (it.next()==null) {
                    it.remove();
                    return true;
                }
            }
        } else {
            while (it.hasNext()) {
                if (o.equals(it.next())) {
                    it.remove();
                    return true;
                }
            }
        }
        return false;
    }


    // Bulk Operations

    /**
     * 遍历参数集合，依次判断参数集合中的元素是否在当前集合中，
     * 只要有一个不存在，则返回false
     * 如果参数集合中所有的元素都在当前集合中，则返回true
     */
    public boolean containsAll(Collection<?> c) {
        for (Object e : c)
            if (!contains(e))
                return false;

        return true;
    }

    /**
     * 遍历参数集合，依次将参数集合中的元素添加当前集合中
     */
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c)
            if (add(e))
                modified = true;

        return modified;
    }

    /**
     * 功能：移除参数集合的元素
     * （1）获取当前集合的迭代器进行遍历
     * （2）如果当前集合中的元素包含在参数集合中，则删除当前集合中的元素
     *  注：只要参数集合中有任何一个元素在当前元素中，则返回true，表示当前集合有发生变化，否则返回false。
     */
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<?> it = iterator();
        while (it.hasNext()) {
            //双重循环
            if (c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    /***
     * 功能：求参数集合与当前集合的交集
     * （1）获取当前集合的迭代器进行遍历
     * （2）如果当前集合中的元素不在参数集合中，则将其移除。
     *  注意：如果当前集合是参数集合中的子集，则返回false，表示当前集合未发生变化，否则返回true。
     */
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            //排除异己，不在我集合中的统统 886
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    //删除所有元素
    public void clear() {
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }


    //  String conversion

    public String toString() {
        Iterator<E> it = iterator();
        if (! it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            E e = it.next();
            sb.append(e == this ? "(this Collection)" : e);
            if (! it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }

}
