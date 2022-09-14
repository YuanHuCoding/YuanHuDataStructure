package me.yuanhu.core.DataStructure.VS_Queue;

import me.yuanhu.core.DataStructure.queue.Queue_Optimistic;

public class Test_OptimisticQueue {

    public static class MyBarrierHolder implements Queue_Optimistic.BarrierHolder {
        private Object event;

        private MyBarrierHolder(Object event){
            this.event=event;
        }

        public static MyBarrierHolder create(Object event){
            return new MyBarrierHolder(event);
        }

        @Override
        public Object getBarrier() {
            return event;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final Queue_Optimistic<LogEvent> queue = new Queue_Optimistic<LogEvent>(16);
        final long startTime = System.currentTimeMillis();
        new Thread(() -> {
            byte[] lock_obj=new byte[0];
            MyBarrierHolder myBarrierHolder = MyBarrierHolder.create(lock_obj);
            int i = 0;
            while (i < LinkedBlockingQueueTest.eventNum) {
                LogEvent logEvent = new LogEvent(i, "c" + i);

                try {
                    queue.offer(myBarrierHolder,logEvent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }).start();

        new Thread(() -> {
            byte[] lock_obj=new byte[0];
            MyBarrierHolder myBarrierHolder = MyBarrierHolder.create(lock_obj);
            int k = 0;
            LogEvent logEvent=null;
            while (k < LinkedBlockingQueueTest.eventNum) {
                try {
                    logEvent=queue.take(myBarrierHolder);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                k++;
            }
            long endTime = System.currentTimeMillis();
            System.out.println("costTime = " + (endTime - startTime) + "ms");
            System.out.println("WaitNum_offer="+ Queue_Optimistic.WaitNum_offer);
            System.out.println("WaitNum_take="+ Queue_Optimistic.WaitNum_take);

        }).start();
    }
}
