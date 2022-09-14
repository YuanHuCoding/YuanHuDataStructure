package me.yuanhu.core.DataStructure.tree2;

public class DataItem {
    public long dData; // 存储的数据类型，可以为其他复杂的对象或自定义对象

    public DataItem(long dd) {
        dData = dd;
    }

    public void displayItem() {
        System.out.print("/" + dData);
    }
}