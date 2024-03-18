package org.syh.demo.disruptor.naive;

public class ArrayBlockingQueueWithSynchronized<E> implements ArrayBlockingQueue<E> {
    private final Object[] items;
    private int count;

    private int takeIndex;
    private int putIndex;

    public ArrayBlockingQueueWithSynchronized(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }

        this.items = new Object[capacity];
        this.count = 0;
        
        this.takeIndex = 0;
        this.putIndex = 0;
    }

    public void put(E e) throws InterruptedException {
        synchronized (this) {
            while (count == items.length) {
                System.out.println("Full, so blocking");
                this.wait();
            }
            System.out.println("Put");
            enqueue(e);
            this.notifyAll();
        }
    }

    public E take() throws InterruptedException {
        synchronized (this) {
            while (count == 0) {
                System.out.println("Empty, so blocking");
                this.wait();
            }
            System.out.println("Take");
            E e = dequeue();
            this.notifyAll();
            return e;
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
