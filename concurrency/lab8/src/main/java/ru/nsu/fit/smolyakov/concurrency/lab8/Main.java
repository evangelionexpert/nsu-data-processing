package ru.nsu.fit.smolyakov.concurrency.lab8;

import sun.misc.Signal;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var thread = Thread.currentThread();

        Signal.handle(
            new Signal("INT"),
            signal -> {
                System.err.println("sigint recieved");
                thread.interrupt();
            }
            );

        var threads = 6;

        double res = PiFounder.parallelLeibniz(threads, Integer.MAX_VALUE);
        System.out.println(res);
    }
}
