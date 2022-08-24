package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartProduction {
    public static void main(String[] args) {
        Object lock = new Object();

        Queue<Integer> queue=new LinkedBlockingQueue<>();


        new Producer(queue,100,lock).start();

        //let the producer create products for 5 seconds (stock).
        new Consumer(queue, lock).start();
    }
}
