package me.yuanhu.core.DataStructure.skiplist;

import java.util.Random;

/**
 * 不固定层级的跳跃表
 * created by 曹艳丰，2016-08-14
 * 参考：http://www.acmerblog.com/skip-list-impl-java-5773.html
 * */
public class SkipList<E>{
    private SkipListNode<E> head,tail;
    private int size;//节点总数
    private int levels;//层数
    private Random random;// 用于投掷硬币
    private static final double PROBABILITY=0.5;//向上提升一个的概率
    public SkipList() {
        // TODO Auto-generated constructor stub
        random = new Random();
        clear();
    }
    /**
     *清空跳跃表
     * */
    public void clear() {
        head = new SkipListNode<E>(SkipListNode.HEAD_KEY, null);
        tail = new SkipListNode<E>(SkipListNode.TAIL_KEY, null);
        horizontalLink(head, tail);
        levels = 0;
        size = 0;
    }
    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
    /**
     * 在最下面一层，找到要插入的位置前面的那个key
     * */
    private SkipListNode<E> findNode(int key) {
        SkipListNode<E> p = head;
        while (true) {
            while (p.right.key != SkipListNode.TAIL_KEY && p.right.key <= key) {
                p = p.right;
            }
            if (p.down != null) {
                p = p.down;
            } else {
                break;
            }

        }
        return p;
    }
    /**
     * 查找是否存储key，存在则返回该节点，否则返回null
     * */
    public SkipListNode<E> search(int key) {
        SkipListNode<E> p = findNode(key);
        if (key == p.getKey()) {
            return p;
        } else {
            return null;
        }
    }
    /**
     * 向跳跃表中添加key-value
     *
     * */
    public void put(int k, E v) {
        SkipListNode<E> p = findNode(k);
        //如果key值相同，替换原来的vaule即可结束
        if (k == p.getKey()) {
            p.value = v;
            return;
        }
        SkipListNode<E> q = new SkipListNode<E>(k, v);
        backLink(p, q);
        int currentLevel = 0;//当前所在的层级是0
        //抛硬币
        while (random.nextDouble() < PROBABILITY) {
            //如果超出了高度，需要重新建一个顶层
            if (currentLevel >= levels) {
                levels++;
                SkipListNode<E> p1 = new SkipListNode<E>(SkipListNode.HEAD_KEY, null);
                SkipListNode<E> p2 = new SkipListNode<E>(SkipListNode.TAIL_KEY, null);
                horizontalLink(p1, p2);
                vertiacallLink(p1, head);
                vertiacallLink(p2, tail);
                head = p1;
                tail = p2;
            }
            //将p移动到上一层
            while (p.up == null) {
                p = p.left;
            }
            p = p.up;

            SkipListNode<E> e = new SkipListNode<E>(k, null);//只保存key就ok
            backLink(p, e);//将e插入到p的后面
            vertiacallLink(e, q);//将e和q上下连接
            q = e;
            currentLevel++;
        }
        size++;//层数递增
    }
    //node1后面插入node2
    private void backLink(SkipListNode<E> node1, SkipListNode<E> node2) {
        node2.left = node1;
        node2.right = node1.right;
        node1.right.left = node2;
        node1.right = node2;
    }
    /**
     * 水平双向连接
     * */
    private void horizontalLink(SkipListNode<E> node1, SkipListNode<E> node2) {
        node1.right = node2;
        node2.left = node1;
    }
    /**
     * 垂直双向连接
     * */
    private void vertiacallLink(SkipListNode<E> node1, SkipListNode<E> node2) {
        node1.down = node2;
        node2.up = node1;
    }
    /**
     * 打印出原始数据
     * */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        if (isEmpty()) {
            return "跳跃表为空！";
        }
        StringBuilder builder = new StringBuilder();
        SkipListNode<E> p = head;
        while (p.down != null) {
            p = p.down;
        }

        while (p.left != null) {
            p = p.left;
        }
        if (p.right != null) {
            p = p.right;
        }
        while (p.right != null) {
            builder.append(p);
            builder.append("\n");
            p = p.right;
        }
        return builder.toString();
    }

    public static class SkipListNode <E> {
        public int key;
        public E value;
        public SkipListNode<E> up, down, left, right; // 上下左右 四个指针

        public static final int HEAD_KEY = Integer.MIN_VALUE; // 负无穷
        public static final int TAIL_KEY = Integer.MAX_VALUE; // 正无穷

        public SkipListNode(int k, E v) {
            // TODO Auto-generated constructor stub
            key = k;
            value = v;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public E getValue() {
            return value;
        }

        public void setValue(E value) {
            this.value = value;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (!(o instanceof SkipListNode)) {
                return false;
            }
            SkipListNode<E> ent;
            try {
                ent = (SkipListNode<E>) o; // 检测类型
            } catch (ClassCastException ex) {
                return false;
            }
            return (ent.getKey() == key) && (ent.getValue() == value);
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return "key-value:" + key + "-" + value;
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SkipList<String> list=new SkipList<String>();
        System.out.println(list);
        list.put(2, "yan");
        list.put(1, "co");
        list.put(3, "feng");
        list.put(1, "cao");//测试同一个key值
        list.put(4, "曹");
        list.put(6, "丰");
        list.put(5, "艳");
        System.out.println(list);
        System.out.println(list.size());
    }
}
