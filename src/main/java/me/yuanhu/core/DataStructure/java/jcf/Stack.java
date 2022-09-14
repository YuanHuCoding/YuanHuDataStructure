package me.yuanhu.core.DataStructure.java.jcf;

import java.util.EmptyStackException;

/*
   Stack 是栈，它继承于Vector。它的特性是：先进后出(FILO, First In Last Out)。
* 《Java编程思想》第四版一书中明确不建议我们使用java.util.Stack类，一直保留只是为了兼容以前的版本，在17.13.3中提到了原因。主要是因为：
* Stack类是继承自Vector类，而不是使用Vector来实现Stack，这就产生了一个问题，Vector上可以使用的方法Stack类都可以使用，所以很容易破坏栈应有的规则。
* */
public class Stack<E> extends Vector<E> {

    public Stack() {
    }

    public E push(E item) {
        addElement(item);

        return item;
    }

    public synchronized E pop() {
        E obj;
        int len = size();

        obj = peek();
        removeElementAt(len - 1);

        return obj;
    }

    public synchronized E peek() {
        int len = size();

        if (len == 0)
            throw new EmptyStackException();
        return elementAt(len - 1);
    }

    public boolean empty() {
        return size() == 0;
    }

    public synchronized int search(Object o) {
        int i = lastIndexOf(o);

        if (i >= 0) {
            return size() - i;
        }
        return -1;
    }

    /**
     * use serialVersionUID from JDK 1.0.2 for interoperability
     */
    private static final long serialVersionUID = 1224463164541339165L;
}
