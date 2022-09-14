package me.yuanhu.core.DataStructure;

/**
 * List顶层接口
 */
public interface IList<E> {

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
     * 清空数据
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
     * 根据index添加数据
     * @param index
     * @param data
     */
    boolean add(int index, E data);

    /**
     * 尾部添加节点
     * @param data
     * @return
     */
    boolean add(E data);

    /**
     * 根据值查询下标
     * @param data
     * @return
     */
    int indexOf(E data);

    /**
     * 根据data值查询最后一个出现在列表中的下标
     * @param data
     * @return
     */
    int lastIndexOf(E data);

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



    default Object[] toArray() {
        return new Object[0];
    }

    default <T> T[] toArray(T[] a) {
        return null;
    }

    String toString();

}