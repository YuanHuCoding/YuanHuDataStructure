package me.yuanhu.core.DataStructure.collection;


import me.yuanhu.core.DataStructure.IList;
import me.yuanhu.core.DataStructure.ILinkedList;

import java.io.Serializable;
import java.util.*;

/**
 * 改良的双链表(带头结点和尾结点)类似java集合类LinkedList
 */
public class LinkeList<E> implements Serializable, ILinkedList<E>, IList<E>, Iterable<E>{

    private static final long serialVersionUID = 8683452581122892300L;

    //===========数据结构========================================

    //链表size,优化计算过程,无需遍历链表
    protected int size = 0;

    //记录修改次数,适用于快速失败机制
    protected int modCount=0;

    /**
     * 头部指向结点,不带数据,排除特殊情况,优化代码量
     */
    protected Node<E> first;

    /**
     * 尾部指向结点,不带数据,排除特殊情况,优化代码量
     */
    protected Node<E> last;


    //===========构造函数========================================

    public LinkeList() {
        first=new Node<>(null,null,null);
        last=new Node<>(first,null,null);
        first.next=last;
        size=0;
        modCount++;
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
        for (Node<E> x = first.next; x != null; ) {
            Node<E> next = x.next;
            // clear to let GC do its work
            x.data = null;
            x.next = null;
            x.prev = null;
            x = next;
        }
        //初始化链表
        first=new Node<>(null,null,null);
        last=new Node<>(first,null,null);
        first.next=last;
        size = 0;
        modCount++;
    }

    @Override
    public E get(int index) {
        rangeCheck(index);
        return getNode(index).data;
    }

    @Override
    public E set(int index, E data) {
        rangeCheck(index);
        Node<E> x = getNode(index);
        E oldVal = x.data;
        x.data = data;
        return oldVal;
    }

    @Override
    public boolean add(E data) {
        linkLast(data);
        return true;
    }

    @Override
    public boolean add(int index, E data) {
        rangeCheck(index);

        if (index == size)//直接尾部添加
            linkLast(data);
        else
            linkBefore(data, getNode(index));//查找到插入结点并在其前插入
        return true;
    }

    @Override
    public int indexOf(E data) {
        int index = 0;
        if (data == null) {
            //注意起始结点
            for (Node<E> x = first.next; x != null; x = x.next) {
                if (x.data == null)
                    return index;
                index++;
            }
        } else {
            for (Node<E> x = first.next; x != null; x = x.next) {
                if (data.equals(x.data))
                    return index;
                index++;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(E data) {
        int index = size;
        if (data == null) {
            for (Node<E> x = last.prev; x != null; x = x.prev) {
                index--;
                if (x.data == null)
                    return index;
            }
        } else {
            for (Node<E> x = last.prev; x != null; x = x.prev) {
                index--;
                if (data.equals(x.data))
                    return index;
            }
        }
        return -1;
    }

    @Override
    public boolean contains(E data) {
        return indexOf(data)!=-1;
//        return indexOf(data)>=0;
    }

    @Override
    public boolean remove(E data) {
        if (data == null) {
            for (Node<E> x = first.next; x != null; x = x.next) {
                if (x.data == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (data.equals(x.data)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public E remove(int index) {
        rangeCheck(index);
        //移除
        return  unlink(getNode(index));
    }


    //===========辅助方法========================================

    //删除x结点
    E unlink(Node<E> x) {
        // assert x != null;
        x.next.prev=x.prev;
        x.prev.next=x.next;
        size--;
        modCount++;
        return  x.data;
    }


    //在succ结点前插入
    void linkBefore(E E, Node<E> succ) {
        // assert succ != null;
        final Node<E> newNode = new Node<>(succ.prev, E, succ);
        succ.prev.next=newNode;
        succ.prev = newNode;
        size++;
        modCount++;
    }

    //链表头部添加,由于拥有头结点和尾结点,无需判断插入情况
    private void linkFirst(E data) {
        //头结点的下一个结点
        final Node<E> f = first.next;
        final Node<E> newNode = new Node<>(first, data, f);
        f.prev=newNode;
        first.next = newNode;
        size++;
        modCount++;
    }


    //链表尾部添加,由于拥有头结点和尾结点,无需判断插入情况
    void linkLast(E data) {
        //尾部结点的前一个结点
        final Node<E> l = last.prev;
        final Node<E> newNode = new Node<>(l, data, last);
        l.next = newNode;
        last.prev=newNode;
        size++;
        //记录修改
        modCount++;
    }


    //优化结点查询,根据情况而定查询起点
    Node<E> getNode(int index) {
        //如果index小于size的一半,则从头结点开始查找,否则从尾部开始查找(右移2位相当除以2)
        if (index < (size >> 1)) {
            Node<E> x = first.next;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last.prev;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

    private void rangeCheck(int index) {
        if (index >= size|| index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    //===========迭代器方法========================================


    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }


    /**
     * 迭代器,支持遍历过程中删除结点
     */
    private class Itr implements Iterator<E> {

        //指向下一个结点的下标
        int cursor = 0;

        //当前需要返回结点的下标
        int lastRet = -1;

        //用于判断是否集合被修改
        int expectedModCount = modCount;

        //是否还有下一个结点
        public boolean hasNext() {
            return cursor != size();
        }

        //获取当前结点的值
        public E next() {
            checkForComodification();
            try {
                int i = cursor;
                E next = get(i);
                lastRet = i;//指向当前结点
                cursor = i + 1;//更新,指向下一个还未访问的结点
                return next;
            } catch (IndexOutOfBoundsException T) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                LinkeList.this.remove(lastRet);
                if (lastRet < cursor)
                    cursor--;//回撤一位
                lastRet = -1;//复原
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException T) {
                throw new ConcurrentModificationException();
            }
        }

        //快速失败机制,检测是否集合已变更
        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }


    public ListIterator<E> listIterator(int index) {
        rangeCheck(index);
        return new ListItr(index);
    }

    //含前后指向的迭代器,支持变量过程添加元素,删除元素
    private class ListItr implements ListIterator<E> {
        private Node<E> lastReturned;//指向当前正在被访问的结点
        private Node<E> next;//还未被访问的结点
        private int nextIndex;//还未被访问的结点下标
        private int expectedModCount = modCount;//用于判断集合是否被修改

        //结点指向传入值index的结点
        ListItr(int index) {
            // assert isPositionIndex(index);
            next = (index == size) ? null : getNode(index);
            nextIndex = index;
        }

        public boolean hasNext() {
            return nextIndex < size;
        }

        //获取结点数据
        public E next() {
            checkForComodification();
            if (!hasNext())
                throw new NoSuchElementException();

            lastReturned = next;//当前正在被访问的结点
            next = next.next;//更新至还未被访问的结点
            nextIndex++;//更新至还未被访问结点的下标
            return lastReturned.data;
        }

        //是否有前驱结点
        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        //功能与next()一样,但previous()是往前遍历
        public E previous() {
            checkForComodification();
            if (!hasPrevious())
                throw new NoSuchElementException();

            lastReturned = next = (next == null) ? last.prev : next.prev;
            nextIndex--;
            return lastReturned.data;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        //移除操作
        public void remove() {
            checkForComodification();
            if (lastReturned == null)
                throw new IllegalStateException();

            Node<E> lastNext = lastReturned.next;
            unlink(lastReturned);
            //如果next还未更新,则直接执行lastNext
            if (next == lastReturned)
                next = lastNext;
            else
                //如果next已更新,那么nextIndex必定已执行了nextIndex++操作,此时由于删除结点
                //所以必须执行nextIndex--,才能使nextIndex与next相对应
                nextIndex--;

            //复原
            lastReturned = null;
            expectedModCount++;
        }

        public void set(E E) {
            if (lastReturned == null)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.data = E;
        }

        public void add(E E) {
            checkForComodification();
            lastReturned = null;
            if (next == null)
                linkLast(E);
            else
                linkBefore(E, next);
            nextIndex++;
            expectedModCount++;
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }


    /**
     * 双向结点类
     * @param <T>
     */
    protected static class Node<T> {
        T data;
        Node<T> next;
        Node<T> prev;

        Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    //测试
    public static void main(String[] args){
        System.out.println("------init-------");
        LinkeList<Integer> mylinkeList=new LinkeList<>();
        mylinkeList.add(2);
        mylinkeList.add(10);
        mylinkeList.add(1);
        mylinkeList.add(9);
        mylinkeList.add(20);
        mylinkeList.add(555);

        print(mylinkeList);
        System.out.println("------remove(2)-------");
        mylinkeList.remove(2);
        print(mylinkeList);
        System.out.println("------indexOf(10)&set(0,0)-------");
        System.out.println("index-->"+mylinkeList.indexOf(10));
        mylinkeList.set(0,0);
        print(mylinkeList);

        System.out.println("-------------iterator--------------");
        Iterator<Integer> iterator=mylinkeList.iterator();
        while (iterator.hasNext()){
            System.out.println("iterator.next-->"+iterator.next());
        }

        System.out.println("-------------iteratorList--------------");
        ListIterator<Integer> iteratorList=mylinkeList.listIterator(0);
        iteratorList.add(88);
        while (iteratorList.hasNext()){
            System.out.println("iteratorList.next-->"+iteratorList.next());
        }
        iteratorList.add(100);
        System.out.println("-------------iteratorList1.add--------------");
        //使用完后必须重新new
        ListIterator<Integer> iteratorList1=mylinkeList.listIterator(0);
        while (iteratorList1.hasNext()){
            int i=iteratorList1.next();
            if(i==555){
                System.out.println("i==555");
                iteratorList1.remove();
            }else {
                System.out.println("iteratorList.next-->" +i);
            }
        }


        System.out.println("-------------foreach--------------");
        for(Integer data : mylinkeList){
            System.out.println("data-->"+data);
        }

        System.out.println("-------------iterator--------------");
        //抛异常:java.util.ConcurrentModificationException
        //在迭代时删除元素必须使用iterator自身的删除方法,使用mylinkeList的
        //删除方法将会触发快速失败机制
        Iterator<Integer> it = mylinkeList.iterator();
        while (it.hasNext()) {
            mylinkeList.remove(new Integer(100));
            Integer value = it.next();
            if (value==100) {
                System.out.println("该集合含100!");
            }else {
                System.out.println("该集合不含100!");
            }
        }
    }

    public static void print(LinkeList mylinkeList){
        for (int i=0;i<mylinkeList.size();i++) {
            System.out.println("i->"+mylinkeList.get(i));
        }
    }


}