package me.yuanhu.core.DataStructure.VS_Queue;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

    public class ArrayBlockingQueueTest {

        public static void main(String[] args) {
            final BlockingQueue<LogEvent> queue = new ArrayBlockingQueue<LogEvent>(65536);
            final long startTime = System.currentTimeMillis();
            new Thread(() -> {
                int i = 0;
                while (i < LinkedBlockingQueueTest.eventNum) {
//                LogEvent logEvent = LogEvent.Recycler_LogEvent.get();
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
                while (k < LinkedBlockingQueueTest.eventNum) {
                    try {
                        LogEvent logEvent = queue.take();
//                        logEvent.recycle();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    k++;
                }
                long endTime = System.currentTimeMillis();
                System.out
                        .println("costTime = " + (endTime - startTime) + "ms");
//                Recycler.statistics();

            }).start();
        }
    }