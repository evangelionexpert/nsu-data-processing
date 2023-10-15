package ru.nsu.fit.smolyakov.concurrency.lab11;

import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {
        var childSem = new Semaphore(1);
        var parentSem = new Semaphore(1);

        childSem.acquireUninterruptibly();

        var thread = new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                childSem.acquireUninterruptibly();
                System.out.printf("[child thread] hello this is line %d%n", i);
                parentSem.release();
            }
        });
        thread.start();

        for (int i = 0; i < 20; i++) {
            parentSem.acquireUninterruptibly();
            System.out.printf("[parent thread] hello this is line %d%n", i);
            childSem.release();
        }

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
