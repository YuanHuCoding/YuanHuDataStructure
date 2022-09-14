package me.yuanhu.core.DataStructure.stack.Offer;

import me.yuanhu.core.DataStructure.stack.Stack_Seq;
import me.yuanhu.core.DataStructure.IStack;

/**
 *
 * 设计一个有getMin功能的栈：StackGetMin1
 *
 * 【实现一个特殊的栈，在实现栈的基本功能的基础上，再实现返回栈中最小元素的操作】
 *
 * 要求：1、pop、push、getMin操作的时间复杂度都是O(1);2、设计的栈类型可以使用现成的栈结构。
 *
 * 设计思路：两个栈-普通栈+getMin栈
 *
 */
public class StackGetMin1 {
    private IStack<Integer> stackData;

    private IStack<Integer> stackMin;

    /**
     * 构造函数.
     */
    public StackGetMin1() {
        this.stackData = new Stack_Seq<Integer>();
        this.stackMin = new Stack_Seq<Integer>();
    }

    public void push(int item) { // 压入稍省空间
        this.stackData.push(item);
        if (this.stackMin.isEmpty()) {
            this.stackMin.push(item);
        } else if (item <= this.getMin()) { // 小于等于:避免插入多个最小值再弹出后，栈中无最小值
            this.stackMin.push(item);
        }
    }

    public int pop() { // 弹出稍费时间
        if (this.stackData.isEmpty()) { // 判空
            throw new RuntimeException("Your Stack is empty.");
        }
        int value = this.stackData.pop();
        if (value == this.getMin()) { // 两个栈同步弹出
            this.stackMin.pop();
        }
        return value;
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
        System.out.println(myStack.getMin());
        System.out.println(myStack.pop());
        System.out.println(myStack.getMin());
    }
}
