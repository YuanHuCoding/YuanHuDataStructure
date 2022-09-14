package me.yuanhu.core.DataStructure.java.jcf;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/*
    ArrayDeque不是线程安全的。
    ArrayDeque不可以存取null元素，因为系统根据某个位置是否为null来判断元素的存在。
    当作为栈使用时，性能比Stack好；当作为队列使用时，性能比LinkedList好。
    ArrayDeque并不是一个固定大小的队列，每次队列满了以后就将队列容量扩大一倍（doubleCapacity()），
    因此加入一个元素总是能成功，而且也不会抛出一个异常。也就是说ArrayDeque是一个没有容量限制的队列。
* */
public class ArrayDeque<E> extends AbstractCollection<E>
        implements Deque<E>, Cloneable, Serializable
{
    //elements就是存储元素的数组。ArrayDeque的高效来源于head和tail这两个变量，它们使得物理上简单的从头到尾的数组变为了一个逻辑上循环的数组，避免了在头尾操作时的移动。
    //储存元素的数组，长度总是2的次幂，数组不允许饱和，在使用addX方法添加元素以后，如果数组饱和了，那么就会立即扩容到原来长度的两倍
    transient Object[] elements; // non-private to simplify nested class access

    /**
     * The index of the element at the head of the deque (which is the
     * element that would be removed by remove() or pop()); or an
     * arbitrary number equal to tail if the deque is empty.
     * 队列的头部元素索引（即将pop出的一个）
     */
    transient int head;

    /**
     * The index at which the next element would be added to the tail
     * of the deque (via addLast(E), add(E), or push(E)).
     *  队列下一个要添加的元素索引，也就是最后一个元素+1
     */
    //标志：如果一个新元素要被添加到双端队列尾部(通过 addLast(E), add(E), or push(E))，那么这个新元素在双端队列的下标就是tail
    transient int tail;

    //最小容量，必须为2的幂次方
    private static final int MIN_INITIAL_CAPACITY = 8;

    // ******  Array allocation and resizing utilities ******

    private static int calculateSize(int numElements) {
        int initialCapacity = MIN_INITIAL_CAPACITY;
        // 找到大于指定容量的最小的2的n次幂
        // Find the best power of two to hold elements.
        // Tests "<=" because arrays aren't kept full.
        // 如果指定的容量小于初始容量8，则执行一下if中的逻辑操作

        //>>>是无符号右移操作，|是位或操作，经过五次右移和位或操作可以保证得到大小为2^k-1的数。看一下这个例子：
        //
        //0 0 0 0 1 ? ? ? ? ?     //n
        //0 0 0 0 1 1 ? ? ? ?     //n |= n >>> 1;
        //0 0 0 0 1 1 1 1 ? ?     //n |= n >>> 2;
        //0 0 0 0 1 1 1 1 1 1     //n |= n >>> 4;
        if (numElements >= initialCapacity) {
            initialCapacity = numElements;
            initialCapacity |= (initialCapacity >>>  1);
            initialCapacity |= (initialCapacity >>>  2);
            initialCapacity |= (initialCapacity >>>  4);
            initialCapacity |= (initialCapacity >>>  8);
            initialCapacity |= (initialCapacity >>> 16);
            initialCapacity++;

            if (initialCapacity < 0)   // Too many elements, must back off
                initialCapacity >>>= 1;// Good luck allocating 2 ^ 30 elements
        }
        return initialCapacity;
    }

    private void allocateElements(int numElements) {
        elements = new Object[calculateSize(numElements)];
    }

    /**
     * Doubles the capacity of this deque.  Call only when full, i.e.,
     * when head and tail have wrapped around to become equal.
     */
    /*

    这个方法中为什么System.arraycopy()方法为什么要调用两次呢，下面来分析一下。
    首先这个方法调用的地方有两个，addFirst()和addLast()

    第一种情况：只使用addFirst()方法添加元素，当我们添加完了第8个元素，此时elements=[7,6,5,4,3,2,1,0],需要进行扩容了

    此时
    head = tail = 0;
    p=0;
    n=8;
    r=8;
    首先把数组容量增加两倍，然后
    System.arraycopy(elements, p, x, 0, r);会把elements中的元素从下标p=0拷贝到a中，一共拷贝r=8个元素，a从下标为0开始放置元素

    //第二次拷贝因为p=0，所以并不会改变a。
    System.arraycopy(elements, 0, x, r, p);
    最后让elements指向a。拷贝完成后 elements=[7,6,5,4,3,2,1,0,null,null,null,null,null,null,null,null],head=0,tail=8;

    再用addFirst()方法添加一个元素8，结果elements=[7,6,5,4,3,2,1,0,null,null,null,null,null,null,null,8]

    再用addFirst()方法添加一个元素9，结果elements=[7,6,5,4,3,2,1,0,null,null,null,null,null,null,9,8]

    当我们继续添加元素elements=[7,6,5,4,3,2,1,0,15,14,13,12,11,10,9,8]，此时数组满了又需要扩容了
    此时
    head == tail=8;
    p=8;
    n=16;
    r=8;
    首先把数组容量增加两倍，然后
    System.arraycopy(elements, p, x, 0, r);会把elements中的元素从下标p=8拷贝到a中，一共拷贝r=8个元素，a从下标为0开始放置元素
    拷贝完成后a=[15,14,13,12,11,10,9,8,...]
    //第二次拷贝p=8，会把elements中的元素从下标0拷贝到a中，一共拷贝p=8个元素，a从下标r=8开始放置元素
    System.arraycopy(elements, 0, x, r, p);
    最后让elements指向a。拷贝完成后 x=[15,14,13,12,11,10,9,8,7,6,5,4,3,2,1,...],head=0,tail=16;

    插一句,到这里隐隐感觉到写这个代码的人真是厉害，佩服！

    第二种情况：只使用addLast()方法添加元素，当我们添加完了第8个元素，此时elements=[0,1,2,3,4,5,6,7],需要进行扩容了
    此时
    head = tail=0;
    p=0;
    n=8;
    r=8;
    首先把数组容量增加两倍，然后
    System.arraycopy(elements, p, x, 0, r);会把elements中的元素从下标p=0拷贝到a中，一共拷贝r=8个元素，a从下标为0开始放置元素
    //第二次拷贝因为p=0，所以并不会改变a。
    System.arraycopy(elements, 0, x, r, p);
    最后让elements指向a。拷贝完成后 elements=[0,1,2,3,4,5,6,7,null,null,null,null,null,null,null,null],head=0,tail=8;
    再用addLast()方法添加一个元素8，结果elements=[0,1,2,3,4,5,6,7,8,null,null,null,null,null,null,null]
    再用addLast()方法添加个一个元素9，结果elements=[0,1,2,3,4,5,6,7,8,9,null,null,null,null,null,null]

    当我们继续添加元素elements=[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]，此时数组满了又需要扩容了
    此时
    head = tail=0;
    p=0;
    n=16;
    r=16;
    首先把数组容量增加两倍，然后
    System.arraycopy(elements, p, x, 0, r);会把elements中的元素从下标p=0拷贝到a中，一共拷贝r=16个元素，a从下标为0开始放置元素
    //第二次拷贝因为p=0，所以并不会改变a。
    System.arraycopy(elements, 0, x, r, p);
    最后让elements指向a。拷贝完成后 elements=[0,1,2,3,4,5,6,7,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,...],head=0,tail=16;

    插一句,到这里深深感觉到写这个代码的人真是厉害，佩服！

    再分析一种情况
    使用addFirst()方法添加元素，当我们添加完了第7个元素，此时elements=[null,6,5,4,3,2,1,0],然后使用addLast()方法添加一个元素7，
    此时elements=[7,6,5,4,3,2,1,0]然后数组满了需要扩容
    此时
    head = tail=1;
    p=1;
    n=8;
    r=7;
    数组容量增加两倍
    System.arraycopy(elements, p, x, 0, r);会把elements中的元素从下标p=1拷贝到a中，一共拷贝r=7个元素，a从下标为0开始放置元素，拷贝
    完成后a=[6,5,4,3,2,1,0,...]
    第二次拷贝
    System.arraycopy(elements, 0, x, r, p);会把elements中的元素从下标0拷贝到a中，一共拷贝p=1个元素，a从下标为r=7开始放置元素，拷贝完成后
    x=[6,5,4,3,2,1,0,7，...]此时elements=[6,5,4,3,2,1,0,7，...],head = 0``tail=8
    如果此时继续用然后使用addLast()添加8个元素element=[6,5,4,3,2,1,0,7,8,9,10,11,12,13,14,15]，此时数组饱和，扩容
    head = tail=0;
    p=0;
    n=16;
    r=16;
    数组容量增加两倍
    System.arraycopy(elements, p, x, 0, r);会把elements中的元素从下标p=0拷贝到a中，一共拷贝r=16个元素，a从下标为0开始放置元素，拷贝
    完成后a=[6,5,4,3,2,1,0,7,8,9,10,11,12,13,14,15,...]
    第二次拷贝p=0,a不会改变。
    最后elements=[6,5,4,3,2,1,0,7,8,9,10,11,12,13,14,15,...],head = 0``tail=16

    * */
    private void doubleCapacity() {
        // 验证head和tail是否相等
        assert head == tail;
        int p = head;
        int n = elements.length; // 记录数组的长度
        // 计算head后面的元素个数，这里没有采用jdk中自带的英文注释right，是因为所谓队列的上下左右，只是我们看的方位不同而已，如果上面画的图，这里就应该是left而非right
        int r = n - p; // number of elements to the right of p
        int newCapacity = n << 1; // 将数组长度扩大2倍
        if (newCapacity < 0) // 如果此时长度小于0，则抛出IllegalStateException异常，什么时候newCapacity会小于0呢，前面我们说过了int值<<1越界
            throw new IllegalStateException("Sorry, deque too big");
        Object[] a = new Object[newCapacity]; // 创建一个长度是原数组大小2倍的新数组
        // 既然是head和tail已经重合了，说明tail是在head的左边。
        System.arraycopy(elements, p, a, 0, r); // 将原数组head后的元素都拷贝值新数组
        System.arraycopy(elements, 0, a, r, p); // 将原数组head前的元素都拷贝到新数组
        elements = a; // 将新数组赋值给elements
        head = 0; // 重置head为数组的第一个位置索引0
        tail = n; // 重置tail为数组的最后一个位置索引+1（(length - 1) + 1）
    }

    private <T> T[] copyElements(T[] a) {
        if (head < tail) {// 开始索引大于结束索引，一次拷贝
            System.arraycopy(elements, head, a, 0, size());
        } else if (head > tail) { // 开始索引在结束索引的右边，分两段拷贝
            int headPortionLen = elements.length - head;
            System.arraycopy(elements, head, a, 0, headPortionLen);
            System.arraycopy(elements, 0, a, headPortionLen, tail);
        }
        return a;
    }

    public ArrayDeque() {
        elements = new Object[16];
    }

    //指定初始容量下限，最终初始容量是大于等于numElements并且是2的次幂的一个数，比如指定numElements=9，那么初始容量最终会是16
    public ArrayDeque(int numElements) {
        allocateElements(numElements);
    }

    public ArrayDeque(Collection<? extends E> c) {
        allocateElements(c.size());
        addAll(c);
    }


    public void addFirst(E e) {
        if (e == null)
            throw new NullPointerException();
        //本来可以简单地写成head-1，但如果head为0，减1就变为-1了，和elements.length - 1进行与操作就是为了处理这种情况，这时结果为elements.length - 1。
        //添加第一个元素，表达式head = (head - 1) & (elements.length - 1)就等价于(-1&7)=7,所以elements[7]=0。
        //添加第二个元素，表达式head = (head - 1) & (elements.length - 1)就等价于(6&7)=6,所以elements[6]=1。
        elements[head = (head - 1) & (elements.length - 1)] = e;
        if (head == tail)
            doubleCapacity();
    }

    public void addLast(E e) {
        if (e == null)
            throw new NullPointerException();
        elements[tail] = e; // tail位置是空的，将元素e放到数组的tail位置
        // 判断tail和head是否相等，如果相等则对数组进行扩容
        // 和head的操作类似，为了处理临界情况 (tail为length - 1时)，和length - 1进行与操作，结果为0。
        //这条语句的判断条件还是比较难理解的，我们之前在构造elements元素的时候，说过它的长度一定是2的指数级，
        // 所以对于任意一个2的指数级的值减去1之后必然所有位全为1，例如：8-1之后为111，16-1之后1111。
        // 而对于tail来说，当tail+1小于等于elements.length - 1，两者与完之后的结果还是tail+1，但是如果tail+1大于elements.length - 1
        // ，两者与完之后就为0，回到初始位置。这种判断队列是否满的方式要远远比我们使用符号%直接取模高效，jdk优雅的设计从此可见一瞥。
        // 接着，如果队列满，那么会调用方法doubleCapacity扩充容量，
        //那么为什么(tail + 1) & (elements.length - 1)就能保证按照环形取得正确的下一个索引值呢？这就和前面说到的 ArrayDeque 对容量的特殊要求有关了。下面对其正确性加以验证：
        /*
        length = 2^n，二进制表示为: 第 n 位为1，低位 (n-1位) 全为0
        length - 1 = 2^n-1，二进制表示为：低位(n-1位)全为1

        如果 tail + 1 <= length - 1，则位与后低 (n-1) 位保持不变，高位全为0
        如果 tail + 1 = length，则位与后低 n 全为0，高位也全为0，结果为 0
        可见，在容量保证为 2^n 的情况下，仅仅通过位与操作就可以完成环形索引的计算，而不需要进行边界的判断，在实现上更为高效。

        * */
        if ( (tail = (tail + 1) & (elements.length - 1)) == head)//把数组当作环形的，越界后到0索引
            doubleCapacity(); // tail 和 head相遇，空间用尽，需要进行两倍扩容
    }

    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    public E removeFirst() {
        E x = pollFirst();
        if (x == null)
            throw new NoSuchElementException();
        return x;
    }

    public E removeLast() {
        E x = pollLast();
        if (x == null)
            throw new NoSuchElementException();
        return x;
    }

    public E pollFirst() {
        int h = head;
        @SuppressWarnings("unchecked")
        E result = (E) elements[h];
        // Element is null if deque empty
        if (result == null)//null值意味着deque为空
            return null;
        // 表明head位置已为空
        elements[h] = null;     // Must null out slot
        head = (h + 1) & (elements.length - 1);// 处理临界情况（当h为elements.length - 1时），与后的结果为0。
        return result;
    }

    public E pollLast() {
        int t = (tail - 1) & (elements.length - 1);// 处理临界情况（当tail为0时），与后的结果为elements.length - 1。
        @SuppressWarnings("unchecked")
        E result = (E) elements[t];
        if (result == null)//null值意味着deque为空
            return null;
        elements[t] = null;
        tail = t; // tail指向的是下个要添加元素的索引。
        return result;
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E getFirst() {
        @SuppressWarnings("unchecked")
        E result = (E) elements[head];
        if (result == null)
            throw new NoSuchElementException();
        return result;
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E getLast() {
        @SuppressWarnings("unchecked")
        E result = (E) elements[(tail - 1) & (elements.length - 1)];
        if (result == null)
            throw new NoSuchElementException();
        return result;
    }

    @SuppressWarnings("unchecked")
    public E peekFirst() {
        // elements[head] is null if deque empty
        return (E) elements[head];
    }

    @SuppressWarnings("unchecked")
    public E peekLast() {
        return (E) elements[(tail - 1) & (elements.length - 1)];
    }

    public boolean removeFirstOccurrence(Object o) {
        if (o == null)
            return false;
        int mask = elements.length - 1;
        int i = head;
        Object x;
        while ( (x = elements[i]) != null) {
            if (o.equals(x)) {
                delete(i);
                return true;
            }
            i = (i + 1) & mask; // 从头到尾遍历
        }
        return false;
    }

    public boolean removeLastOccurrence(Object o) {
        if (o == null)
            return false;
        int mask = elements.length - 1;
        int i = (tail - 1) & mask;// 末尾元素的索引
        Object x;
        while ( (x = elements[i]) != null) {
            if (o.equals(x)) {
                delete(i);
                return true;
            }
            i = (i - 1) & mask; // 从尾到头遍历
        }
        return false;
    }

    // *** Queue methods ***


    public boolean add(E e) {
        addLast(e);
        return true;
    }

    public boolean offer(E e) {
        return offerLast(e);
    }

    public E remove() {
        return removeFirst();
    }

    public E poll() {
        return pollFirst();
    }

    public E element() {
        return getFirst();
    }

    public E peek() {
        return peekFirst();
    }

    // *** Stack methods ***

    public void push(E e) {
        addFirst(e);
    }

    public E pop() {
        return removeFirst();
    }

    // 有效性检查
    private void checkInvariants() {
        assert elements[tail] == null;// tail位置没有元素
        assert head == tail ? elements[head] == null :
                (elements[head] != null &&
                        elements[(tail - 1) & (elements.length - 1)] != null);// 如果head和tail重叠，队列为空；否则head位置有元素，tail-1位置有元素。
        assert elements[(head - 1) & (elements.length - 1)] == null;// head-1的位置没有元素。
    }

    private boolean delete(int i) {
        checkInvariants();
        final Object[] elements = this.elements;
        final int mask = elements.length - 1;
        final int h = head;
        final int t = tail;
        final int front = (i - h) & mask;// i前面的元素个数
        final int back  = (t - i) & mask;// i后面的元素个数

        // Invariant: head <= i < tail mod circularity
        if (front >= ((t - h) & mask))
            throw new ConcurrentModificationException();

        // Optimize for least element motion
        if (front < back) { // i的位置靠近head，移动开始的元素，返回false。
            if (h <= i) {
                System.arraycopy(elements, h, elements, h + 1, front);
            } else { // Wrap around
                System.arraycopy(elements, 0, elements, 1, i);
                elements[0] = elements[mask];// 处理边缘元素
                System.arraycopy(elements, h, elements, h + 1, mask - h);
            }
            elements[h] = null;
            head = (h + 1) & mask; // head位置后移
            return false;
        } else { // i的位置靠近tail，移动末尾的元素，返回true。
            if (i < t) { // Copy the null tail as well
                System.arraycopy(elements, i + 1, elements, i, back);
                tail = t - 1;
            } else { // Wrap around
                System.arraycopy(elements, i + 1, elements, i, mask - i);
                elements[mask] = elements[0];
                System.arraycopy(elements, 1, elements, 0, t);
                tail = (t - 1) & mask;
            }
            return true;
        }
    }

    // *** Collection Methods ***

    public int size() {
        return (tail - head) & (elements.length - 1);
    }

    public boolean isEmpty() {
        return head == tail;
    }

    public Iterator<E> iterator() {
        return new DeqIterator();
    }

    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    /*
        ArrayDeque 在迭代时检查并发修改并没有使用类似于 ArrayList 等容器中使用的 modCount，而是通过尾部索引来确定的。
        具体参考 next 方法中的注释。但是这样不一定能保证检测到所有的并发修改情况，假如先移除了尾部元素，又添加了一个尾部元素，
        这种情况下迭代器是没法检测出来的。
    * */
    private class DeqIterator implements Iterator<E> {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        private int cursor = head;

        /**
         * Tail recorded at construction (also in remove), to stop
         * iterator and also to check for comodification.
         */
        private int fence = tail;

        /**
         * Index of element returned by most recent call to next.
         * Reset to -1 if element is deleted by x call to remove.
         */
        private int lastRet = -1;

        public boolean hasNext() {
            return cursor != fence;
        }

        public E next() {
            if (cursor == fence)
                throw new NoSuchElementException();
            @SuppressWarnings("unchecked")
            E result = (E) elements[cursor];
            // This check doesn't catch all possible comodifications,
            // but does catch the ones that corrupt traversal
            // 如果移除了尾部元素，会导致tail != fence
            // 如果移除了头部元素，会导致 result == null
            if (tail != fence || result == null)
                throw new ConcurrentModificationException();
            lastRet = cursor;
            cursor = (cursor + 1) & (elements.length - 1);
            return result;
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            if (delete(lastRet)) { // if left-shifted, undo increment in next()
                cursor = (cursor - 1) & (elements.length - 1);
                fence = tail;
            }
            lastRet = -1;
        }

        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            Object[] a = elements;
            int m = a.length - 1, f = fence, i = cursor;
            cursor = f;
            while (i != f) {
                @SuppressWarnings("unchecked") E e = (E)a[i];
                i = (i + 1) & m;
                if (e == null)
                    throw new ConcurrentModificationException();
                action.accept(e);
            }
        }
    }

    private class DescendingIterator implements Iterator<E> {
        /*
         * This class is nearly x mirror-image of DeqIterator, using
         * tail instead of head for initial cursor, and head instead of
         * tail for fence.
         */
        private int cursor = tail;
        private int fence = head;
        private int lastRet = -1;

        public boolean hasNext() {
            return cursor != fence;
        }

        public E next() {
            if (cursor == fence)
                throw new NoSuchElementException();
            cursor = (cursor - 1) & (elements.length - 1);
            @SuppressWarnings("unchecked")
            E result = (E) elements[cursor];
            if (head != fence || result == null)
                throw new ConcurrentModificationException();
            lastRet = cursor;
            return result;
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            if (!delete(lastRet)) {
                cursor = (cursor + 1) & (elements.length - 1);
                fence = head;
            }
            lastRet = -1;
        }
    }

    public boolean contains(Object o) {
        if (o == null)
            return false;
        int mask = elements.length - 1;
        int i = head;
        Object x;
        while ( (x = elements[i]) != null) {
            if (o.equals(x))
                return true;
            i = (i + 1) & mask;
        }
        return false;
    }

    public boolean remove(Object o) {
        return removeFirstOccurrence(o);
    }

    public void clear() {
        int h = head;
        int t = tail;
        if (h != t) { // clear all cells
            head = tail = 0;
            int i = h;
            int mask = elements.length - 1;
            do {
                elements[i] = null;
                i = (i + 1) & mask;
            } while (i != t);
        }
    }

    public Object[] toArray() {
        return copyElements(new Object[size()]);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);
        copyElements(a);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    // *** Object methods ***

    public ArrayDeque<E> clone() {
        try {
            @SuppressWarnings("unchecked")
            ArrayDeque<E> result = (ArrayDeque<E>) super.clone();
            result.elements = Arrays.copyOf(elements, elements.length);
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    private static final long serialVersionUID = 2340985798034038923L;

    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size());

        // Write out elements in order.
        int mask = elements.length - 1;
        for (int i = head; i != tail; i = (i + 1) & mask)
            s.writeObject(elements[i]);
    }

    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();

        // Read in size and allocate array
        int size = s.readInt();
        int capacity = calculateSize(size);
//        SharedSecrets.getJavaOISAccess().checkArray(s, Object[].class, capacity);
        allocateElements(size);
        head = 0;
        tail = size;

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
            elements[i] = s.readObject();
    }

}
