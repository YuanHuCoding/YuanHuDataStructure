package me.yuanhu.core.DataStructure.stack.Offer;

import me.yuanhu.core.DataStructure.stack.Stack_Seq;
import me.yuanhu.core.DataStructure.IStack;

/**
 * 一个栈一次压入了1、2、3、4、5，那么从栈顶到栈底分别为5、4、3、2、1.将这个栈转置后，
 * 从栈顶到栈底为1、2、3、4、5，
 * 也就是实现栈中元素的逆序，但是只能用递归函数来实现，不能用其他数据结构。
 *
 */
public class ReverseStackUsingRecursive {

    public static void reverse(IStack<Integer> stack) {
        if (stack.isEmpty()) {
            return;
        }
        int i = getAndRemoveLastElement(stack);
        reverse(stack);
        stack.push(i);
    }

    /**
     * 这个函数就是删除栈底元素并返回这个元素
     * @param stack
     * @return
     */
    public static int getAndRemoveLastElement(IStack<Integer> stack) {
        int result = stack.pop();
        if (stack.isEmpty()) {
            return result;//如果是栈底元素，直接返回
        } else {
            int last = getAndRemoveLastElement(stack);
            stack.push(result);//如果不是栈底元素，重新入栈
            return last;
        }
    }

    public static void main(String[] args) {
        IStack<Integer> test = new Stack_Seq<>();
        test.push(1);
        test.push(2);
        test.push(3);
        test.push(4);
        test.push(5);
        reverse(test);
        while (!test.isEmpty()) {
            System.out.println(test.pop());
        }

    }

}
