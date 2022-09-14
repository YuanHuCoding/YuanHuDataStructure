package me.yuanhu.core.DataStructure.VS_Queue;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

//https://my.oschina.net/OutOfMemory/blog/793275
public class DisruptorTest {

    static class LogEventTranslator implements EventTranslatorTwoArg<LogEvent,Long,String> {
        static final LogEventTranslator INSTANCE = new LogEventTranslator();
        @Override
        public void translateTo(LogEvent event, long sequence, Long arg0, String arg1) {
            event.setLogId(arg0);
            event.setContent(arg1);
        }
    }

    public static void main(String[] args) {
        LogEventFactory factory = new LogEventFactory();
        int ringBufferSize = 65536;
        final Disruptor<LogEvent> disruptor = new Disruptor<>(factory,
                ringBufferSize, DaemonThreadFactory.INSTANCE,
                ProducerType.SINGLE, new BusySpinWaitStrategy());

        LogEventConsumer consumer = new LogEventConsumer();
        disruptor.handleEventsWith(consumer);
        disruptor.start();
        new Thread(() -> {
//            RingBuffer<LogEvent> ringBuffer = disruptor.getRingBuffer();
            for (int i = 0; i < LinkedBlockingQueueTest.eventNum; i++) {
                //使用EventTranslator模式，性能好像要低点
                disruptor.publishEvent(LogEventTranslator.INSTANCE, (long)i,"c" + i);

//                long seq = ringBuffer.next();
//                try {
//                    LogEvent logEvent = ringBuffer.get(seq);// 根据序列号获取预分配的数据槽
//                    logEvent.setLogId(i);
//                    logEvent.setContent("c" + i); // 向数据槽中填充数据
//                } finally {
//                    // 注意，最后的ringBuffer.publish方法必须包含在finally中以确保必须得到调用；如果某个请求的sequence未被提交，
//                    // 将会堵塞后续的发布操作或者其它的producer。Disruptor还提供另外一种形式的调用(EventTranslator)来简化以上操作，并确保 publish 总是得到调用。
//                    ringBuffer.publish(seq);
//                }
            }
        }).start();
    }
}