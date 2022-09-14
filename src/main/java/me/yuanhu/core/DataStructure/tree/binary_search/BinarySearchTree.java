package me.yuanhu.core.DataStructure.tree.binary_search;

import me.yuanhu.core.DataStructure.IBinaryTree;
import me.yuanhu.core.DataStructure.IQueue;
import me.yuanhu.core.DataStructure.queue.Queue_Linked;
import me.yuanhu.core.DataStructure.IStack;
import me.yuanhu.core.DataStructure.stack.Stack_Linked;

import java.util.*;


/**
 * 二叉搜索树（非递归版本）
 * https://www.jianshu.com/p/4ef1f50d45b5
 */
public class BinarySearchTree<E extends Comparable<E>> implements IBinaryTree<E> {


    // 记录搜索深度
    public int count;
    private int size;
    private BinaryTreeNode<E> root;


    public BinarySearchTree() {
        root = null;
        size = 0;
    }

    public BinarySearchTree(E[] datas) {
        this();
        for (E item : datas) {
            this.add(item);
        }
    }

    @Override
    public BinaryTreeNode<E> getRoot() {
        return root;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public void add(E e) {
        // 建立树根
        if (root == null) {
            root = new BinaryTreeNode<E>(e);
            size++;
            return;
        }

        BinaryTreeNode<E> current = root;
        // 建立二叉树
        while (true) {
            // 新增的value比节点的value小，则在左子树
            if (e.compareTo(current.data()) < 0) {
                if (current.left() == null) {
                    current.setLeft(new BinaryTreeNode<E>(e));
                    size++;
                    return;
                } else {
                    current = current.left();
                }
            } else { // 新增的value比节点的value大，在右子树
                if (current.right() == null) {
                    current.setRight(new BinaryTreeNode<E>(e));
                    size++;
                    return;
                } else {
                    current = current.right();
                }
            }
        }
    }

    @Override
    public BinaryTreeNode<E> findNode(E e) {
        count = 0;
        return findNode(root, e);
    }

    // 二叉查找树的搜索
    private BinaryTreeNode<E> findNode(BinaryTreeNode<E> node, E e) {
        BinaryTreeNode<E> current = node;
        while (current != null) {
            if (e.compareTo(current.data()) == 0) {
                System.out.println("共搜索" + count + "次");
                return current;
            } else if (e.compareTo(current.data()) < 0) {
                count++;
                current = current.left();
            } else {
                count++;
                current = current.right();
            }
        }
        return null;
    }

    @Override
    public boolean contains(E e) {
        return findNode(e) != null;
    }

    // 二叉搜索树删除节点
    /*
    要在二叉查找树中删除一个元素，首先需要定位包含该元素的节点，以及它的父节点。假设current指向二叉查找树中包含该元素的节点，
    而parent指向current节点的父节点，current节点可能是parent节点的左孩子，也可能是右孩子。这里需要考虑两种情况：
    1.current节点没有左孩子，那么只需要将patent节点和current节点的右孩子相连。
    2.current节点有一个左孩子，假设rightMost指向包含current节点的左子树中最大元素的节点，而parentOfRightMost指向rightMost节点的父节点。
    那么先使用rightMost节点中的元素值替换current节点中的元素值，将parentOfRightMost节点和rightMost节点的左孩子相连，然后删除rightMost节点。
    * */
    @Override
    public void remove(E e) {
        Boolean isRemoved = false;
        BinaryTreeNode<E> parent = null;
        BinaryTreeNode<E> current = root;

        // 找到要删除的节点的位置
        while (current != null) {
            if (e.compareTo(current.data()) < 0) {
                parent = current;
                current = current.left();
            } else if (e.compareTo(current.data()) > 0) {
                parent = current;
                current = current.right();
            } else {
                break;
            }
        }

        // 没找到要删除的节点
        if (current == null) {
            isRemoved = false;
            return;
        }

        // 考虑第一种情况
        if (current.left() == null) {
            if (parent == null) {
                root = current.right();
            } else {
                if (e.compareTo(parent.data()) < 0) {
                    parent.setLeft(current.right());
                } else {
                    parent.setRight(current.right());
                }
            }
        } else { // 考虑第二种情况
            BinaryTreeNode<E> parentOfRightMost = current;
            BinaryTreeNode<E> rightMost = current.left();
            // 找到左子树中最大的元素节点
            while (rightMost.right() != null) {
                parentOfRightMost = rightMost;
                rightMost = rightMost.right();
            }

            // 替换
            current.setData(rightMost.data());

            // parentOfRightMost和rightMost左孩子相连
            if (parentOfRightMost.right() == rightMost) {
                parentOfRightMost.setRight(rightMost.left());
            } else {
                parentOfRightMost.setLeft(rightMost.left());
            }
        }
        isRemoved = true;
    }

    // 寻找二分搜索树的最小元素
    public E findMin() {
        if (size == 0)
            throw new IllegalArgumentException("BinarySearchTree is empty!");

        return findMin(root).data();
    }

    // 返回以node为根的二分搜索树的最小值所在的节点
    private BinaryTreeNode<E> findMin(BinaryTreeNode<E> node) {
        while (node.left() != null) {
            node = node.left();
        }
        return node;
    }

    // 寻找二分搜索树的最大元素
    public E findMax() {
        if (size == 0)
            throw new IllegalArgumentException("BinarySearchTree is empty");

        return findMax(root).data();
    }

    // 返回以node为根的二分搜索树的最大值所在的节点
    private BinaryTreeNode<E> findMax(BinaryTreeNode node) {
        while (node.right() != null) {
            node = node.right();
        }
        return node;
    }

    // 从二分搜索树中删除最小值所在节点, 返回最小值
    public E removeMin() {
        BinaryTreeNode<E> parent = null;
        BinaryTreeNode<E> current = root;
        while (current.left() != null) {
            parent = current;
            current = current.left();
        }

        if (parent == null) {
            root = current.right();
        } else {
            parent.setLeft(current.right());
        }
        current.setRight(null);

        return current.data();
    }

    // 从二分搜索树中删除最大值所在节点
    public E removeMax() {
        BinaryTreeNode<E> parent = null;
        BinaryTreeNode<E> current = root;
        while (current.right() != null) {
            parent = current;
            current = current.right();
        }

        if (parent == null) {
            root = current.left();
        } else {
            parent.setRight(current.left());
        }
        current.setLeft(null);

        return current.data();
    }


    class OrderNodeVisited {
        BinaryTreeNode<E> node;
        Boolean visited;

        public OrderNodeVisited(BinaryTreeNode<E> node, Boolean visited) {
            this.node = node;
            this.visited = visited;
        }
    }


    // 前序遍历(根-左-右)
    @Override
    public void preOrder() {
        IStack<OrderNodeVisited> s = new Stack_Linked<>();
        s.push(new OrderNodeVisited(root, false));

        BinaryTreeNode<E> cur = null;
        Boolean visited;
        while (!s.isEmpty()) {
            cur = s.peek().node;
            visited = s.peek().visited;
            s.pop();
            if (cur == null)
                continue;
//            if (visited) {
//                System.out.print("[" + cur.data() + "]");
//            } else {
//                s.push(new KeyValuePaired(cur.right(), false));
//                s.push(new KeyValuePaired(cur.left(), false));
//                s.push(new KeyValuePaired(cur, true));
//            }
            //优化的写法
            //值得一提的是，对于前序遍历，大家可能发现取出一个栈顶元素，使其局部前序入栈后，栈顶元素依然是此元素，
            // 接着就要出栈输出了，所以使其随局部入栈是没有必要的，其代码就可以简化为下面的形式。
            System.out.print("[" + cur.data() + "]");
            s.push(new OrderNodeVisited(cur.right(), false));
            s.push(new OrderNodeVisited(cur.left(), false));
        }
    }


    // 中序遍历(左-根-右)
    @Override
    public void inOrder() {
        IStack<OrderNodeVisited> s = new Stack_Linked<>();
        s.push(new OrderNodeVisited(root, false));

        BinaryTreeNode<E> cur = null;
        Boolean visited;
        while (!s.isEmpty()) {
            cur = s.peek().node;
            visited = s.peek().visited;
            s.pop();
            if (cur == null)
                continue;
            if (visited) {
                System.out.print("[" + cur.data() + "]");
            } else {
                s.push(new OrderNodeVisited(cur.right(), false));
                s.push(new OrderNodeVisited(cur, true));
                s.push(new OrderNodeVisited(cur.left(), false));
            }
        }
    }

    //后序遍历(左-右-根)
    @Override
    public void postOrder() {
        IStack<OrderNodeVisited> s = new Stack_Linked<>();
        s.push(new OrderNodeVisited(root, false));

        BinaryTreeNode<E> cur = null;
        Boolean visited;
        while (!s.isEmpty()) {
            cur = s.peek().node;
            visited = s.peek().visited;
            s.pop();
            if (cur == null)
                continue;
            if (visited) {
                System.out.print("[" + cur.data() + "]");
            } else {
                s.push(new OrderNodeVisited(cur, true));
                s.push(new OrderNodeVisited(cur.right(), false));
                s.push(new OrderNodeVisited(cur.left(), false));
            }
        }
    }


    // 前序遍历(根-左-右)
    public void preOrder2() {
        IStack<BinaryTreeNode<E>> stack = new Stack_Linked<>();
        stack.push(getRoot());
        while (!stack.isEmpty()) {
            BinaryTreeNode<E> cur = stack.pop();
            System.out.print("[" + cur.data() + "]");

            if (cur.right() != null)
                stack.push(cur.right());
            if (cur.left() != null)
                stack.push(cur.left());
        }
    }

    //后序遍历(左-右-根)

    /**
     * https://blog.csdn.net/zlp1992/article/details/51406067
     * Discuss中有人给出了一个”巧“的方法，即先采用类似先序遍历，先遍历根结点再右孩子最后左孩子（先序是先根结
     * 点再左孩子最后右孩子），最后把遍历的序列逆转即得到了后序遍历。
     * https://leetcode.com/discuss/101547/java-recursive-and-iterative-solutions
     */
    public void postOrder2() {
        IStack<BinaryTreeNode<E>> stack = new Stack_Linked<>();
        stack.push(root);
        List<E> ret = new ArrayList<>();
        while (!stack.isEmpty()) {
            BinaryTreeNode<E> node = stack.pop();
            if (node != null) {
                ret.add(node.data());
                stack.push(node.left());
                stack.push(node.right());
            }
        }
        Collections.reverse(ret);
        for (E e : ret) {
            System.out.print("[" + e + "]");
        }
    }


    //非递归实现
    //https://blog.csdn.net/snow_7/article/details/51818580
    //利用层次遍历的算法，设置变量level记录当前节点所在的层数，设置变量last指向当前层的最后一个节点，当处理完当前层的最后一个节点，
    // 让level指向+1操作。设置变量cur记录当前层已经访问的节点的个数，当cur等于last时，表示该层访问结束。
    @Override
    public int height() {
        if (root == null)
            return 0;

        BinaryTreeNode<E> current = null;
        IQueue<BinaryTreeNode<E>> queue = new Queue_Linked<>();
        queue.offer(root);
        int cur, last;
        int level = 0;
        while (!queue.isEmpty()) {
            cur = 0;//记录本层已经遍历的节点个数
            last = queue.size();//当遍历完当前层以后，队列里元素全是下一层的元素，队列的长度是这一层的节点的个数
            while (cur < last)//当还没有遍历到本层最后一个节点时循环
            {
                current = queue.poll();//出队一个元素
                cur++;
                //把当前节点的左右节点入队（如果存在的话）
                if (current.left() != null) {
                    queue.offer(current.left());
                }
                if (current.right() != null) {
                    queue.offer(current.right());
                }
            }
            level++;//每遍历完一层level+1
        }
        return level;
    }

    @Override
    public String toString() {
       return IBinaryTree.generateBSTString(this);
    }

    public static void main(String[] args) {
        Integer[] content = {15, 6, 18, 4, 13, 17, 20, 9};
//        Integer[] content = {15,6,18,4,14,13,17,20,9};
//        Integer[] content = {150,60,180,40,140,130,170,200,90,135,145};

        BinarySearchTree<Integer> tree = new BinarySearchTree(content);
        tree.remove(15);
//        tree.removeMin();
//        tree.removeMax();

        System.out.println("前序遍历:");
        tree.preOrder();
        System.out.println("\n中序遍历:");
        tree.inOrder();
        System.out.println("\n后序遍历:");
        tree.postOrder();
        System.out.println("\n层序遍历:");
        tree.levelOrder();

        System.out.println("\n高度" + ":" + tree.height());

        System.out.println("\n开始搜索:");
        boolean isFind = tree.contains(9);
        System.out.println("是否搜索到" + 9 + ":" + isFind);
    }
}
