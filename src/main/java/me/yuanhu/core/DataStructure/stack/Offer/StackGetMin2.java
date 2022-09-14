package me.yuanhu.core.DataStructure.stack.Offer;

import me.yuanhu.core.DataStructure.stack.Stack_Seq;
import me.yuanhu.core.DataStructure.IStack;

///////// 方案2//////
//在push操作的时候，如果stackData压入的数据大于等于stackMin的顶部元素的时候，StackMin重复压入顶部元素，这样，pop的时候，每次MinStack都弹出就可以了。
// 压入稍费空间，弹出稍省时间
public class StackGetMin2 {
    private IStack<Integer> stackData;

    private IStack<Integer> stackMin;

    /**
     * 构造函数.
     */
    public StackGetMin2() {
        this.stackData = new Stack_Seq<Integer>();
        this.stackMin = new Stack_Seq<Integer>();
    }

    public void push(int item) { // 压入稍费空间
        this.stackData.push(item);
        if (this.stackMin.isEmpty()) {
            this.stackMin.push(item);
        } else if (item < this.getMin()) {
            this.stackMin.push(item);
        } else {
            int newMin = this.stackMin.peek();
            this.stackMin.push(newMin);
        }
    }

    public int pop() { // 弹出稍省时间
        if (this.stackData.isEmpty()) { // 判空
            throw new RuntimeException("Your Stack is empty.");
        }
        this.stackMin.pop();
        return this.stackMin.pop();
    }

    public int getMin() {
        if (this.stackMin.isEmpty()) {
            throw new RuntimeException("Your Stack is empty.");
        }
        return this.stackMin.peek();
    }

    //////////// 测试类/////////////////

    public static void main(String[] args) {
        StackGetMin1 myStack = new StackGetMin1();
        myStack.push(2);
        myStack.push(5);
        myStack.push(4);
        myStack.push(1);
        myStack.push(1);
//        System.out.println(myStack.pop());
        System.out.println(myStack.getMin());
        System.out.println(myStack.pop());
        System.out.println(myStack.getMin());
    }
}
