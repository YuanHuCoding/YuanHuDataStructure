package me.yuanhu.core.DataStructure.queue;

import org.jctools.queues.MessagePassingQueue;
import org.jctools.util.PortableJvmInfo;
import static org.jctools.queues.LinkedArrayQueueUtil.length;

public class MpscUnboundedArrayQueue <E> extends BaseMpscLinkedArrayQueue<E>
{
    public MpscUnboundedArrayQueue(int chunkSize)
    {
        super(chunkSize);
    }


    // 获取可用容量值
    @Override
    protected long availableInQueue(long pIndex, long cIndex)
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public int capacity()
    {
        return MessagePassingQueue.UNBOUNDED_CAPACITY;
    }

    @Override
    public int drain(Consumer<E> c)
    {
        return drain(c, 4096);
    }

    @Override
    public int fill(Supplier<E> s)
    {
        long result = 0;// result is a long because we want to have a safepoint check at regular intervals
        final int capacity = 4096;
        do
        {
            final int filled = fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH);
            if (filled == 0)
            {
                return (int) result;
            }
            result += filled;
        }
        while (result <= capacity);
        return (int) result;
    }

    @Override
    protected int getNextBufferSize(E[] buffer)
    {
        return length(buffer);
    }

    // 获取当前缓冲区的容量值
    @Override
    protected long getCurrentBufferCapacity(long mask)
    {
        return mask;
    }
}
