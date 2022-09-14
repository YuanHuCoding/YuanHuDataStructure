package me.yuanhu.core.DataStructure.queue;

import org.jctools.queues.IndexedQueueSizeUtil;
import org.jctools.queues.MessagePassingQueue;
import org.jctools.queues.QueueProgressIndicators;
import org.jctools.util.PortableJvmInfo;
import org.jctools.util.Pow2;
import org.jctools.util.RangeUtil;
import sun.misc.Contended;

import java.util.AbstractQueue;
import java.util.Iterator;

import static org.jctools.queues.LinkedArrayQueueUtil.length;
import static org.jctools.queues.LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset;
import static org.jctools.util.UnsafeAccess.UNSAFE;
import static org.jctools.util.UnsafeAccess.fieldOffset;
import static org.jctools.util.UnsafeRefArrayAccess.*;

public abstract class BaseMpscLinkedArrayQueue<E> extends AbstractQueue<E> implements IndexedQueueSizeUtil.IndexedQueue, MessagePassingQueue<E>, QueueProgressIndicators
{

    private final static long P_INDEX_OFFSET = fieldOffset(BaseMpscLinkedArrayQueue.class, "producerIndex");

    @Contended
    protected long producerIndex;// 生产者指针，每添加一个数据，指针加2

    //获取生产者指针位置
    @Override
    public final long lvProducerIndex()
    {
        //getLongVolatile 强制从主存中获取属性值
        return UNSAFE.getLongVolatile(this, P_INDEX_OFFSET);
    }

    //更新生产者指针位置（内存非立即可见）
    final void soProducerIndex(long newValue)
    {
        // putOrderedObject操作会插入一个#storestore的内存屏障，而非putXxxVolatile的#storeload的内存屏障，所以效率会高。
        // 所谓#storestore的内存屏障，是说虚拟机在对于这样的语句【Store1; StoreStore; Store2】，在Store2及后续写入操作执行前，
        // 保证Store1的写入操作对其它处理器可见。
        UNSAFE.putOrderedLong(this, P_INDEX_OFFSET, newValue);
    }

    //CAS方式更新生产者指针位置
    final boolean casProducerIndex(long expect, long newValue)
    {
        return UNSAFE.compareAndSwapLong(this, P_INDEX_OFFSET, expect, newValue);
    }

    //=========================================================================

    private final static long C_INDEX_OFFSET = fieldOffset(BaseMpscLinkedArrayQueue.class,"consumerIndex");

    protected long consumerMask;
    protected E[] consumerBuffer;// 消费者数据缓冲区

    @Contended
    protected long consumerIndex; // 消费者指针，每移除一个数据，指针加2

    @Override
    public final long lvConsumerIndex()
    {
        return UNSAFE.getLongVolatile(this, C_INDEX_OFFSET);
    }

    final void soConsumerIndex(long newValue)
    {
        UNSAFE.putOrderedLong(this, C_INDEX_OFFSET, newValue);
    }

    private final static long P_LIMIT_OFFSET = fieldOffset(BaseMpscLinkedArrayQueue.class,"producerLimit");


    //=========================================================================

    @Contended
    private volatile long producerLimit;// 数据链表所分配或者扩展后的容量值
    protected long producerMask;  // 生产者扩充容量值，一般producerMask与consumerMask是一致的，而且需要扩容的数值一般和此值一样
    protected E[] producerBuffer; // 生产者数据缓冲区，需要添加的数据放在此

    final long lvProducerLimit()
    {
        return producerLimit;
    }

    // 通过CAS尝试对阈值进行修改扩容处理
    final boolean casProducerLimit(long expect, long newValue)
    {
        return UNSAFE.compareAndSwapLong(this, P_LIMIT_OFFSET, expect, newValue);
    }

    final void soProducerLimit(long newValue)
    {
        UNSAFE.putOrderedLong(this, P_LIMIT_OFFSET, newValue);
    }

    //===========================================

    private static final Object JUMP = new Object();
    private static final int CONTINUE_TO_P_INDEX_CAS = 0;
    private static final int RETRY = 1;// 重新尝试，有可能是因为并发原因，CAS操作指针失败，所以需要重新尝试添加动作
    private static final int QUEUE_FULL = 2; // 队列已满，直接返回false操作
    private static final int QUEUE_RESIZE = 3;// 需要扩容处理，扩容后的容量值producerLimit一般都是mask的N倍



    /**
     * @param initialCapacity the queue initial capacity. If chunk size is fixed this will be the chunk size.
     *                        Must be 2 or more.
     */
    public BaseMpscLinkedArrayQueue(final int initialCapacity)
    {
        // 校验队列容量值，大小必须不小于2
        RangeUtil.checkGreaterThanOrEqual(initialCapacity, 2, "initialCapacity");

        // 通过传入的参数通过Pow2算法获取大于initialCapacity最近的一个2的n次方的值
        int p2capacity = Pow2.roundToPowerOfTwo(initialCapacity);
        // leave lower bit of mask clear
        long mask = (p2capacity - 1) << 1;// 通过p2capacity计算获得mask值，该值后续将用作扩容的值
        // need extra element to point at next array
        E[] buffer = allocateRefArray(p2capacity + 1);// 默认分配一个 p2capacity + 1 大小的数据缓冲区
        producerBuffer = buffer;
        producerMask = mask;
        consumerBuffer = buffer;
        consumerMask = mask;
        // 同时用mask作为初始化队列的Limit值，当生产者指针producerIndex超过该Limit值时就需要做扩容处理
        soProducerLimit(mask); // we know it's all empty to start with
    }

    @Override
    public final Iterator<E> iterator()
    {
        throw new UnsupportedOperationException();
    }

    //获取缓冲区数据大小其实很简单，就是拿着生产指针减去消费指针，但是为了防止并发操作计算错，才用了死循环的方式计算size值；
    @Override
    public final int size()
    {
        // NOTE: because indices are on even numbers we cannot use the size util.

        /*
         * It is possible for a thread to be interrupted or reschedule between the read of the producer and
         * consumer indices, therefore protection is required to ensure size is within valid range. In the
         * event of concurrent polls/offers to this method the size is OVER estimated as we read consumer
         * index BEFORE the producer index.
         */
        long after = lvConsumerIndex();// 获取消费指针
        long size;
        while (true) // 为了防止在获取大小的时候指针发生变化，那么则死循环自旋方式获取大小数值
        {
            final long before = after;
            final long currentProducerIndex = lvProducerIndex(); // 获取生产者指针
            after = lvConsumerIndex();// 获取消费指针
            // 如果后获取的消费指针after和之前获取的消费指针before相等的话，那么说明此刻还没有指针变化
            if (before == after)
            {
                // 那么则直接通过生产指针直接减去消费指针，然后向偏移一位，即除以2，得出最后size大小
                size = ((currentProducerIndex - after) >> 1);
                // 计算完了之后则直接break中断处理
                break;
            }
            // 若消费指针前后不一致，那么可以说是由于并发原因导致了指针发生了变化；
            // 那么则进行下一次循环继续获取最新的指针值再次进行判断
        }
        // Long overflow is impossible, so size is always positive. Integer overflow is possible for the unbounded
        // indexed queues.
        if (size > Integer.MAX_VALUE)
        {
            return Integer.MAX_VALUE;
        }
        else
        {
            return (int) size;
        }
    }

    //通过前面我们已经知道了，添加数据的话生产指针在不停的累加操作，而做移除数据的时候消费指针也在不停的累加操作；
    //那么这种指针总会有一天会碰面的吧，碰面的那个时候则是数据已经空空如也的时刻；
    @Override
    public final boolean isEmpty()
    {
        // Order matters!
        // Loading consumer before producer allows for producer increments after consumer index is read.
        // This ensures this method is conservative in it's estimate. Note that as this is an MPMC there is
        // nothing we can do to make this an exact method.
        // 这个就简单了，直接判断消费指针和生产指针是不是相等就知道了
        return (this.lvConsumerIndex() == this.lvProducerIndex());
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
    }

    //该方法主要完成新的元素的放置，同时也完成了扩容操作，采用单向链表指针关系，将原缓冲区和新创建的缓冲区衔接起来；
    @Override
    public boolean offer(final E e)
    {
        if (null == e)// 待添加的元素e不允许为空，否则抛空指针异常
        {
            throw new NullPointerException();
        }

        long mask;
        E[] buffer;
        long pIndex;

        while (true)
        {
            long producerLimit = lvProducerLimit();// 获取当前数据Limit的阈值
            pIndex = lvProducerIndex(); // 获取当前生产者指针位置
            // lower bit is indicative of resize, if we see it we spin until it's cleared
            //较低的位表示正在调整大小，如果我们看到它，我们旋转直到它被清除。
            if ((pIndex & 1) == 1)
            {
                continue;
            }
            // pIndex is even (lower bit is 0) -> actual index is (pIndex >> 1)

            // mask/buffer may get changed by resizing -> only use for array access after successful CAS.
            mask = this.producerMask;
            buffer = this.producerBuffer;
            // a successful CAS ties the ordering, lv(pIndex) - [mask/buffer] -> cas(pIndex)

            // assumption behind this optimization is that queue is almost always empty or near empty
            //这种优化背后的假设是，队列几乎总是空的或接近空的。
            if (producerLimit <= pIndex)// 当阈值小于等于生产者指针位置时，则需要扩容，否则直接通过CAS操作对pIndex做加2处理
            {
                // 通过offerSlowPath返回状态值，来查看怎么来处理这个待添加的元素
                int result = offerSlowPath(mask, pIndex, producerLimit);
                switch (result)
                {
                    case CONTINUE_TO_P_INDEX_CAS:
                        break;
                    case RETRY:// 可能由于并发原因导致CAS失败，那么则再次重新尝试添加元素
                        continue;
                    case QUEUE_FULL:// 队列已满，直接返回false操作
                        return false;
                    case QUEUE_RESIZE:// 队列需要扩容操作
                        resize(mask, buffer, pIndex, e);// 对队列进行直接扩容操作
                        return true;
                }
            }

            // 能走到这里，则说明当前的生产者指针位置还没有超过阈值，因此直接通过CAS操作做加2处理
            if (casProducerIndex(pIndex, pIndex + 2))
            {
                break;
            }
        }
        // 获取计算需要添加元素的位置
        // INDEX visible before ELEMENT
        final long offset = modifiedCalcCircularRefElementOffset(pIndex, mask);
        // 在buffer的offset位置添加e元素
        soRefElement(buffer, offset, e); // release element e
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation is correct for single consumer thread use only.
     */
    //该方法主要阐述了该队列是如何的移除数据的；取出的数据如果为JUMP空对象的话，那么则准备从下一个缓冲区获取数据元素，否则还是从当前的缓冲区对象中移除元素，并且更新消费指针；
    @SuppressWarnings("unchecked")
    @Override
    public E poll()
    {
        final E[] buffer = consumerBuffer;
        final long index = consumerIndex;
        final long mask = consumerMask;

        // 根据消费指针与mask来获取当前需要从哪个位置开始来移除元素
        final long offset = modifiedCalcCircularRefElementOffset(index, mask);
        // 从buffer缓冲区的offset位置获取元素内容
        Object e = lvRefElement(buffer, offset);// LoadLoad
        if (e == null)// 如果元素为null的话
        {
            // 则再探讨看看消费指针是不是和生产指针是不是相同
            if (index != lvProducerIndex())
            {
                // poll() == null iff queue is empty, null element is not strong enough indicator, so we must
                // check the producer index. If the queue is indeed not empty we spin until element is
                // visible.
                // 若不相同的话，则先尝试从buffer缓冲区的offset位置获取元素先，若获取元素为null则结束while处理
                do
                {
                    e = lvRefElement(buffer, offset);
                }
                while (e == null);
            }
            // 说明消费指针是不是和生产指针是相等的，那么则缓冲区的数据已经被消费完了，直接返回null即可
            else
            {
                return null;
            }
        }

        // 如果元素为JUMP空对象的话，那么意味着我们就得获取下一缓冲区进行读取数据了
        if (e == JUMP)
        {
            final E[] nextBuffer = getNextBuffer(buffer, mask);
            return newBufferPoll(nextBuffer, index);
        }

        // 能执行到这里，说明需要移除的元素既不是空的，也不是JUMP空对象，那么则就按照正常处理置空即可
        // 移除元素时，则将buffer缓冲区的offset位置的元素置为空即可
        soRefElement(buffer, offset, null); // release element null
        // 同时也通过CAS操作增加消费指针的关系，加2操作
        soConsumerIndex(index + 2); // release cIndex
        return (E) e;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation is correct for single consumer thread use only.
     */
    @SuppressWarnings("unchecked")
    @Override
    public E peek()
    {
        final E[] buffer = consumerBuffer;
        final long index = consumerIndex;
        final long mask = consumerMask;

        final long offset = modifiedCalcCircularRefElementOffset(index, mask);
        Object e = lvRefElement(buffer, offset);// LoadLoad
        if (e == null && index != lvProducerIndex())
        {
            // peek() == null iff queue is empty, null element is not strong enough indicator, so we must
            // check the producer index. If the queue is indeed not empty we spin until element is visible.
            do
            {
                e = lvRefElement(buffer, offset);
            }
            while (e == null);
        }
        if (e == JUMP)
        {
            return newBufferPeek(getNextBuffer(buffer, mask), index);
        }
        return (E) e;
    }

    /**
     * We do not inline resize into this method because we do not resize on fill.
     */
    //该方法主要通过一系列的if...else判断，并结合子类MpscUnboundedArrayQueue的一些重写方法来判断针对该新添加的元素要做何种状态处理；
    private int offerSlowPath(long mask, long pIndex, long producerLimit)
    {
        final long cIndex = lvConsumerIndex();// 获取消费者指针
        // 获取当前缓冲区的容量值，getCurrentBufferCapacity方法由子类MpscUnboundedArrayQueue实现，默认返回mask值
        long bufferCapacity = getCurrentBufferCapacity(mask);

        if (cIndex + bufferCapacity > pIndex)// 如果消费指针加上容量值如果超过了生产指针，那么则会尝试进行扩容处理
        {
            if (!casProducerLimit(producerLimit, cIndex + bufferCapacity))
            {
                // retry from top
                return RETRY;
            }
            else
            {
                // continue to pIndex CAS
                return CONTINUE_TO_P_INDEX_CAS;
            }
        }
        // full and cannot grow
        else if (availableInQueue(pIndex, cIndex) <= 0)//子类MpscUnboundedArrayQueue默认返回Integer.MAX_VALUE值，所以不会进入此分支
        {
            // offer should return false;
            return QUEUE_FULL;
        }
        // grab index for resize -> set lower bit 尝试扩容队列
        else if (casProducerIndex(pIndex, pIndex + 1))
        {
            // trigger a resize
            return QUEUE_RESIZE;
        }
        else
        {
            // failed resize attempt, retry from top
            return RETRY;
        }
    }

    /**
     * @return available elements in queue * 2
     */
    protected abstract long availableInQueue(long pIndex, long cIndex);

    @SuppressWarnings("unchecked")
    private E[] getNextBuffer(final E[] buffer, final long mask)
    {
        // 获取下一个缓冲区的偏移位置值
        final long offset = nextArrayOffset(mask);
        // 从buffer缓冲区的offset位置获取下一个缓冲区数组
        final E[] nextBuffer = (E[]) lvRefElement(buffer, offset);
        // 获取出来后，同时将buffer缓冲区的offset位置置为空，代表指针已经被取出，原来位置没用了，清空即可
        soRefElement(buffer, offset, null);
        return nextBuffer;
    }

    private long nextArrayOffset(long mask)
    {
        return modifiedCalcCircularRefElementOffset(mask + 2, Long.MAX_VALUE);
    }

    private E newBufferPoll(E[] nextBuffer, long index)
    {
        // 从下一个新的缓冲区中找到需要移除数据的指针位置
        final long offset = newBufferAndOffset(nextBuffer, index);
        // 从newBuffer新的缓冲区中offset位置取出元素
        final E n = lvRefElement(nextBuffer, offset);// LoadLoad
        if (n == null)// 若取出的元素为空，则直接抛出异常
        {
            throw new IllegalStateException("new buffer must have at least one element");
        }
        // 如果取出的元素不为空，那么先将这个元素原先的位置内容先清空掉
        soRefElement(nextBuffer, offset, null);// StoreStore
        // 然后通过Unsafe对象调用native方法，修改消费指针的数值偏移加2处理
        soConsumerIndex(index + 2);
        return n;
    }

    private E newBufferPeek(E[] nextBuffer, long index)
    {
        final long offset = newBufferAndOffset(nextBuffer, index);
        final E n = lvRefElement(nextBuffer, offset);// LoadLoad
        if (null == n)
        {
            throw new IllegalStateException("new buffer must have at least one element");
        }
        return n;
    }

    private long newBufferAndOffset(E[] nextBuffer, long index)
    {
        consumerBuffer = nextBuffer;
        consumerMask = (length(nextBuffer) - 2) << 1;
        return modifiedCalcCircularRefElementOffset(index, consumerMask);
    }

    @Override
    public long currentProducerIndex()
    {
        return lvProducerIndex() / 2;
    }

    @Override
    public long currentConsumerIndex()
    {
        return lvConsumerIndex() / 2;
    }

    @Override
    public abstract int capacity();

    @Override
    public boolean relaxedOffer(E e)
    {
        return offer(e);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E relaxedPoll()
    {
        final E[] buffer = consumerBuffer;
        final long index = consumerIndex;
        final long mask = consumerMask;

        final long offset = modifiedCalcCircularRefElementOffset(index, mask);
        Object e = lvRefElement(buffer, offset);// LoadLoad
        if (e == null)
        {
            return null;
        }
        if (e == JUMP)
        {
            final E[] nextBuffer = getNextBuffer(buffer, mask);
            return newBufferPoll(nextBuffer, index);
        }
        soRefElement(buffer, offset, null);
        soConsumerIndex(index + 2);
        return (E) e;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E relaxedPeek()
    {
        final E[] buffer = consumerBuffer;
        final long index = consumerIndex;
        final long mask = consumerMask;

        final long offset = modifiedCalcCircularRefElementOffset(index, mask);
        Object e = lvRefElement(buffer, offset);// LoadLoad
        if (e == JUMP)
        {
            return newBufferPeek(getNextBuffer(buffer, mask), index);
        }
        return (E) e;
    }

    @Override
    public int fill(Supplier<E> s)
    {
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
    public int fill(Supplier<E> s, int batchSize)
    {
        long mask;
        E[] buffer;
        long pIndex;
        int claimedSlots;
        while (true)
        {
            long producerLimit = lvProducerLimit();
            pIndex = lvProducerIndex();
            // lower bit is indicative of resize, if we see it we spin until it's cleared
            if ((pIndex & 1) == 1)
            {
                continue;
            }
            // pIndex is even (lower bit is 0) -> actual index is (pIndex >> 1)

            // NOTE: mask/buffer may get changed by resizing -> only use for array access after successful CAS.
            // Only by virtue offloading them between the lvProducerIndex and a successful casProducerIndex are they
            // safe to use.
            mask = this.producerMask;
            buffer = this.producerBuffer;
            // a successful CAS ties the ordering, lv(pIndex) -> [mask/buffer] -> cas(pIndex)

            // we want 'limit' slots, but will settle for whatever is visible to 'producerLimit'
            long batchIndex = Math.min(producerLimit, pIndex + 2 * batchSize);

            if (pIndex >= producerLimit || producerLimit < batchIndex)
            {
                int result = offerSlowPath(mask, pIndex, producerLimit);
                switch (result)
                {
                    case CONTINUE_TO_P_INDEX_CAS:
                        // offer slow path verifies only one slot ahead, we cannot rely on indication here
                    case RETRY:
                        continue;
                    case QUEUE_FULL:
                        return 0;
                    case QUEUE_RESIZE:
                        resize(mask, buffer, pIndex, s.get());
                        return 1;
                }
            }

            // claim limit slots at once
            if (casProducerIndex(pIndex, batchIndex))
            {
                claimedSlots = (int) ((batchIndex - pIndex) / 2);
                break;
            }
        }

        for (int i = 0; i < claimedSlots; i++)
        {
            final long offset = modifiedCalcCircularRefElementOffset(pIndex + 2 * i, mask);
            soRefElement(buffer, offset, s.get());
        }
        return claimedSlots;
    }

    @Override
    public void fill(
            Supplier<E> s,
            WaitStrategy w,
            ExitCondition exit)
    {

        while (exit.keepRunning())
        {
            if (fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH) == 0)
            {
                int idleCounter = 0;
                while (exit.keepRunning() && fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH) == 0)
                {
                    idleCounter = w.idle(idleCounter);
                }
            }
        }
    }

    @Override
    public int drain(Consumer<E> c)
    {
        return drain(c, capacity());
    }

    @Override
    public int drain(final Consumer<E> c, final int limit)
    {
        // Impl note: there are potentially some small gains to be had by manually inlining relaxedPoll() and hoisting
        // reused fields out to reduce redundant reads.
        int i = 0;
        E m;
        for (; i < limit && (m = relaxedPoll()) != null; i++)
        {
            c.accept(m);
        }
        return i;
    }

    @Override
    public void drain(Consumer<E> c, WaitStrategy w, ExitCondition exit)
    {
        int idleCounter = 0;
        while (exit.keepRunning())
        {
            E e = relaxedPoll();
            if (e == null)
            {
                idleCounter = w.idle(idleCounter);
                continue;
            }
            idleCounter = 0;
            c.accept(e);
        }
    }

    private void resize(long oldMask, E[] oldBuffer, long pIndex, E e)
    {
        int newBufferLength = getNextBufferSize(oldBuffer);// 获取oldBuffer的长度值
        final E[] newBuffer = allocateRefArray(newBufferLength);// 重新创建新的缓冲区

        producerBuffer = newBuffer;// 将新创建的缓冲区赋值到生产者缓冲区对象上
        final int newMask = (newBufferLength - 2) << 1;
        producerMask = newMask;

        // 根据oldMask获取偏移位置值
        final long offsetInOld = modifiedCalcCircularRefElementOffset(pIndex, oldMask);
        // 根据newMask获取偏移位置值
        final long offsetInNew = modifiedCalcCircularRefElementOffset(pIndex, newMask);

        // 将元素e设置到新的缓冲区newBuffer的offsetInNew位置处
        soRefElement(newBuffer, offsetInNew, e);// element in new array
        // 通过nextArrayOffset(oldMask)计算新的缓冲区将要放置旧的缓冲区的哪个位置
        // 将新的缓冲区newBuffer设置到旧的缓冲区oldBuffer的nextArrayOffset(oldMask)位置处
        // 主要是将oldBuffer中最后一个元素的位置指向新的缓冲区newBuffer
        // 这样就构成了一个单向链表指向的关系
        soRefElement(oldBuffer, nextArrayOffset(oldMask), newBuffer);// buffer linked

        // ASSERT code
        final long cIndex = lvConsumerIndex();
        final long availableInQueue = availableInQueue(pIndex, cIndex);
        RangeUtil.checkPositive(availableInQueue, "availableInQueue");

        // Invalidate racing CASs
        // We never set the limit beyond the bounds of a buffer
        // 重新扩容阈值，因为availableInQueue反正都是Integer.MAX_VALUE值，所以自然就取mask值啦
        // 因此针对MpscUnboundedArrayQueue来说，扩容的值其实就是mask的值的大小
        soProducerLimit(pIndex + Math.min(newMask, availableInQueue));

        // make resize visible to the other producers
        // 设置生产者指针加2处理
        soProducerIndex(pIndex + 2);

        // INDEX visible before ELEMENT, consistent with consumer expectation

        // make resize visible to consumer
        // 用一个空对象来衔接新老缓冲区，凡是在缓冲区中碰到JUMP对象的话，那么就得琢磨着准备着获取下一个缓冲区的数据元素了
        soRefElement(oldBuffer, offsetInOld, JUMP);
    }

    /**
     * @return next buffer size(inclusive of next array pointer)
     */
    protected abstract int getNextBufferSize(E[] buffer);

    /**
     * @return current buffer capacity for elements (excluding next pointer and jump entry) * 2
     */
    protected abstract long getCurrentBufferCapacity(long mask);
}
