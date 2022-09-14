package me.yuanhu.core.DataStructure;

//栈接口抽象数据类型
//https://blog.csdn.net/javazejian/article/details/53362993
public interface IStack<E> {

    /**
     * 返回栈内元素数量
     * @return
     */
    int size();

    /**
     * 栈是否为空
     * @return
     */
    boolean isEmpty();

    /**
     * 元素入栈
     */
    void push(E item);

    /**
     * 出栈,返回栈顶元素,同时从栈中移除该元素
     * @return
     */
    E pop();

    /**
     * 返回栈顶元素,未出栈
     * @return
     */
    E peek();



    class StackEmptyException extends RuntimeException {
        private static final long serialVersionUID = 0L;

        public StackEmptyException() {
        }
    }
}

