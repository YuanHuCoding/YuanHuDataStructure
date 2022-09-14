package me.yuanhu.core.DataStructure.stack.Offer;

import me.yuanhu.core.DataStructure.stack.Stack_Seq;
import me.yuanhu.core.DataStructure.IStack;

/**
 * 一个栈元素的类型为整数，现在要想将该栈从顶到底按从大到小的顺序排列，只允许申请一个栈，除此之外，
 * 可以申请一个变量，可以申请额外的变量，但是不能申请额外的数据结构，如何完成排序

 * 思路：我们需要排序的栈为stack,然后我们申请一个栈记为help,在stack上面执行pop()操作，弹出的元素记为cur
 * 如果cur小于或者等于help的栈顶元素，则将cur压入help,
 * 如何cur大于help的栈顶元素，则将help的元素涿步弹出，涿一压入stack，直到cur小于或等于help的栈顶元素、再将
 * cur压入help
 */
public class SortStackByStack {
    /**
     * 用一个栈实现另一个栈的排序
     * @param stack
     */
    public static void sortStackByStack(IStack<Integer> stack){
        IStack<Integer> help = new Stack_Seq<>();//帮助栈
        while(!stack.isEmpty()){
            int cur = stack.pop();
            //在帮助栈不为空并且弹出的元素小于帮助栈栈顶的元素就将帮助栈栈顶的元素压会元素数据栈
            while(!help.isEmpty() && cur > help.peek()){
                stack.push(help.pop());
            }
            //将弹出的元素放到帮助栈中
            help.push(cur);
        }
        //现在的元素全部在帮助栈中从栈顶到栈底从大到小排列
        System.out.println(help);
        //此时数据栈为空，辅助栈是逆序的，将辅助栈依次弹出，压入到数据栈中
        while(!help.isEmpty()){
            stack.push(help.pop());
        }
    }

    public static void main(String[] args) {
        IStack<Integer> s=new Stack_Seq<>();
        s.push(3);
        s.push(2);
        s.push(5);
        s.push(4);
        s.push(1);
        sortStackByStack(s);
        while(!s.isEmpty()){
            System.out.println(s.pop());
        }
    }
}
