package me.yuanhu.core.DataStructure.counter;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongCounter extends AtomicLong implements Counter{
    private static final long serialVersionUID = 4074772784610639305L;

    @Override
    public void add(long delta) {
        addAndGet(delta);
    }

    @Override
    public void increment() {
        incrementAndGet();
    }

    @Override
    public void decrement() {
        decrementAndGet();
    }

    @Override
    public long value() {
        return get();
    }
}
