package ru.nsu.fit.smolyakov.concurrency.lab7;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException();
        }

        var threads = Integer.parseInt(args[0]);
        var iterations = Integer.parseInt(args[1]);

        double res;
        try {
            res = PiFounder.parallelLeibniz(iterations, threads);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(res);
    }
}
