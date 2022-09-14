package me.yuanhu.core.DataStructure.counter;

import org.jctools.maps.ConcurrentAutoTable;

public class ConcurrentAutoTableCounter extends ConcurrentAutoTable implements Counter {

    @Override
    public long value() {
        return this.get();
    }

}
