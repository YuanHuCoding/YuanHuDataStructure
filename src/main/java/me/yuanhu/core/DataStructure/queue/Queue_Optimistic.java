package me.yuanhu.core.DataStructure.queue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.LockSupport;

//https://my.oschina.net/xiaominmin/blog/1597667
//https://blog.csdn.net/q291611265/article/details/48242815
public class Queue_Optimistic<E> {
    public interface BarrierHolder {
        Object getBarrier();
    }


    public static LongAdder WaitNum_offer = new LongAdder();
    public static LongAdder WaitNum_take = new LongAdder();

    private Object[] ringBuffer = null;//环形数组
    private AtomicInteger offerSeq = new AtomicInteger(-1);
    private AtomicInteger takeSeq = new AtomicInteger(-1);
    private int size;
    private int mask;

    public Queue_Optimistic(int sizePower) {
        this.size = 1 << sizePower;
        this.ringBuffer = new Object[size];
        for (int i = 0; i < size; i++) {
            ringBuffer[i] = new Entry(i + 1);
        }
        /*
            每个十六进制数4bit，因此8位16进制是4个字节，刚好是一个int整型
            F的二进制码为 1111
            7的二进制码为 0111
            这样一来，整个整数 0x7FFFFFFF 的二进制表示就是除了首位是 0，其余都是1
            就是说，这是最大的整型数 int（因为第一位是符号位，0 表示他是正数）
        * */
        this.mask = Integer.MAX_VALUE >> (31 - sizePower);
    }

    @SuppressWarnings("unchecked")
    private Entry nextOffer() {
        return (Entry) ringBuffer[offerSeq.incrementAndGet() & mask];
    }

    @SuppressWarnings("unchecked")
    private Entry nextTake() {
        return (Entry) ringBuffer[takeSeq.incrementAndGet() & mask];
    }

    public void offer(BarrierHolder holder, E event) throws
            InterruptedException {
        Entry entry = nextOffer();
        Object barrier = holder.getBarrier();
        while (!entry.enterFront(barrier)){
            entry = nextOffer();
        }
        if (entry.event != null) {
            synchronized (barrier) {
                while (entry.event != null) {
                    WaitNum_offer.increment();
                    //生产者是否也需要引入WaitStrategy策略
//                    LockSupport.parkNanos(1);
                    Thread.yield();
//                    Thread.sleep(1);
                    barrier.wait();
                }
            }
        }
        entry.publish(event);
    }

    public E take(BarrierHolder consumer) throws InterruptedException {
        Entry entry = nextTake();
        Object barrier = consumer.getBarrier();
        while(!entry.enterBack(barrier)){
            entry = nextTake();
        }

        if (entry.event == null) {
            synchronized (barrier) {
                while (entry.event == null) {
                    WaitNum_take.increment();
                    barrier.wait();
                    //这里后续要引入WaitStrategy策略
                    LockSupport.parkNanos(10);

//                    for(int i=0;i<1000;i++){
//                        Thread.yield();
//                    }

//                    Thread.sleep(10);
                }
            }
        }
        return entry.take();
    }

    private class Entry {
        private volatile E event = null;
        private AtomicReference<Object> frontDoor = new AtomicReference<Object>();//前门
        private AtomicReference<Object> backDoor = new AtomicReference<Object>();//后门
        private int id;
        public int getId() {
            return id;
        }

        public Entry(int id) {
            this.id = id;
        }

        //生产者检测能否进入前门
        public boolean enterFront(Object barrier) {
            return frontDoor.compareAndSet(null, barrier);
        }


        //消费者检测能否进入后门
        public boolean enterBack(Object barrier) {
            return backDoor.compareAndSet(null, barrier);
        }

        //发布消息
        public void publish(E event) {
            this.event = event;
            frontDoor.set(null);//设置前门为null，运行生产者再次进入
            Object barrier = backDoor.get();
            if (barrier != null) {//如果后门有消费者在等待取消息，则立即唤醒它
                synchronized (barrier) {
                    barrier.notify();
                }
            }
        }

        //取走消息
        public E take() {
            E e = event;
            event = null;
            backDoor.set(null);//设置后门为null,允许消费者再次进入
            Object barrier = frontDoor.get();
            if (barrier != null) {//如果前门有生产者在等待写消息，则立即唤醒它
                synchronized (barrier) {
                    barrier.notify();
                }
            }
            return e;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "id=" + id +
                    '}';
        }
    }
}