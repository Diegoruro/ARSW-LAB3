/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

/**
 *
 * @author hcadavid
 */
public class CountThreadsMain implements Runnable {
    
    public static void main(String args[]){
        CountThread countThread = new CountThread(0,99);
        CountThread countThread1 = new CountThread(99,199);
        CountThread countThread2 = new CountThread(200,299);
        countThread.start();
        countThread1.start();
        countThread2.start();
    }

    @Override
    public void run() {
    }
}
