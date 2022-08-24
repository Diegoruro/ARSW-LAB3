/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;

/**
 *
 * @author hcadavid
 */
public class Consumer extends Thread{
    
    private Queue<Integer> queue;

    private final Object lock;
    
    
    public Consumer(Queue<Integer> queue, Object lock){
        this.queue=queue;
        this.lock = lock;
    }
    
    @Override
    public void run() {
        while (true) {
            synchronized (lock) {
                if (queue.size() > 0) {
                    int elem = queue.poll();
                    System.out.println("Consumer consumes " + elem);
                    try {
                        Thread.sleep(2000);
                        lock.notify();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
