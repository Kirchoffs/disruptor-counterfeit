package org.syh.demo.disruptor.naive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArrayBlockingQueueTest {
    @Test
    public void testArrayBlockingQueueWithSynchronized() {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueueWithSynchronized<>(2);
        
        try {
            singleThreadTest(queue);
            oneProducerOneConsumerTest(queue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testArrayBlockingQueueWithReentrantLock() {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueueWithReentrantLock<>(2);
        
        try {
            singleThreadTest(queue);
            oneProducerOneConsumerTest(queue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void singleThreadTest(ArrayBlockingQueue<Integer> queue) throws InterruptedException {
        queue.put(1);
        queue.put(2);
        Assertions.assertEquals(1, queue.take().intValue());
        queue.put(3);
        Assertions.assertEquals(2, queue.take().intValue());
        Assertions.assertEquals(3, queue.take().intValue());
    }

    public void oneProducerOneConsumerTest(ArrayBlockingQueue<Integer> queue) throws InterruptedException {
        int N = 5;
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < N; i++) {
                    queue.put(i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                List<Integer> list = new ArrayList<>();
                for (int i = 0; i < N; i++) {
                    list.add(queue.take());
                }
                Collections.sort(list);
                for (int i = 0; i < N; i++) {
                    Assertions.assertEquals(i, list.get(i).intValue());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
    }
}
