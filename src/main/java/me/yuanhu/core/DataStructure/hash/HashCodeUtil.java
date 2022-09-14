package me.yuanhu.core.DataStructure.hash;

import java.util.concurrent.atomic.AtomicInteger;

public class HashCodeUtil {

    /**
     * The next hash code to be given out. Updated atomically. Starts at
     * zero.
     */
    private static AtomicInteger nextHashCode = new AtomicInteger();

    /**
     * The difference between successively generated hash codes - turns
     * implicit sequential thread-local IDs into near-optimally spread
     * multiplicative hash values for power-of-two-sized tables.
     */
    /*
       生成hash code间隙的这个魔数，可以让生成出来的值或者说ThreadLocal的ID较为均匀地分布在2的幂大小的数组中。
       1640531527 这是一个神奇的数字，能够让hash槽位分布相当均匀。
     */
    private static final int HASH_INCREMENT = 0x61c88647;

    //ThreadLocal的hash算法
    public static int nextHashCode() {
        return nextHashCode.getAndAdd(HASH_INCREMENT);
    }

    //JDK8中的hash算法
    public static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        // x. 当key = null时，hash值 = 0，所以HashMap的key 可为null
        // 注：对比HashTable，HashTable对key直接hashCode（），若key为null时，会抛出异常，所以HashTable的key不可为null
        // y. 当key ≠ null时，则通过先计算出 key的 hashCode()（记为h），然后 对哈希码进行 扰动处理： 按位 异或（^） 哈希码自身右移16位后的二进制
    }

    //JDK7中的hash算法
    public static int hash(int h) {
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).

        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    //获取槽位（要求槽位数量是2的幂次方）
    public static int getSlot(int hashCode,int len) {
        // 对于2的幂作为模数取模，可以用&(2^n-1)来替代%2^n，位运算比取模效率高很多。
        // 至于为什么，因为对2^n取模，只要不是低n位对结果的贡献显然都是0，会影响结果的只能是低n位。
        return hashCode & (len - 1);
    }

    //获取槽位（Hashtable计算下标的方法）
    public static int getSlot2(int hashCode,int len) {
        return (hashCode & 0x7FFFFFFF) % len;
    }


    //这里模仿的ThreadLocal的hash算法，可以看出hash分布非常均匀，槽位分配非常完美
    public static void main(String[] args) {
        printAllSlot(8);
        printAllSlot(16);
        printAllSlot(32);
    }

    static void printAllSlot(int len) {
        System.out.println("********** len = " + len + " ************");
        for (int i = 1; i <= 64; i++) {
            int slot = getSlot(nextHashCode(), len);
            System.out.print(slot + " ");
            if (i % len == 0)
                System.out.println(); // 分组换行
        }
    }


}
