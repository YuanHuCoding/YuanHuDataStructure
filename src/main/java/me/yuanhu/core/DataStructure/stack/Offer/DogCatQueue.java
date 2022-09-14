package me.yuanhu.core.DataStructure.stack.Offer;

import me.yuanhu.core.DataStructure.IQueue;
import me.yuanhu.core.DataStructure.queue.Queue_Seq_Cyclic;

/*
* 给出宠物（Pet），猫（Cat）和狗（Dog）的类，实现一种猫狗队列，要求如下：

用户可以调用add方法将cat类或dog类的实例放入队列中;
用户可以调用pollAll方法,将队列中所有的实例按照进队列的先后顺序依次弹出;
用户可以调用pollDog方法,将队列中的dog类的实例按照进队列的先后顺序依次弹出;
用户可以调用pollCat方法,将队列中的cat类的实例按照进队列的先后顺序依次弹出;
用户可以调用isEmpty方法,检查队列中是否还有dog或cat的实例;
用户可以调用isDogEmpty方法,检查队列中是否还有dog类的实例;
用户可以调用isCatEmpty方法,检查队列中是否还有cat类的实例.

本题解法不唯一,先举出几种常见错误:

用三个队列: cat / dog 和总队列.更新问题.
用哈系表: key表示一个cat实例或dog实例,value表示这个实例进队列的次序.错误在不支持一个实例多次进队列.
将原有cat / dog类改写,加入计数项.错误在不能修改用户的类结构.
本题实现将不同的实例盖上时间戳的方法,但是不能改变用户本身的类,所以定义一个新的类,具体实现请看下面的PetWithTimestamp类:
* */
public class DogCatQueue {
    private IQueue<PetWithTimestamp> dogQ;
    private IQueue<PetWithTimestamp> catQ;
    private long count;

    public DogCatQueue() {
        this.dogQ = new Queue_Seq_Cyclic<>();
        this.catQ = new Queue_Seq_Cyclic<PetWithTimestamp>();
    }

    public void add(Pet pet) {
        if (pet.getType().equals("dog")) {
            PetWithTimestamp petEnter = new PetWithTimestamp(pet, this.count++);
            dogQ.add(petEnter);
        } else if (pet.getType().equals("cat")) {
            PetWithTimestamp petEnter = new PetWithTimestamp(pet, this.count++);
            catQ.add(petEnter);
        } else {
            throw new RuntimeException("类型不匹配");
        }
    }

    public Pet pollAll() {
        if (!this.isEmpty()) {
            if (this.dogQ.peek().getCount() < this.catQ.peek().getCount()) {
                return this.dogQ.poll().getPet();
            } else {
                return this.catQ.poll().getPet();
            }
        } else if (this.dogQ.isEmpty()) {
            return this.catQ.poll().getPet();
        } else if (this.catQ.isEmpty()) {
            return this.dogQ.poll().getPet();
        } else {
            throw new RuntimeException("queue is empty");
        }
    }

    public Pet pollDog() {
        if (!this.isDogEmpty()) {
            return this.dogQ.poll().getPet();
        } else {
            throw new RuntimeException("Dog queue is empty");
        }
    }

    public Pet pollCat() {
        if (!this.isCatEmpty()) {
            return this.catQ.poll().getPet();
        } else {
            throw new RuntimeException("cat queue is empty");
        }
    }

    public boolean isEmpty() {
        return this.dogQ.isEmpty() && this.catQ.isEmpty();
    }

    public boolean isDogEmpty() {
        return this.dogQ.isEmpty();
    }

    public boolean isCatEmpty() {
        return this.catQ.isEmpty();
    }

}

class Pet {
    private String type;

    public String getType() {
        return type;
    }

    public Pet(String type) {
        this.type = type;
    }

}

class Cat extends Pet {
    public Cat() {
        super("Cat");
    }
}

class Dog extends Pet {
    public Dog() {
        super("Dog");
    }
}

class PetWithTimestamp {
    private Pet pet;
    private long count;// 时间戳

    public PetWithTimestamp(Pet pet, long count) {
        super();
        this.pet = pet;
        this.count = count;
    }

    public Pet getPet() {
        return pet;
    }

    public long getCount() {
        return count;
    }
}