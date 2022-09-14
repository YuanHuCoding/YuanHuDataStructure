package me.yuanhu.core.DataStructure.queue;

import org.jctools.util.Pow2;

import static org.jctools.queues.LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset;
import static org.jctools.util.UnsafeRefArrayAccess.allocateRefArray;
import static org.jctools.util.UnsafeRefArrayAccess.lvRefElement;

public class SpscUnboundedArrayQueue<E> extends BaseSpscLinkedArrayQueue<E>
{

    public SpscUnboundedArrayQueue(int chunkSize)
    {
        // 通过传入的参数通过Pow2算法获取大于initialCapacity最近的一个2的n次方的值，且不能小于16
        int chunkCapacity = Math.max(Pow2.roundToPowerOfTwo(chunkSize), 16);
        long mask = chunkCapacity - 1;
        E[] buffer = allocateRefArray(chunkCapacity + 1);// 默认分配一个 chunkCapacity + 1 大小的数据缓冲区，多出的一个空间用来指向下一个数组
        producerBuffer = buffer;
        producerMask = mask;
        consumerBuffer = buffer;
        consumerMask = mask;
        //为什么要减1，因为要留一个空间存JUMP对象。
        producerBufferLimit = mask - 1; // we know it's all empty to start with
    }

    @Override
    final boolean offerColdPath(E[] buffer, long mask, long pIndex, long offset, E v, Supplier<? extends E> s)
    {
        // use a fixed lookahead step based on buffer capacity
        // 使用基于缓冲容量的固定前瞻步骤
        final long lookAheadStep = (mask + 1) / 4;
        long pBufferLimit = pIndex + lookAheadStep;

        // go around the buffer or add a new buffer  绕缓冲区一圈后继续 或 添加新缓冲区

        if (null == lvRefElement(buffer, modifiedCalcCircularRefElementOffset(pBufferLimit, mask)))
        {
            producerBufferLimit = pBufferLimit - 1; // 还有足够的空间
            writeToQueue(buffer, v == null ? s.get() : v, pIndex, offset);
        }
        else if (null == lvRefElement(buffer, modifiedCalcCircularRefElementOffset(pIndex + 1, mask)))
        { // buffer is not full    buff还没满
            writeToQueue(buffer, v == null ? s.get() : v, pIndex, offset);
        }
        else
        {
            // we got one slot left to write into, and we are not full. Need to link new buffer.
            // allocate new buffer of same length
            // 我们还有一个插槽要写入，我们还没有满员。 需要链接新的buffer.
            // allocate相同长度的新缓冲区
            final E[] newBuffer = allocateRefArray((int) (mask + 2));
            producerBuffer = newBuffer;
            producerBufferLimit = pIndex + mask - 1;

            linkOldToNew(pIndex, buffer, offset, newBuffer, offset, v == null ? s.get() : v);
        }
        return true;
    }

    //无界，所以返回-1
    @Override
    public int capacity()
    {
        return UNBOUNDED_CAPACITY;
    }
}
