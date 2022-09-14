package me.yuanhu.core.DataStructure.list.double_linked_list;

/**
 * 升序排序链表
 * https://blog.csdn.net/javazejian/article/details/53047590
 */
public class DoubleLinkedList_Head_NoTail_Cyclic_Sort<E extends Comparable<? extends E>> extends DoubleLinkedList_Head_NoTail_Cyclic<E> {

    /**
     * 顺序插入
     *
     * @param data
     * @return
     */
    @Override
    public boolean add(E data) {
        if (data == null || !(data instanceof Comparable))
            throw new NullPointerException("data can\'t be null or data instanceof Comparable must be true");

        Comparable cmp = data;//这里需要转一下类型,否则idea编辑器上检验不通过.

        //如果data值比最后一个结点大,那么直接调用父类方法,在尾部添加.
        if (this.isEmpty() || cmp.compareTo(this.head.prev.data) > 0) {
            return super.add(data);
        }

        DNode<E> cur = this.head.next;
        //查找插入点
        while (cur != head && cmp.compareTo(cur.data) > 0)
            cur = cur.next;

        DNode<E> q = new DNode<E>(data, cur.prev, cur);
        cur.prev.next = q;
        cur.prev = q;

        return true;
    }


    public static void main(String[] args) {
        DoubleLinkedList_Head_NoTail_Cyclic_Sort<Integer> list = new DoubleLinkedList_Head_NoTail_Cyclic_Sort<>();
        list.add(50);
        list.add(40);
        list.add(80);
        list.add(20);
        System.out.println("init list-->" + list.toString());
    }
}