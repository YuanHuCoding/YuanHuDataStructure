package me.yuanhu.core.DataStructure;

/**
 * 链表顶级接口
 */
public interface ILinkedList<E> extends IList<E> {

    /**
     * list大小
     * @return
     */
    int size();

    /**
     * 是否为空
     * @return
     */
    boolean isEmpty();

    /**
     * 清空链表
     */
    void clear();

    /**
     * 根据index获取节点数据
     * @param index
     * @return
     */
    E get(int index);

    /**
     * 设置某个结点的的值
     * @param index
     * @param data
     * @return
     */
    E set(int index, E data);

    /**
     * 根据index添加结点
     * @param index
     * @param data
     * @return
     */
    boolean add(int index, E data);

    /**
     * 尾部添加节点
     * @param data
     * @return
     */
    boolean add(E data);

    @Override
    default int indexOf(E data) {
        throw new UnsupportedOperationException();
    }

    @Override
    default int lastIndexOf(E data) {
        throw new UnsupportedOperationException();
    }

    /**
     * 是否包含data结点
     * @param data
     * @return
     */
    boolean contains(E data);

    /**
     * 根据index移除结点
     * @param index
     * @return
     */
    E remove(int index);

    /**
     * 根据data移除结点
     * @param data
     * @return
     */
    boolean remove(E data);


    @Override
    default Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }
}