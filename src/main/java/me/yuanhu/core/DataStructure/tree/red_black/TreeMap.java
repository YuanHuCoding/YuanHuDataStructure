package me.yuanhu.core.DataStructure.tree.red_black;


import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Random;

public class TreeMap<K,V> {

    //比较器，因为TreeMap是有序的，通过comparator接口我们可以对TreeMap的内部排序进行精密的控制
    private final Comparator<? super K> comparator;

    //TreeMap红-黑节点，为TreeMap的内部类
    private transient Entry<K,V> root;

    //容器大小
    private transient int size = 0;

    //TreeMap修改次数
    private transient int modCount = 0;

    public TreeMap() {
        comparator = null;
    }

    public TreeMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    //=======================================================================
    // Query Operations

    public Comparator<? super K> comparator() {
        return comparator;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void clear() {
        modCount++;
        size = 0;
        root = null;
    }

    public boolean containsKey(Object key) {
        return getEntry(key) != null;
    }

    public boolean containsValue(Object value) {
        for (Entry<K,V> e = getFirstEntry(); e != null; e = successor(e))
            if (valEquals(value, e.value))
                return true;
        return false;
    }

    public K firstKey() {
        return key(getFirstEntry());
    }

    public K lastKey() {
        return key(getLastEntry());
    }

    public V get(Object key) {
        Entry<K,V> p = getEntry(key);
        return (p==null ? null : p.value);
    }

    final Entry<K,V> getEntry(Object key) {
        // Offload comparator-based version for sake of performance
        if (comparator != null)
            return getEntryUsingComparator(key);
        if (key == null)
            throw new NullPointerException();
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key;
        Entry<K,V> p = root;
        while (p != null) {
            int cmp = k.compareTo(p.key);
            if (cmp < 0)
                p = p.left;
            else if (cmp > 0)
                p = p.right;
            else
                return p;
        }
        return null;
    }

    final Entry<K,V> getEntryUsingComparator(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) key;
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            Entry<K,V> p = root;
            while (p != null) {
                int cmp = cpr.compare(k, p.key);
                if (cmp < 0)
                    p = p.left;
                else if (cmp > 0)
                    p = p.right;
                else
                    return p;
            }
        }
        return null;
    }

    public V put(K key, V value) {
        //用t表示二叉树的当前节点
        Entry<K,V> t = root;
        //t为null表示一个空树，即TreeMap中没有任何元素，直接插入
        if (t == null) {
            //比较key值，个人觉得这句代码没有任何意义，空树还需要比较、排序？
            compare(key, key); // type (and possibly null) check

            //将新的key-value键值对创建为一个Entry节点，并将该节点赋予给root
            root = new Entry<>(key, value, null);
            //容器的size = 1，表示TreeMap集合中存在一 个元素
            size = 1;
            modCount++;
            return null;
        }
        int cmp; //cmp表示key排序的返回结果
        Entry<K,V> parent;//父节点
        // split comparator and comparable paths
        Comparator<? super K> cpr = comparator; //指定的排序算法
        //如果cpr不为空，则采用既定的排序算法进行创建TreeMap集合
        if (cpr != null) {
            do {
                parent = t; //parent指向上次循环后的t
                //比较新增节点的key和当前节点key的大小
                cmp = cpr.compare(key, t.key);
                //cmp返回值小于0，表示新增节点的key小于当前节点的key，则以当前节点的左子节点作为新的当前节点
                if (cmp < 0)
                    t = t.left;
                    //cmp返回值大于0，表示新增节点的key大于当前节点的key，则以当前节点的右子节点作为新的当前节点
                else if (cmp > 0)
                    t = t.right;
                    //cmp返回值等于0，表示两个key值相等，则新值覆盖旧值，并返回新值
                else
                    return t.setValue(value);
            } while (t != null);
        }
        //如果cpr为空，则采用默认的排序算法进行创建 TreeMap集合
        else {
            if (key == null)//key值为空抛出异常
                throw new NullPointerException();
            @SuppressWarnings("unchecked")
            Comparable<? super K> k = (Comparable<? super K>) key;
            // 上面代码中 do{} 代码块是实现排序二叉树的核心算法，通过该算法我们可以确认新增节点在该树的正确位置。
            // 找到正确位置后将插入即可，这样做了其实还没有完成，因为我知道 TreeMap 的底层实现是红黑树，红黑树是一棵平衡排序二叉树，
            // 普通的排序二叉树可能会出现失衡的情况，所以下一步就是要进行调整。fixAfterInsertion(e); 调整的过程务必会涉及到红黑树的左旋、右旋、着色三个基本操作。
            do {
                parent = t;
                /* 下面处理过程和上面一样 */
                cmp = k.compareTo(t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        //将新增节点当做parent的子节点
        Entry<K,V> e = new Entry<>(key, value, parent);
        //如果新增节点的key小于parent的key，则当做左子节点
        if (cmp < 0)
            parent.left = e;
            //如果新增节点的key大于parent的key，则当做右子节点
        else
            parent.right = e;
        /*
         *  上面已经完成了排序二叉树的的构建，将新增节点  插入该树中的合适位置
         *  下面fixAfterInsertion()方法就是对这棵树进行调整、平衡，具体过程参考上面的五种情况
         */
        fixAfterInsertion(e);
        //TreeMap元素数量 + 1
        size++;
        modCount++;
        return null;
    }

    public V remove(Object key) {
        Entry<K,V> p = getEntry(key);
        if (p == null)
            return null;

        V oldValue = p.value;
        deleteEntry(p);
        return oldValue;
    }

    //=======================================================================
    // Red-black mechanics

    //红黑树的节点颜色--红色
    private static final boolean RED   = false;
    //红黑树的节点颜色--黑色
    private static final boolean BLACK = true;

    /**
     * Node in the DataStructure_Tree.  Doubles as x means to pass key-value pairs back to
     * user (see Map.Entry).
     */

    static final class Entry<K,V>{
        K key;
        V value;
        Entry<K,V> left;
        Entry<K,V> right;
        Entry<K,V> parent;
        boolean color = BLACK;

        /**
         * Make x new cell with given key, value, and parent, and with
         * {@code null} child links, and BLACK color.
         */
        Entry(K key, V value, Entry<K,V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }

        /**
         * Returns the key.
         *
         * @return the key
         */
        public K getKey() {
            return key;
        }

        /**
         * Returns the value associated with the key.
         *
         * @return the value associated with the key
         */
        public V getValue() {
            return value;
        }

        /**
         * Replaces the value currently associated with the key with the given
         * value.
         *
         * @return the value associated with the key before this method was
         *         called
         */
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Entry))
                return false;
            Entry<?,?> e = (Entry<?,?>)o;

            return valEquals(key,e.getKey()) && valEquals(value,e.getValue());
        }

        public int hashCode() {
            int keyHash = (key==null ? 0 : key.hashCode());
            int valueHash = (value==null ? 0 : value.hashCode());
            return keyHash ^ valueHash;
        }

        public String toString() {
            return key + "=" + value;
        }
    }

    /**
     * Returns the first Entry in the TreeMap (according to the TreeMap's
     * key-sort function).  Returns null if the TreeMap is empty.
     */
    final Entry<K,V> getFirstEntry() {
        Entry<K,V> p = root;
        if (p != null)
            while (p.left != null)
                p = p.left;
        return p;
    }

    /**
     * Returns the last Entry in the TreeMap (according to the TreeMap's
     * key-sort function).  Returns null if the TreeMap is empty.
     */
    final Entry<K,V> getLastEntry() {
        Entry<K,V> p = root;
        if (p != null)
            while (p.right != null)
                p = p.right;
        return p;
    }

    /**后继
     * Returns the successor of the specified Entry, or null if no such.
     */
    static <K,V> Entry<K,V> successor(Entry<K,V> t) {
        if (t == null)
            return null;
            /*
             * 寻找右子树的最左子树
             */
        else if (t.right != null) {
            Entry<K,V> p = t.right;
            while (p.left != null)
                p = p.left;
            return p;
        }
        /*
         * 选择左子树的最右子树
         */
        else {
            Entry<K,V> p = t.parent;
            Entry<K,V> ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    /**
     * 前任
     * Returns the predecessor of the specified Entry, or null if no such.
     */
    static <K,V> Entry<K,V> predecessor(Entry<K,V> t) {
        if (t == null)
            return null;
        else if (t.left != null) {
            Entry<K,V> p = t.left;
            while (p.right != null)
                p = p.right;
            return p;
        } else {
            Entry<K,V> p = t.parent;
            Entry<K,V> ch = t;
            while (p != null && ch == p.left) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    //=======================================================================
    /**
     * Balancing operations.
     *
     * Implementations of rebalancings during insertion and deletion are
     * slightly different than the CLR version.  Rather than using dummy
     * nilnodes, we use x set of accessors that deal properly with null.  They
     * are used to avoid messiness surrounding nullness checks in the main
     * algorithms.
     */

    private static <K,V> boolean colorOf(Entry<K,V> p) {
        return (p == null ? BLACK : p.color);
    }

    private static <K,V> Entry<K,V> parentOf(Entry<K,V> p) {
        return (p == null ? null: p.parent);
    }

    private static <K,V> void setColor(Entry<K,V> p, boolean c) {
        if (p != null)
            p.color = c;
    }

    private static <K,V> Entry<K,V> leftOf(Entry<K,V> p) {
        return (p == null) ? null: p.left;
    }

    private static <K,V> Entry<K,V> rightOf(Entry<K,V> p) {
        return (p == null) ? null: p.right;
    }

    /**
     * 所谓左旋转，就是将新增节点（N）当做其父节点（P），将其父节点P当做新增节点（N）的左子节点。即：G.left —> N ,N.left —> P。
     * */
    private void rotateLeft(Entry<K,V> p) {
        if (p != null) {
            //获取P的右子节点，其实这里就相当于新增节点N（情况四而言）
            Entry<K,V> r = p.right;
            //将R的左子树设置为P的右子树
            p.right = r.left;
            //若R的左子树不为空，则将P设置为R左子树的父亲
            if (r.left != null)
                r.left.parent = p;
            //将P的父亲设置R的父亲
            r.parent = p.parent;
            //如果P的父亲为空，则将R设置为跟节点
            if (p.parent == null)
                root = r;
                //如果P为其父节点（G）的左子树，则将R设置为P父 节点(G)左子树
            else if (p.parent.left == p)
                p.parent.left = r;
                //否则R设置为P的父节点（G）的右子树
            else
                p.parent.right = r;
            //将P设置为R的左子树
            r.left = p;
            //将R设置为P的父节点
            p.parent = r;
        }
    }

    /** From CLR */
    private void rotateRight(Entry<K,V> p) {
        if (p != null) {
            //将L设置为P的左子树
            Entry<K,V> l = p.left;
            //将L的右子树设置为P的左子树
            p.left = l.right;
            //若L的右子树不为空，则将P设置L的右子树的父节点
            if (l.right != null) l.right.parent = p;
            //将P的父节点设置为L的父节点
            l.parent = p.parent;
            //如果P的父节点为空，则将L设置根节点
            if (p.parent == null)
                root = l;
                //若P为其父节点的右子树，则将L设置为P的父节点的右子树
            else if (p.parent.right == p)
                p.parent.right = l;
                //否则将L设置为P的父节点的左子树
            else p.parent.left = l;
            //将P设置为L的右子树
            l.right = p;
            //将L设置为P的父节点
            p.parent = l;
        }
    }

    /*
        新增节点后的修复操作
        x 表示新增节点
    */
    private void fixAfterInsertion(Entry<K,V> x) {
        x.color = RED;//新增节点的颜色为红色

        //循环 直到 x不是根节点，且x的父节点不为红色
        while (x != null && x != root && x.parent.color == RED) {
            //如果X的父节点（P）是其父节点的父节点（G）的左节点
            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
                //获取X的叔节点(U)
                Entry<K,V> y = rightOf(parentOf(parentOf(x)));
                //如果X的叔节点（U） 为红色（情况三）
                if (colorOf(y) == RED) {
                    //将X的父节点（P）设置为黑色
                    setColor(parentOf(x), BLACK);
                    //将X的叔节点（U）设置为黑色
                    setColor(y, BLACK);
                    //将X的父节点的父节点（G）设置红色
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                }
                //如果X的叔节点（U为黑色）；这里会存在  两种情况（情况四、情况五）
                else {
                    //如果X节点为其父节点（P）的右子 树，则进行左旋转（情况四）
                    if (x == rightOf(parentOf(x))) {
                        //将X的父节点作为X
                        x = parentOf(x);
                        //左旋转
                        rotateLeft(x);
                    }
                    //（情况五）
                    //将X的父节点（P）设置为黑色
                    setColor(parentOf(x), BLACK);
                    //将X的父节点的父节点（G）设置红色
                    setColor(parentOf(parentOf(x)), RED);
                    //以X的父节点的父节点（G）为中心右旋转
                    rotateRight(parentOf(parentOf(x)));
                }
            }
            //如果X的父节点（P）是其父节点的父节点（G）的右节点
            else {
                //获取X的叔节点（U）
                Entry<K,V> y = leftOf(parentOf(parentOf(x)));
                //如果X的叔节点（U） 为红色（情况三）
                if (colorOf(y) == RED) {
                    //将X的父节点（P）设置为黑色
                    setColor(parentOf(x), BLACK);
                    //将X的叔节点（U）设置为黑色
                    setColor(y, BLACK);
                    //将X的父节点的父节点（G）设置红色
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                }
                //如果X的叔节点（U为黑色）；这里会存在两种情况（情况四、情况五）
                else {
                    //如果X节点为其父节点（P）的右子树，则进行左旋转（情况四）
                    if (x == leftOf(parentOf(x))) {
                        //将X的父节点作为X
                        x = parentOf(x);
                        //右旋转
                        rotateRight(x);
                    }
                    //（情况五）
                    //将X的父节点（P）设置为黑色
                    setColor(parentOf(x), BLACK);
                    //将X的父节点的父节点（G）设置红色
                    setColor(parentOf(parentOf(x)), RED);
                    //以X的父节点的父节点（G）为中心右旋转
                    rotateLeft(parentOf(parentOf(x)));
                }
            }
        }
        //将根节点G强制设置为黑色
        root.color = BLACK;
    }

    /**
     * Delete node p, and then rebalance the tree.
     */
    private void deleteEntry(Entry<K,V> p) {
        modCount++;
        size--;

        // If strictly internal, copy successor's element to p and then make p
        // point to successor.
        /*
         * 被删除节点的左子树和右子树都不为空，那么就用 p节点的中序后继节点代替 p 节点
         * successor(P)方法为寻找P的替代节点。规则是右分支 最左边，或者 左分支最右边的节点
         * ---------------------（1）
         */
        if (p.left != null && p.right != null) {
            Entry<K,V> s = successor(p);
            p.key = s.key;
            p.value = s.value;
            p = s;
        } // p has 2 children

        // Start fixup at replacement node, if it exists.
        //replacement为替代节点，如果P的左子树存在那么就用左子树替代，否则用右子树替代
        Entry<K,V> replacement = (p.left != null ? p.left : p.right);

        /*
         * 删除节点，分为上面提到的三种情况
         * -----------------------（2）
         */
        //如果替代节点不为空
        if (replacement != null) {
            // Link replacement to parent
            replacement.parent = p.parent;
            /*
             *replacement来替代P节点
             */
            //若P没有父节点，则跟节点直接变成replacement
            if (p.parent == null)
                root = replacement;
                //如果P为左节点，则用replacement来替代为左节点
            else if (p == p.parent.left)
                p.parent.left  = replacement;
                //如果P为右节点，则用replacement来替代为右节点
            else
                p.parent.right = replacement;

            // Null out links so they are OK to use by fixAfterDeletion.
            //同时将P节点从这棵树中剔除掉
            p.left = p.right = p.parent = null;

            // Fix replacement
            /*
             * 若P为红色直接删除，红黑树保持平衡
             * 但是若P为黑色，则需要调整红黑树使其保持平衡
             */
            if (p.color == BLACK)
                fixAfterDeletion(replacement);
        } else if (p.parent == null) { // return if we are the only node. //p没有父节点，表示为P根节点，直接删除即可
            root = null;
        } else { //  No children. Use self as phantom replacement and unlink. //P节点不存在子节点，直接删除即可
            if (p.color == BLACK)//如果P节点的颜色为黑色，对红黑树进行调整
                fixAfterDeletion(p);

            //删除P节点
            if (p.parent != null) {
                if (p == p.parent.left)
                    p.parent.left = null;
                else if (p == p.parent.right)
                    p.parent.right = null;
                p.parent = null;
            }
        }
    }

    /** From CLR */
    private void fixAfterDeletion(Entry<K,V> x) {
        // 删除节点需要一直迭代，知道 直到 x 不是根节点，且   x 的颜色是黑色
        while (x != root && colorOf(x) == BLACK) {
            if (x == leftOf(parentOf(x))) { //若X 节点为左节点
                //获取其兄弟节点
                Entry<K,V> sib = rightOf(parentOf(x));

                /*
                 * 如果兄弟节点为红色----（情况3.1）
                 * 策略：改变W、P的颜色，然后进行一次左旋转
                 */
                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateLeft(parentOf(x));
                    sib = rightOf(parentOf(x));
                }

                /*
                 * 若兄弟节点的两个子节点都为黑色----（情况3.2）
                 * 策略：将兄弟节点编程红色
                 */
                if (colorOf(leftOf(sib))  == BLACK &&
                        colorOf(rightOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    /*
                     * 如果兄弟节点只有右子树为黑色----  （情况3.3）
                     * 策略：将兄弟节点与其左子树进行颜色互换然后进行右转
                     * 这时情况会转变为3.4
                     */
                    if (colorOf(rightOf(sib)) == BLACK) {
                        setColor(leftOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateRight(sib);
                        sib = rightOf(parentOf(x));
                    }
                    /*
                     *----情况3.4
                     *策略：交换兄弟节点和父节点的颜色，
                     *同时将兄弟节点右子树设置为黑色，最后左旋转
                     */
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(rightOf(sib), BLACK);
                    rotateLeft(parentOf(x));
                    x = root;
                }
            }
            /**
             * X节点为右节点与其为做节点处理过程差不多，这里 就不在累述了
             */
            else { // symmetric
                Entry<K,V> sib = leftOf(parentOf(x));

                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateRight(parentOf(x));
                    sib = leftOf(parentOf(x));
                }

                if (colorOf(rightOf(sib)) == BLACK &&
                        colorOf(leftOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(leftOf(sib)) == BLACK) {
                        setColor(rightOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateLeft(sib);
                        sib = leftOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(leftOf(sib), BLACK);
                    rotateRight(parentOf(x));
                    x = root;
                }
            }
        }

        setColor(x, BLACK);
    }


    //=======================================================================
    // Little utilities

    @SuppressWarnings("unchecked")
    final int compare(Object k1, Object k2) {
        return comparator==null ? ((Comparable<? super K>)k1).compareTo((K)k2)
                : comparator.compare((K)k1, (K)k2);
    }


    /**
     * Return key for entry, or null if null
     */
    static <K,V> K keyOrNull(Entry<K,V> e) {
        return (e == null) ? null : e.key;
    }

    /**
     * Returns the key corresponding to the specified Entry.
     * @throws NoSuchElementException if the Entry is null
     */
    static <K> K key(Entry<K,?> e) {
        if (e==null)
            throw new NoSuchElementException();
        return e.key;
    }

    static final boolean valEquals(Object o1, Object o2) {
        return (o1==null ? o2==null : o1.equals(o2));
    }


    public static void main(String[] agrs){
        // 初始化随机种子
        Random r = new Random();
        // 新建TreeMap
        TreeMap tmap = new TreeMap();
        // 添加操作
        tmap.put("one", r.nextInt(10));
        tmap.put("two", r.nextInt(10));
        tmap.put("three", r.nextInt(10));

        System.out.printf("\n ---- testTreeMapOridinaryAPIs ----\n");
        // 打印出TreeMap
        System.out.printf("%s\n",tmap );

//        // 通过Iterator遍历key-value
//        Iterator iter = tmap.entrySet().iterator();
//        while(iter.hasNext()) {
//            Map.Entry entry = (Map.Entry)iter.next();
//            System.out.printf("next : %s - %s\n", entry.getKey(), entry.getValue());
//        }

        // TreeMap的键值对个数
        System.out.printf("size: %s\n", tmap.size());

        // containsKey(Object key) :是否包含键key
        System.out.printf("contains key two : %s\n",tmap.containsKey("two"));
        System.out.printf("contains key five : %s\n",tmap.containsKey("five"));

        // containsValue(Object value) :是否包含值value
        System.out.printf("contains value 0 : %s\n",tmap.containsValue(new Integer(0)));

        // remove(Object key) ： 删除键key对应的键值对
        tmap.remove("three");

        System.out.printf("tmap:%s\n",tmap );

        // clear() ： 清空TreeMap
        tmap.clear();

        // isEmpty() : TreeMap是否为空
        System.out.printf("%s\n", (tmap.isEmpty()?"tmap is empty":"tmap is not empty") );
    }
}
