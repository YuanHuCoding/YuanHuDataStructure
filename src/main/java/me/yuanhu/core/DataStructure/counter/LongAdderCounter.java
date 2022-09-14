package me.yuanhu.core.DataStructure.counter;

import java.util.concurrent.atomic.LongAdder;

public class LongAdderCounter extends LongAdder implements Counter {

    @Override
    public long value() {
        return longValue();
    }

}