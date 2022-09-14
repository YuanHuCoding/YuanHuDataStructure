package me.yuanhu.core.DataStructure.VS_Queue;

import com.lmax.disruptor.EventFactory;

public class LogEventFactory  implements EventFactory<LogEvent> {

    private static Long id=1L;
    @Override
    public LogEvent newInstance() {
        return new LogEvent(id++,"content");
    }
}
