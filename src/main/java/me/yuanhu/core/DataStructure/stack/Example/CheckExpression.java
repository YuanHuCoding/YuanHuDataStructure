package me.yuanhu.core.DataStructure.stack.Example;

import me.yuanhu.core.DataStructure.stack.Stack_Linked;

/**
 * 括号匹配检验
 */
public class CheckExpression {

    public static String isValid(String expstr) {
        //创建栈
        Stack_Linked<String> stack = new Stack_Linked<>();

        int i = 0;
        while (i < expstr.length()) {
            char ch = expstr.charAt(i);
            i++;
            switch (ch) {
                case '(': {
                    stack.push(ch + "");//左括号直接入栈
                    break;
                }
                case ')': {
                    if (stack.isEmpty() || !stack.pop().equals("(")) {//遇见右括号左括号直接出栈
                        break;
                    }
                }
            }
        }
        //最后检测是否为空,为空则检测通过
        if (stack.isEmpty())
            return "check pass!";
        else
            return "check exception!";
    }

    public static void main(String args[]) {
        String expstr = "((5-3)*8-2)";
        System.out.println(expstr + "  " + isValid(expstr));
        expstr = "((5-3*8-2)";
        System.out.println(expstr + "  " + isValid(expstr));
    }
}