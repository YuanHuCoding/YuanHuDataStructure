package me.yuanhu.core.DataStructure.tree.heap;

/**
 * 二叉堆(最小堆)
 *
 * @author skywang
 * @date 2014/03/07
 * http://www.cnblogs.com/skywang12345/p/3610390.html
 * https://github.com/wangkuiwu/datastructs_and_algorithm/blob/master/source/tree/huffman/java/MinHeap.java
 * https://yq.aliyun.com/articles/574087#
 */

import me.yuanhu.core.DataStructure.ISeqList;
import me.yuanhu.core.DataStructure.list.seq_list.SeqList;

import java.util.Comparator;

public class MinHeap<T extends Comparable<T>> {

    private ISeqList<T> mHeap;        // 存放堆的数组
    /**
     * 比较器，设定元素比较规则
     */
    private Comparator<T> comparator;

    public MinHeap() {
        this.mHeap = new SeqList<T>();
    }

    public MinHeap(int initialCapacity, Comparator<T> comparator) {
        this.mHeap = new SeqList<T>(initialCapacity);
        this.comparator=comparator;
    }


    /**
     * 最小堆的向上调整算法(从start开始向上直到0，调整堆)
     * 上浮
     * 注：数组实现的堆中，第N个节点的左孩子的索引值是(2N+1)，右孩子的索引是(2N+2)。
     * c 当前节点(current)的位置，(c-1)/2 父(parent)结点的位置
     */
    private void swim(int c) {
        while (c > 0 && !less((c-1) / 2, c)) {
            swap((c-1)/ 2, c);
            c = (c-1) / 2;
        }
    }

    /**
     * 最小堆的向下调整算法
     * 下沉
     * 数组实现的堆中，第N个节点的左孩子的索引值是(2N+1)，右孩子的索引是(2N+2)。
     * c 当前(current)节点的位置, (2*c)+1左(left)孩子的位置，(2*c)+2是右孩子
     */
    private void sink(int c) {
        int end = mHeap.size();
        while ((2*c) + 1 < end) {
            int l = (2*c) + 1;//左(left)孩子的位置
            // 找出两个子节点中较大的节点
            if (l+1 < end && !less(l, l + 1)) {
                l++;
            }
            // 如果当前节点大与等于子节点，则位置合适，跳出循环
            if (less(c, l)) {
                break;
            }
            // 交换元素
            swap(c, l);
            // 更新元素位置，进入下一轮迭代
            c = l;
        }
    }

    /**
     * 比较
     */
    private boolean less(int i, int j) {
        // 若未指定比较器，则按照默认规则比较
        if (null == comparator) {
            return mHeap.get(i).compareTo(mHeap.get(j)) < 0;
        }
        // 若指定比较器，则使用比较器比较
        return comparator.compare(mHeap.get(i), mHeap.get(j)) < 0;
    }

    /**
     * 交换
     */
    private void swap(int i, int j) {
        T tmp = mHeap.get(i);
        mHeap.set(i,mHeap.get(j));
        mHeap.set(j,tmp);
    }

    /*
     * 最小堆的删除
     *
     * 返回值：
     *     成功，返回被删除的值
     *     失败，返回null
     */
    public int remove(T data) {
        // 如果"堆"已空，则返回-1
        if(mHeap.isEmpty() == true)
            return -1;

        // 获取data在数组中的索引
        int index = mHeap.indexOf(data);
        if (index==-1)
            return -1;

        int size = mHeap.size();
        mHeap.set(index, mHeap.get(size-1));// 用最后元素填补
        mHeap.remove(size - 1);                // 删除最后的元素

        if (mHeap.size() > 1) {
            sink(index);// 从index号位置开始自上向下调整为最小堆
        }
        return 0;
    }

    /*
     * 将data插入到二叉堆中
     */
    public void insert(T data) {
        int size = mHeap.size();

        mHeap.add(data);    // 将"数组"插在表尾
        swim(size); // 向上调整堆
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<mHeap.size(); i++)
            sb.append(mHeap.get(i) +" ");

        return sb.toString();
    }

    public static void main(String[] args) {
        int i;
        int a[] = {80, 40, 30, 60, 90, 70, 10, 50, 20};
        MinHeap<Integer> tree=new MinHeap<Integer>();

        System.out.printf("== 依次添加: ");
        for(i=0; i<a.length; i++) {
            System.out.printf("%d ", a[i]);
            tree.insert(a[i]);
        }

        System.out.printf("\n== 最 小 堆: %s", tree);

        i=15;
        tree.insert(i);
        System.out.printf("\n== 添加元素: %d", i);
        System.out.printf("\n== 最 小 堆: %s", tree);

        i=10;
        tree.remove(i);
        System.out.printf("\n== 删除元素: %d", i);
        System.out.printf("\n== 最 小 堆: %s", tree);
        System.out.printf("\n");
    }
}