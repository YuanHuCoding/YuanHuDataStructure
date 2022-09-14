package me.yuanhu.core.DataStructure.stack.Example;

import me.yuanhu.core.DataStructure.IStack;
import me.yuanhu.core.DataStructure.stack.Stack_Linked;

/**
 * 数制转换
 * https://blog.csdn.net/gavin_john/article/details/71374487
 */
public class Conversion {
    public static void conversion(int N){
        IStack<Integer> stack = new Stack_Linked<>();
        while( N != 0){
            stack.push(N % 8);
            N /= 8;
        }
        while(!stack.isEmpty()){
            System.out.print(stack.pop());
        }
    }

    public static void main(String[] args) {
        conversion(2007);
    }
}