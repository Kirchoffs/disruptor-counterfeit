package org.syh.demo.disruptor.naive;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrayBlockingQueueWithReentrantLock<E> implements ArrayBlockingQueue<E> {
    private final Object[] items;
    private int count;

    private int takeIndex;
    private int putIndex;

    private final ReentrantLock reentrantLock;
    private final Condition notEmpty;
    private final Condition notFull;

    public ArrayBlockingQueueWithReentrantLock(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }

        this.items = new Object[capacity];
        this.count = 0;

        this.takeIndex = 0;
        this.putIndex = 0;

        this.reentrantLock = new ReentrantLock();
        this.notEmpty = reentrantLock.newCondition();
        this.notFull = reentrantLock.newCondition();
    }

    public void put(E e) throws InterruptedException {
        reentrantLock.lock();
        try {
            while (count == items.length) {
                System.out.println("Full, so blocking");
                notFull.await();
            }
            System.out.println("Put");
            enqueue(e);
            notEmpty.signalAll();
        } finally {
            reentrantLock.unlock();
        }
    }

    public E take() throws InterruptedException {
        reentrantLock.lock();
        try {
            while (count == 0) {
                System.out.println("Empty, so blocking");
                notEmpty.await();
            }
            System.out.println("Take");
            E e = dequeue();
            notFull.signalAll();
            return e;
        } finally {
            reentrantLock.unlock();
        }
    }

    private void enqueue(E e) {
        final Object[] items = this.items;
        items[putIndex++] = e;
        if (putIndex == items.length) {
            putIndex = 0;
        }
        count++;
    }

    private E dequeue() {
        final Object[] items = this.items;
        E e = (E) items[takeIndex];
        items[takeIndex++] = null;
        if (takeIndex == items.length) {
            takeIndex = 0;
        }
        count--;
        return e;
    }
}
