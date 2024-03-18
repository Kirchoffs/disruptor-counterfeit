package org.syh.demo.disruptor.naive;

public interface ArrayBlockingQueue<E> {
    void put(E e) throws InterruptedException;
    E take() throws InterruptedException;
}
