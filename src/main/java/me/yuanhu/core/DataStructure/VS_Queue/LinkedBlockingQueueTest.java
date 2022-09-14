package me.yuanhu.core.DataStructure.VS_Queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBlockingQueueTest {
    public static int eventNum = 10000000;

    public static void main(String[] args) {
        final BlockingQueue<LogEvent> queue = new LinkedBlockingQueue<>(65536);
        final long startTime = System.currentTimeMillis();
        new Thread(() -> {
            int i = 0;
            while (i < eventNum) {
//                LogEvent logEvent = LogEvent.LogEventRecycler.get();
//                logEvent.reuse(i, "c" + i);
                LogEvent logEvent = new LogEvent(i, "c" + i);
                try {
                    queue.put(logEvent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }).start();

        new Thread(() -> {
            int k = 0;
            while (k < eventNum) {
                try {
                    LogEvent logEvent = queue.take();
//                    logEvent.recycle();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                k++;
            }
            long endTime = System.currentTimeMillis();
            System.out
                    .println("costTime = " + (endTime - startTime) + "ms");

        }).start();
    }
}