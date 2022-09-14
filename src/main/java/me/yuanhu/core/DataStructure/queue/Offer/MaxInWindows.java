package me.yuanhu.core.DataStructure.queue.Offer;

import me.yuanhu.core.DataStructure.java.jcf.ArrayDeque;
import me.yuanhu.core.DataStructure.java.jcf.ArrayList;

/*
题目：给定一个数组和滑动窗口的大小，请找出所有滑动窗口里的最大值。
举例说明
例如，如果输入数组{2,3,4,2,6,2,5,1}及滑动窗口的大小，那么一共存在 6 个滑动窗口，它们的最大值分别为{4,4,6,6,6,5}。

我们并不把滑动窗口的每个数值都存入队列中，而只把有可能成为滑动窗口最大值的数值存入到一个两端开口的队列。接着以输入数字{2,3,4,2,6,2,5,1}为例一步分析。

数组的第一个数字是 2，把它存入队列中。第二个数字是3.由于它比前一个数字 2 大，因此 2不可能成为滑动窗口中的最大值。
2 先从队列里删除，再把3存入到队列中。此时队列中只有一个数字 3。针对第三个数字 4 的步骤类似，最终在队列中只剩下一个数字 4。
此时滑动窗口中已经有 3 个数字，而它的最大值 4 位于队列的头部。

接下来处理第四个数字 2。2 比队列中的数字 4 小。当 4 滑出窗口之后 2 还是有可能成为滑动窗口的最大值，因此把 2 存入队列的尾部。
现在队列中有两个数字 4 和 2，其中最大值 4 仍然位于队列的头部。

下一个数字是 6。由于它比队列中已有的数字 4 和 2 都大，因此这时 4 和 2 已经不可能成为滑动窗口中的最大值。先把 4 和 2 从队列中删除，
再把数字 6 存入队列。这个时候最大值 6 仍然位于队列的头部。

第六个数字是 2。由于它比队列中已有的数字 6 小，所以 2 也存入队列的尾部。此时队列中有两个数字，其中最大值 6 位于队列的头部。

接下来的数字是 5。在队列中已有的两个数字 6 和 2 里，2 小于 5，因此 2 不可能是一个滑动窗口的最大值，可以把它从队列的尾部删除。
删除数字 2 之后，再把数字 5 存入队列。此时队列里剩下两个数字 6 和 5，其中位于队列头部的是最大值 6。

数组最后一个数字是 1，把 1 存入队列的尾部。注意到位于队列头部的数字 6 是数组的第 5 个数字，此时的滑动窗口已经不包括这个数字了，
因此应该把数字 6 从队列删除。那么怎么知道滑动窗口是否包括一个数字？应该在队列里存入数字在数组里的下标，而不是数值。当一个数字的
下标与当前处理的数字的下标之差大于或者等于滑动窗口的大小时，这个数字已经从滑动窗口中滑出，可以从队列中删除了。
* */
public class MaxInWindows {
    public static ArrayList<Integer> maxInWindows(int [] num, int size)
    {
        ArrayList<Integer> maxList = new ArrayList<Integer>();
        if(num.length<=0||size<=0||num.length<size)
            return maxList;

        //创建一个双端队列保存每个滑动窗口的最大值的下标
        ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
        //创建一个变量start用于记录当前滑动窗口的最大值的下标
        int start = 0;
        // 窗口还没有被填满时，找最大值的索引
        for (int i = 0; i < num.length; i++) {
            start = i + 1 - size;//当start大于0的时候表示第一个窗口已经不能再移动了
            if(queue.isEmpty()){
                queue.add(i);
            }else if(start > queue.peekFirst()){//这个条件表示当前窗口start的值比上一个窗口的start更大
                queue.pollFirst();
            }
            // 如果索引对应的值比之前存储的索引值对应的值大或者相等，就删除之前存储的值
            while(!queue.isEmpty() && num[queue.peekLast()] <= num[i]){
                //这种情况表示，队列队尾位置对应的元素比当前元素更小，就移除他，因为需要得到的是窗口最大值
                queue.pollLast();
            }
            queue.add(i); //  添加索引
            if(start >= 0){
                //实际上当start=0的时候第第一个滑动窗口，这时队列中保存的是第一个滑动窗口最大值的下标，直接添加就行
                maxList.add(num[queue.peekFirst()]);
            }
        }
        return maxList;
    }
    //测试
    public static void main(String[] args){
        int [] num = {2,3,4,2,6,2,5,1};
        ArrayList<Integer> maxInWindows=MaxInWindows.maxInWindows(num,3);
        System.out.println(maxInWindows);
    }
}
