package me.yuanhu.core.DataStructure.collection;

import me.yuanhu.core.DataStructure.IList;
import me.yuanhu.core.DataStructure.ISeqList;

import java.io.Serializable;
import java.util.*;

/**
 * 改良的顺序表类似java集合类ArrayList
 */
public class ArrayList<E>  implements Serializable, ISeqList<E>, IList<E>,Iterable<E>{

    private static final long serialVersionUID = 8683452581122892389L;

    //===========数据结构========================================

    //默认容量
    private static final int DEFAULT_CAPACITY = 10;

    //空值数组
    private static final Object[] EMPTY_ELEMENTDATA = {};

    //实际元素数量
    private int size;

    /**
     * 记录修改次数,适用于快速失败机制
     */
    private int modCount;

    /**
     * 存储数据的数组
     */
    private E[] elementData;


    //===========构造函数========================================

    public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = (E[]) new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = (E[]) EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
        }
    }

    public ArrayList() {
        this.elementData = (E[]) new Object[DEFAULT_CAPACITY];
    }

    /**
     * 扩容的方法
     * @param capacity
     */
    public void ensureCapacity(int capacity) {
        //如果需要拓展的容量比现在数组的容量还小,则无需扩容
        if (capacity<size)
            return;

        modCount++;//记录元素变化

        E[] old = elementData;
        elementData = (E[]) new Object[capacity];
        //复制元素
        for (int i=0; i<size() ; i++) {
            elementData[i] = old[i];
        }
    }


    /**
     * 检测下标
     * @param index
     */
    private void rangeCheck(int index) {
        if (index >= size|| index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }


    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    //===========接口方法========================================

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public void clear() {
        modCount++;
        // clear to let GC do its work
        for (int i = 0; i < size; i++)
            elementData[i] = null;

        size = 0;
    }

    @Override
    public E get(int index) {
        rangeCheck(index);
        return elementData[index];
    }

    @Override
    public E set(int index, E data) {
        rangeCheck(index);
        E old=elementData[index];
        elementData[index]=data;
        return old;
    }

    @Override
    public boolean add(E data) {
        add(size(),data);
        return true;
    }

    @Override
    public boolean add(int index, E data) {
        //判断容量是否充足
        if(elementData.length==size())
            ensureCapacity(size()*2+1);//扩容
        //根据index找到需要插入的位置
        for (int i=size; i>index; i--)
            elementData[i]=elementData[i-1];

        //赋值
        elementData[index]=data;
        size++;
        //记录变化
        modCount++;
        return true;
    }

    @Override
    public int indexOf(E data) {
        if (data == null) {
            //查找null的下标
            for (int i = 0; i < size; i++)
                if (elementData[i]==null)
                    return i;
        } else {
            //查找有数据的下标
            for (int i = 0; i < size; i++)
                if (data.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(E data) {
        //倒序查找即可
        if (data == null) {
            for (int i = size-1; i >= 0; i--)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = size-1; i >= 0; i--)
                if (data.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    @Override
    public boolean contains(E data) {
        return indexOf(data) >= 0;
    }

    @Override
    public boolean remove(E data) {
        for (int index = 0; index < size; index++) {
            if ((data!=null&&data.equals(elementData[index])||(data==null&&elementData[index]==null))) {
                this.remove(indexOf(data));
                return true;
            }
        }
        return false;
    }

    @Override
    public E remove(int index) {
        rangeCheck(index);
        modCount++;

        E oldValue = elementData[index];

        for (int i=index;i<size()-1;i++){
            elementData[i]=elementData[i+1];
        }

        elementData[--size] = null; // clear to let GC do its work

        return oldValue;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        T[] r = a.length >= size ? a : (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        System.arraycopy(this.elementData, 0, r, 0, this.size);
        return r;
    }

    @Override
    public Object clone() {
        try {
            ArrayList<E> v = (ArrayList<E>) super.clone();
            v.elementData = Arrays.copyOf(elementData, size);
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }

    //===========迭代器方法========================================

    /**
     * 返回迭代器
     * @return
     */
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * 提供从0开始遍历的迭代器
     * @return
     */
    public ListIterator<E> listIterator() {
        return new ListItr(0);
    }


    /**
     * 提供从指定index开始遍历的迭代器
     * @param index
     * @return
     */
    public ListIterator<E> listIterator(int index) {
        if (index < 0 || index > size)
            new IndexOutOfBoundsException(outOfBoundsMsg(index));
        return new ListItr(index);
    }



    /**
     * 迭代器-Itr
     */
    private class Itr implements Iterator<E> {
        //表示将要访问的下一个元素的下标
        int cursor;
        //当前正在访问的元素下标,如果没有则返回-1
        int lastRet = -1;
        //修改标识符,用于判断集合是否被修改
        int expectedModCount = modCount;

        Itr() {}

        public boolean hasNext() {
            return cursor != size;
        }

        @SuppressWarnings("unchecked")
        public E next() {
            checkForComodification();
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            //获取当前集合
            Object[] elementData = ArrayList.this.elementData;

            if (i >= elementData.length)
                throw new ConcurrentModificationException();

            cursor = i + 1;//加一,移动到下一个要访问的下标

            return (E) elementData[lastRet = i];
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();

            checkForComodification();

            try {
                //移除当前操作的元素
                ArrayList.this.remove(lastRet);

                //修改当前下标指向
                cursor = lastRet;
                //复原
                lastRet = -1;
                //更新标识符,防止抛出异常
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }


        //检测modCount标识符
        final void checkForComodification() {//方法前加final的作用：主要是当前类被子类继承后，不允许子类对该final方法重写。
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }


    /**
     * 可以前移指向的迭代器-ListItr
     */
    private class ListItr extends Itr implements ListIterator<E> {
        ListItr(int index) {
            super();
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        @SuppressWarnings("unchecked")
        public E previous() {
            checkForComodification();
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();
            Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i;
            return (E) elementData[lastRet = i];
        }

        public void set(E e) {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                ArrayList.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(E data) {
            checkForComodification();

            try {
                int i = cursor;
                ArrayList.this.add(i, data);
                cursor = i + 1;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }



    public static void main(String[] args){
        ArrayList<Integer> myArrayList=new ArrayList<>();
        myArrayList.add(2);
        myArrayList.add(10);
        myArrayList.add(1);
        myArrayList.add(9);

        print(myArrayList);
        System.out.println("-------------");
        myArrayList.remove(2);
        print(myArrayList);
        System.out.println("-------------");
        System.out.println("index-->"+myArrayList.indexOf(10));
        myArrayList.set(0,0);
        print(myArrayList);

        System.out.println("-------------iterator--------------");
        Iterator iterator=myArrayList.iterator();
        while (iterator.hasNext()){
            System.out.println("iterator.next-->"+iterator.next());
        }

        System.out.println("-------------foreach--------------");
        for(Integer data : myArrayList){
            System.out.println("data-->"+data);
        }

    }

    public static void print(ArrayList myArrayList){
        for (int i=0;i<myArrayList.size();i++) {
            System.out.println("i->"+myArrayList.get(i));
        }
    }

}
