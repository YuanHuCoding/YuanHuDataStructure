package me.yuanhu.core.DataStructure.VS_Queue;

import me.yuanhu.core.DataStructure.queue.MpscUnboundedArrayQueue;

import java.util.Queue;


public class MessagePassingQueueTest {

    public static void main(String[] args) {
        final Queue<LogEvent> queue = new MpscUnboundedArrayQueue<LogEvent>(65536);
        final long startTime = System.currentTimeMillis();
        new Thread(() -> {
            int i = 0;
            while (i < LinkedBlockingQueueTest.eventNum) {
                LogEvent logEvent = new LogEvent(i, "c" + i);
                queue.offer(logEvent);
                i++;
            }
        }).start();

        new Thread(() -> {
            int k = 0;
            while (k < LinkedBlockingQueueTest.eventNum) {
                LogEvent logEvent = queue.poll();
                if(logEvent!=null){
                    k++;
                }

            }
            long endTime = System.currentTimeMillis();
            System.out
                    .println("costTime = " + (endTime - startTime) + "ms");

        }).start();
    }
}