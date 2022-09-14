package me.yuanhu.core.DataStructure;


import me.yuanhu.core.DataStructure.queue.Queue_Linked;

public interface IBinaryTree<E extends Comparable<E>> {

    //获取根节点
    BinaryTreeNode<E> getRoot();

    /**
     * 二叉树的结点个数
     * @return
     */
    int size();

    /**
     * 判空
     * @return
     */
    boolean isEmpty();

    /**
     * 清空
     */
    void clear();

    /**
     * 将data 插入
     * @return
     */
    void add(E data);

    /**
     * 是否包含某个值
     * @param data
     * @return
     */
    boolean contains(E data) throws Exception;

    /**
     * 删除
     */
    void remove(E data);

    // 查找包含元素e的节点
    BinaryTreeNode<E> findNode(E e);

    /**
     * 返回二叉树的高度或者深度,即结点的最大层次
     * @return
     */
    int height();

    /**
     * 前序遍历(树根 -左子树- 右子树)
     */
    void preOrder();

    /**
     * 中序遍历(左子树 -树根- 右子树)
     */
    void inOrder();

    /**
     * 后序遍历(左子树 -右子树- 树根)
     */
    void postOrder();

    /**
     * 层次遍历
     */
    default void levelOrder() {
        IQueue<BinaryTreeNode<E>> queue = new Queue_Linked<>();
        queue.add(getRoot());
        while (!queue.isEmpty()) {
            BinaryTreeNode<E> cur = queue.remove();
            System.out.print("[" + cur.data() + "]");
            if (cur.left() != null)
                queue.add(cur.left());
            if (cur.right() != null)
                queue.add(cur.right());
        }
    }


    // 生成以node为根节点，深度为depth的描述二叉树的字符串
    static String generateBSTString(IBinaryTree tree) {
        StringBuilder res = new StringBuilder();
        generateBSTString(tree.getRoot(), 0, res);
        return res.toString();
    }

    // 生成以node为根节点，深度为depth的描述二叉树的字符串
    static void generateBSTString(BinaryTreeNode node, int depth, StringBuilder res) {
        if (node == null) {
            res.append(generateDepthString(depth) + "null\n");
            return;
        }

        res.append(generateDepthString(depth) + node.data() + "\n");
        generateBSTString(node.left(), depth + 1, res);
        generateBSTString(node.right(), depth + 1, res);
    }

    static String generateDepthString(int depth) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < depth; i++)
            res.append("--");
        return res.toString();
    }


    //二叉搜索树节点类
    class BinaryTreeNode<E extends Comparable<E>> {

        private E data;
        private BinaryTreeNode<E> left;
        private BinaryTreeNode<E> right;

        public BinaryTreeNode(E data) {
            this.data=data;
        }

        public void setData(E data) {
            this.data = data;
        }

        public void setLeft(BinaryTreeNode<E> node) {
            this.left=node;
        }

        public BinaryTreeNode<E> left() {
            return this.left;
        }

        public void setRight(BinaryTreeNode<E> node) {
            this.right=node;
        }

        public BinaryTreeNode<E> right() {
            return this.right;
        }

        public E data() {
            return this.data;
        }


        @Override
        public String toString() {
            return "["+(left()==null?"null":left().data()) +"]"+"["+data +"]"+"["+(right()==null?"null":right().data()) +"]";
        }
    }

}