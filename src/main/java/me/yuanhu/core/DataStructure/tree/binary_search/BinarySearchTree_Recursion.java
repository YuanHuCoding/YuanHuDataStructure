package me.yuanhu.core.DataStructure.tree.binary_search;

import me.yuanhu.core.DataStructure.IBinaryTree;

/**
 *  二叉搜索树（递归版本）
 */
public class BinarySearchTree_Recursion<E extends Comparable<E>> implements IBinaryTree<E> {

    // 记录搜索深度
    public int count;

    private BinaryTreeNode<E> root;
    private int size;

    public BinarySearchTree_Recursion() {
        root = null;
        size = 0;
    }

    public BinarySearchTree_Recursion(E[] datas) {
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

    // 向二分搜索树中添加新的元素e(不得添加相同大小的元素)
    @Override
    public void add(E e) {
        root = add(root, e);
    }

    // 向以node为根的二分搜索树中插入元素e，递归算法
    // 返回插入新节点后二分搜索树的根
    private BinaryTreeNode<E> add(BinaryTreeNode<E> node, E e) {
        if (node == null) {
            size++;
            return new BinaryTreeNode(e);
        }

        if (e.compareTo(node.data()) < 0)
            node.setLeft(add(node.left(), e));
        else if (e.compareTo(node.data()) > 0)
            node.setRight(add(node.right(), e));

        return node;
    }

    // 从二分搜索树中删除元素为e的节点
    @Override
    public void remove(E e) {
        root = remove(root, e);
    }

    // 删除掉以node为根的二分搜索树中值为e的节点, 递归算法
    // 返回删除节点后新的二分搜索树的根
    private BinaryTreeNode<E> remove(BinaryTreeNode<E> node, E e) {
        if (node == null)
            return null;

        if (e.compareTo(node.data()) < 0) {
            node.setLeft(remove(node.left(), e));
            return node;
        } else if (e.compareTo(node.data()) > 0) {
            node.setRight(remove(node.right(), e));
            return node;
        } else {   // e.compareTo(node.e) == 0
            // 待删除节点左子树为空的情况
            if (node.left() == null) {
                BinaryTreeNode rightNode = node.right();
                node.setRight(null);
                size--;
                return rightNode;
            }

            // 待删除节点右子树为空的情况
            if (node.right() == null) {
                BinaryTreeNode leftNode = node.left();
                node.setLeft(null);
                size--;
                return leftNode;
            }

            // 待删除节点左右子树均不为空的情况

//            // 方式一：找到比待删除节点小的最大节点, 即待删除节点左子树的最大节点，用这个节点顶替待删除节点的位置
            BinaryTreeNode successor = findMax(node.left());
            successor.setLeft(removeMax(node.left()));
            successor.setRight(node.right());

            // 方式二：找到比待删除节点大的最小节点, 即待删除节点右子树的最小节点，用这个节点顶替待删除节点的位置
//            BinaryTreeNode<E> successor = findMin(node.right());
//            successor.setRight(removeMin(node.right()));
//            successor.setLeft(node.left());


            node.setLeft(null);
            node.setRight(null);
            return successor;
        }
    }

    @Override
    public boolean contains(E e) {
        return findNode(e) != null;
    }

    @Override
    public BinaryTreeNode<E> findNode(E e) {
        count = 0;
        return findNode(root, e);
    }

    private BinaryTreeNode<E> findNode(BinaryTreeNode<E> node, E e) {
        if (node == null) {
            System.out.println("共搜索" + count + "次");
            return null;
        }

        if (e.compareTo(node.data()) == 0) {
            System.out.println("共搜索" + count + "次");
            return node;
        } else if (e.compareTo(node.data()) < 0) {
            count++;
            return findNode(node.left(), e);
        } else {
            count++;
            return findNode(node.right(), e);
        }
    }

    // 寻找二分搜索树的最小元素

    public E findMin() {
        if (size == 0)
            throw new IllegalArgumentException("BinarySearchTree is empty!");

        return findMin(root).data();
    }

    // 返回以node为根的二分搜索树的最小值所在的节点
    private BinaryTreeNode<E> findMin(BinaryTreeNode<E> node) {
        if (node.left() == null)
            return node;
        return findMin(node.left());
    }

    // 寻找二分搜索树的最大元素

    public E findMax() {
        if (size == 0)
            throw new IllegalArgumentException("BinarySearchTree is empty");

        return findMax(root).data();
    }


    // 返回以node为根的二分搜索树的最大值所在的节点
    private BinaryTreeNode<E> findMax(BinaryTreeNode node) {
        if (node.right() == null)
            return node;

        return findMax(node.right());
    }

    // 从二分搜索树中删除最小值所在节点, 返回最小值
    public E removeMin() {
        E ret = findMin();
        root = removeMin(root);
        return ret;
    }

    // 删除掉以node为根的二分搜索树中的最小节点
    // 返回删除节点后新的二分搜索树的根
    private BinaryTreeNode<E> removeMin(BinaryTreeNode<E> node) {
        if (node.left() == null) {
            BinaryTreeNode<E> rightNode = node.right();
            node.setRight(null);
            size--;
            return rightNode;
        }

        node.setLeft(removeMin(node.left()));
        return node;
    }

    // 从二分搜索树中删除最大值所在节点

    public E removeMax() {
        E ret = findMax();
        root = removeMax(root);
        return ret;
    }

    // 删除掉以node为根的二分搜索树中的最大节点
    // 返回删除节点后新的二分搜索树的根
    private BinaryTreeNode<E> removeMax(BinaryTreeNode<E> node) {
        if (node.right() == null) {
            BinaryTreeNode<E> leftNode = node.left();
            node.setLeft(null);
            size--;
            return leftNode;
        }

        node.setRight(removeMax(node.right()));
        return node;
    }

    // 前序遍历(根-左-右)
    @Override
    public void preOrder() {
        preOrder(getRoot());
    }


    protected void preOrder(BinaryTreeNode<E> node) {
        if (node == null)
            return;

        System.out.print("[" + node.data() + "]");
        preOrder(node.left());
        preOrder(node.right());
    }


    // 中序遍历(左-根-右)
    @Override
    public void inOrder() {
        inOrder(getRoot());
    }

    private void inOrder(BinaryTreeNode node) {
        if (node == null)
            return;

        inOrder(node.left());
        System.out.print("[" + node.data() + "]");
        inOrder(node.right());
    }


    // 后序遍历(左-右-根)
    @Override
    public void postOrder() {
        postOrder(getRoot());
    }

    private void postOrder(BinaryTreeNode<E> node) {
        if (node == null)
            return;

        postOrder(node.left());
        postOrder(node.right());
        System.out.print("[" + node.data() + "]");
    }

    @Override
    public int height() {
        return height(getRoot());
    }

    private int height(BinaryTreeNode<E> node) {
        if (node == null) {
            return 0;
        }
        int leftHeight = height(node.left());
        int rightHeight = height(node.right());
        int max = leftHeight > rightHeight ? leftHeight : rightHeight;
        // 得到左右子树中较大的返回.
        return max + 1;
    }

    @Override
    public String toString() {
        return IBinaryTree.generateBSTString(this);
    }

    public static void main(String[] args) {
        Integer[] content = {15, 6, 18, 4, 13, 17, 20, 9};
        BinarySearchTree_Recursion<Integer> tree = new BinarySearchTree_Recursion(content);
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
        boolean isFind = tree.contains(90);
        System.out.println("是否搜索到" + 90 + ":" + isFind);

    }
}
