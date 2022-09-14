package me.yuanhu.core.DataStructure.tree2;

public class Tree234 {
    private Node root = new Node();            // 创建树的根

    //-------------------------------------------------------------
    //获取查找的下一个节点
    public Node getNextChild(Node theNode, long theValue) {
        int j;
    // 假设这个节点不是叶结点
        int numItems = theNode.getNumItems();//获得当前节点的数据项数目
        for (j = 0; j < numItems; j++) {
            if (theValue < theNode.getItem(j).dData)
                return theNode.getChild(j);  // 返回相应的节点
        }  // end for
        return theNode.getChild(j);        // 此时j=numItems
    }

    //-------------------------------------------------------------
    public int find(long key) {
        Node curNode = root;
        int childNumber;
        while (true) {
            if ((childNumber = curNode.findItem(key)) != -1)//每次循环这句一定执行
                return childNumber;               // found it
            else if (curNode.isLeaf())//叶结点上也没找到
                return -1;                        // can't find it
            else                                 // 不是叶结点，则继续向下查找
                curNode = getNextChild(curNode, key);
        }  // end while
    }

    //-------------------------------------------------------------
    // 插入数据项
    public void insert(long dValue) {
        Node curNode = root;//当前节点标志
        DataItem tempItem = new DataItem(dValue);//插入数据项封装

        while (true) {
            if (curNode.isFull())               // 是满节点
            {
                split(curNode);                   // 分裂
                curNode = curNode.getParent();    // 回到分裂出的父节点上
                // 继续向下查找
                curNode = getNextChild(curNode, dValue);
            }  // end if(node is full)
            //后面的操作中节点都未满，否则先执行上面的代码
            else if (curNode.isLeaf())          // 是叶结点，非满
                break;                            // 跳出，直接插入

            else
                curNode = getNextChild(curNode, dValue);//向下查找
        }  // end while

        curNode.insertItem(tempItem);       // 此时节点一定不满，直接插入数据项，
    }  // end insert()

    //-------------------------------------------------------------
    public void split(Node thisNode)     // 分裂
    {
        // 操作中节点一定是满节点，否则不会执行该操作
        DataItem itemB, itemC;
        Node parent, child2, child3;
        int itemIndex;

        itemC = thisNode.removeItem();    // 移除最右边的两个数据项，并保存为B和C
        itemB = thisNode.removeItem();    //
        child2 = thisNode.disconnectChild(2); // //断开最右边两个子节点的链接
        child3 = thisNode.disconnectChild(3); //

        Node newRight = new Node();       //新建一个节点，作为当前节点的兄弟节点

        if (thisNode == root)                // 是根
        {
            root = new Node();                // 新建一个根
            parent = root;                    // 把新根设为父节点
            root.connectChild(0, thisNode);   // 连接父节点和子节点
        } else                              // 不是根
            parent = thisNode.getParent();    // 获取父节点


        itemIndex = parent.insertItem(itemB); // 把B插入父节点中，返回插入位置
        int n = parent.getNumItems();         // 获得总数据项数目

        for (int j = n - 1; j > itemIndex; j--)          //从后向前移除
        {
            Node temp = parent.disconnectChild(j); // 断开连接
            parent.connectChild(j + 1, temp);        // 连接到新的位置
        }

        parent.connectChild(itemIndex + 1, newRight);//连接到新位置

        // 处理兄弟节点
        newRight.insertItem(itemC);       // 将C放入兄弟节点中
        newRight.connectChild(0, child2); // 把子节点中最右边的两个连接到兄弟节点上
        newRight.connectChild(1, child3); //
    }  // end split()
    //-------------------------------------------------------------
    // gets appropriate child of node during search for value

    public void displayTree() {
        recDisplayTree(root, 0, 0);
    }

    //-------------------------------------------------------------
    private void recDisplayTree(Node thisNode, int level,
                                int childNumber) {
        System.out.print("level=" + level + " child=" + childNumber + " ");
        thisNode.displayNode();               // display this node

        // call ourselves for each child of this node
        int numItems = thisNode.getNumItems();
        for (int j = 0; j < numItems + 1; j++) {
            Node nextNode = thisNode.getChild(j);
            if (nextNode != null)
                recDisplayTree(nextNode, level + 1, j);
            else
                return;
        }
    }  // end recDisplayTree()
}