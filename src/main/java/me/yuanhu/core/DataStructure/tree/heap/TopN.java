package me.yuanhu.core.DataStructure.tree.heap;

import java.io.IOException;
import java.util.*;

public class TopN {
    /**
     * 查找最大TopN，使用大小为n的最小堆优先队列
     */
    public static <Key extends Comparable<Key>> List<Key> maxTopN(Iterator<Key> iterator, int n) throws IOException {

        // 定义大小为n+1的最小堆优先队列
        MinPriorityQueue<Key> minPQ = new MinPriorityQueue<>(n + 1);

        // 开始元素遍历
        while (iterator.hasNext()) {

            // 将元素放入到队列合适位置
            minPQ.insert(iterator.next());

            // 删除队列中最小的元素，使队列大小保持n
            if (minPQ.size() > n) {
                minPQ.delMin();
            }
        }
//
//        // 通过栈翻转，使元素倒序排列
//        Stack<Key> stack = new Stack<>();
//        for (Key element : minPQ) {
//            stack.push(element);
//        }
//
//        List<Key> result = new ArrayList<>(stack.size());
//        for (Key element : stack) {
//            result.add(element);
//        }

        return null;
    }

    /**
     * 查找最小TopN，使用大小为n的最大堆优先队列
     */
    public static <Key extends Comparable<Key>> List<Key> minTopN(Iterator<Key> iterator, int n) throws IOException {

        // 定义大小为n+1的最大优先队列
        MaxPriorityQueue<Key> maxPQ = new MaxPriorityQueue<>(n + 1);

        // 遍历元素
        while (iterator.hasNext()) {

            // 将元素放入到队列合适位置
            maxPQ.insert(iterator.next());

            // 删除队列中最大的元素，使队列大小保持n
            if (maxPQ.size() > n) {
                maxPQ.delMax();
            }
        }

        // 通过栈翻转，使元素正序排列
//        Stack<Key> stack = new Stack<>();
//        for (Key element : maxPQ) {
//            stack.push(element);
//        }
//
//        List<Key> result = new ArrayList<>(stack.size());
//        for (Key element : stack) {
//            result.add(element);
//        }

        return null;
    }

    public static void main(String[] args) {

        List<Integer> test = Arrays.asList(5, 4, 6, 3, 7, 2, 8, 1, 9, 0);

        try {
            List<Integer> minTop3 = minTopN(test.iterator(), 3);
            System.out.println(minTop3);

            List<Integer> maxTop5 = maxTopN(test.iterator(), 5);
            System.out.println(maxTop5);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
