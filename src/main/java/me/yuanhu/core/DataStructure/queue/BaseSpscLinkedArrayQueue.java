package me.yuanhu.core.DataStructure.queue;

import org.jctools.queues.IndexedQueueSizeUtil;
import org.jctools.queues.MessagePassingQueue;
import org.jctools.queues.MessagePassingQueueUtil;
import org.jctools.queues.QueueProgressIndicators;
import org.jctools.util.PortableJvmInfo;
import sun.misc.Contended;

import java.util.AbstractQueue;
import java.util.Iterator;

import static org.jctools.queues.LinkedArrayQueueUtil.*;
import static org.jctools.util.UnsafeAccess.UNSAFE;
import static org.jctools.util.UnsafeAccess.fieldOffset;
import static org.jctools.util.UnsafeRefArrayAccess.lvRefElement;
import static org.jctools.util.UnsafeRefArrayAccess.soRefElement;

public abstract class BaseSpscLinkedArrayQueue<E> extends AbstractQueue<E> implements IndexedQueueSizeUtil.IndexedQueue, MessagePassingQueue<E>, QueueProgressIndicators
{
    protected long consumerMask; //消费者指针 掩码
    protected E[] consumerBuffer; //消费者数据缓冲区

    //===================================================

    private final static long C_INDEX_OFFSET = fieldOffset(BaseSpscLinkedArrayQueue.class, "consumerIndex");

    @Contended
    protected long consumerIndex;// 消费者指针，每消费一个数据，指针加2

    final void soConsumerIndex(long newValue)
    {
        UNSAFE.putOrderedLong(this, C_INDEX_OFFSET, newValue);
    }

    @Override
    public final long lvConsumerIndex()
    {
        return UNSAFE.getLongVolatile(this, C_INDEX_OFFSET);
    }

    //===================================================

    private final static long P_INDEX_OFFSET = fieldOffset(BaseSpscLinkedArrayQueue.class,"producerIndex");

    @Contended
    protected long producerIndex;// 生产者指针，每添加一个数据，指针加2

    final void soProducerIndex(long newValue)
    {
        UNSAFE.putOrderedLong(this, P_INDEX_OFFSET, newValue);
    }

    @Override
    public final long lvProducerIndex()
    {
        return UNSAFE.getLongVolatile(this, P_INDEX_OFFSET);
    }

    //===================================================

    protected long producerBufferLimit; //数据链表所分配或者扩展后的容量值

    // 生产者扩充容量值，一般producerMask与consumerMask是一致的，而且需要扩容的数值一般和此值一样
    protected long producerMask; // fixed for chunked and unbounded 对于有界和无界队列，是固定的

    protected E[] producerBuffer;// 生产者数据缓冲区，需要添加的数据放在此,这个对象是会变化的，有可能链接扩容后的新数组

    //===================================================

    private static final Object JUMP = new Object();//跳转标识，说明后面链接的是下一个buffer

    @Override
    public final Iterator<E> iterator()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public final int size()
    {
        return IndexedQueueSizeUtil.size(this,IndexedQueueSizeUtil.PLAIN_DIVISOR);
    }

    @Override
    public final boolean isEmpty()
    {
        return IndexedQueueSizeUtil.isEmpty(this);
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
    }

    @Override
    public long currentProducerIndex()
    {
        return lvProducerIndex();
    }

    @Override
    public long currentConsumerIndex()
    {
        return lvConsumerIndex();
    }

    protected final void soNext(E[] curr, E[] next)
    {
        long offset = nextArrayOffset(curr);
        soRefElement(curr, offset, next);
    }

    @SuppressWarnings("unchecked")
    protected final E[] lvNextArrayAndUnlink(E[] curr)
    {
        final long offset = nextArrayOffset(curr);
        final E[] nextBuffer = (E[]) lvRefElement(curr, offset);
        // prevent GC nepotism 方便GC回收
        soRefElement(curr, offset, null);
        return nextBuffer;
    }

    @Override
    public boolean relaxedOffer(E e)
    {
        return offer(e);
    }

    @Override
    public E relaxedPoll()
    {
        return poll();
    }

    @Override
    public E relaxedPeek()
    {
        return peek();
    }

    @Override
    public int drain(Consumer<E> c)
    {
        return MessagePassingQueueUtil.drain(this, c);
    }

    @Override
    public int fill(Supplier<E> s)
    {
        //result的类型是long，因为我们希望定期进行安全点检查
        long result = 0;// result is a long because we want to have a safepoint check at regular intervals
        final int capacity = capacity();
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
    public int drain(Consumer<E> c, int limit)
    {
        return MessagePassingQueueUtil.drain(this, c, limit);
    }

    @Override
    public int fill(Supplier<E> s, int limit)
    {
        for (int i = 0; i < limit; i++)
        {
            // local load of field to avoid repeated loads after volatile reads
            // 局部加载字段以避免易失性读取后的重复加载
            final E[] buffer = producerBuffer;
            final long index = producerIndex;
            final long mask = producerMask;
            final long offset = modifiedCalcCircularRefElementOffset(index, mask);
            // expected hot path
            if (index < producerBufferLimit)
            {
                writeToQueue(buffer, s.get(), index, offset);
            }
            else
            {
                if (!offerColdPath(buffer, mask, index, offset, null, s))
                {
                    return i;
                }
            }
        }
        return limit;
    }

    @Override
    public void drain(Consumer<E> c, WaitStrategy wait, ExitCondition exit)
    {
        MessagePassingQueueUtil.drain(this, c, wait, exit);
    }

    @Override
    public void fill(Supplier<E> s, WaitStrategy wait, ExitCondition exit)
    {
        while (exit.keepRunning())
        {
            while (fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH) != 0 && exit.keepRunning())
            {
                continue;
            }
            int idleCounter = 0;
            while (exit.keepRunning() && fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH) == 0)
            {
                idleCounter = wait.idle(idleCounter);
            }
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation is correct for single producer thread use only.
     * 此实现仅适用于单个消费者线程。
     */
    @Override
    public boolean offer(final E e)
    {
        // Objects.requireNonNull(e);
        if (null == e)
        {
            throw new NullPointerException();
        }
        // local load of field to avoid repeated loads after volatile reads
        // 局部加载字段以避免易失性读取后的重复加载
        final E[] buffer = producerBuffer;
        final long index = producerIndex;
        final long mask = producerMask;
        final long offset = modifiedCalcCircularRefElementOffset(index, mask);
        // expected hot path
        // 预期的offer热门路径
        if (index < producerBufferLimit)
        {
            writeToQueue(buffer, e, index, offset);
            return true;
        }
        //offer方法的冷门路径
        return offerColdPath(buffer, mask, index, offset, e, null);
    }

    //offer方法的冷门路径
    abstract boolean offerColdPath(
            E[] buffer,
            long mask,
            long pIndex,
            long offset,
            E v,
            Supplier<? extends E> s);

    /**
     * {@inheritDoc}
     * <p>
     * This implementation is correct for single consumer thread use only.
     * 此实现仅适用于单个消费者线程。
     */
    @SuppressWarnings("unchecked")
    @Override
    public E poll()
    {
        // local load of field to avoid repeated loads after volatile reads
        // 局部加载字段以避免易失性读取后的重复加载
        final E[] buffer = consumerBuffer;
        final long index = consumerIndex;
        final long mask = consumerMask;
        final long offset = modifiedCalcCircularRefElementOffset(index, mask);
        final Object e = lvRefElement(buffer, offset);// LoadLoad
        boolean isNextBuffer = e == JUMP;
        if (null != e && !isNextBuffer)
        {
            soConsumerIndex(index + 1);// this ensures correctness on 32bit platforms
            soRefElement(buffer, offset, null);
            return (E) e;
        }
        else if (isNextBuffer)
        {
            return newBufferPoll(buffer, index);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation is correct for single consumer thread use only.
     * 此实现仅适用于单个消费者线程。
     */
    @SuppressWarnings("unchecked")
    @Override
    public E peek()
    {
        final E[] buffer = consumerBuffer;
        final long index = consumerIndex;
        final long mask = consumerMask;
        final long offset = modifiedCalcCircularRefElementOffset(index, mask);
        final Object e = lvRefElement(buffer, offset);// LoadLoad
        if (e == JUMP)
        {
            return newBufferPeek(buffer, index);
        }

        return (E) e;
    }

    final void linkOldToNew(
            final long currIndex,
            final E[] oldBuffer, final long offset,
            final E[] newBuffer, final long offsetInNew,
            final E e)
    {
        soRefElement(newBuffer, offsetInNew, e);// StoreStore
        // link to next buffer and add next indicator as element of old buffer
        soNext(oldBuffer, newBuffer);
        soRefElement(oldBuffer, offset, JUMP);
        // index is visible after elements (isEmpty/poll ordering)
        soProducerIndex(currIndex + 1);// this ensures atomic write of long on 32bit platforms
    }

    final void writeToQueue(final E[] buffer, final E e, final long index, final long offset)
    {
        soRefElement(buffer, offset, e);// StoreStore
        soProducerIndex(index + 1);// this ensures atomic write of long on 32bit platforms 确保了在32位平台上也能原子写入long字段
    }

    private E newBufferPeek(final E[] buffer, final long index)
    {
        E[] nextBuffer = lvNextArrayAndUnlink(buffer);
        consumerBuffer = nextBuffer;
        final long mask = length(nextBuffer) - 2;
        consumerMask = mask;
        final long offset = modifiedCalcCircularRefElementOffset(index, mask);
        return lvRefElement(nextBuffer, offset);// LoadLoad
    }

    private E newBufferPoll(final E[] buffer, final long index)
    {
        E[] nextBuffer = lvNextArrayAndUnlink(buffer);
        consumerBuffer = nextBuffer;
        final long mask = length(nextBuffer) - 2;
        consumerMask = mask;
        final long offset = modifiedCalcCircularRefElementOffset(index, mask);
        final E n = lvRefElement(nextBuffer, offset);// LoadLoad
        if (null == n)
        {
            throw new IllegalStateException("new buffer must have at least one element");
        }
        else
        {
            soConsumerIndex(index + 1);// this ensures correctness on 32bit platforms
            soRefElement(nextBuffer, offset, null);// StoreStore
            return n;
        }
    }
}
