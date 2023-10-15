package ru.nsu.fit.smolyakov.concurrency.lab8;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class PiFounder {
    public static double parallelLeibniz(int threads, int iterationsCount) throws InterruptedException {
        if (threads < 1 || iterationsCount < 1) {
            throw new IllegalArgumentException();
        }

        var iteratorCdl = new CountDownLatch(threads);
        var finishCdl = new CountDownLatch(threads);

        var denominatorDiff = threads * 2;
        var currentFirstSign = 1;

        var alternateSeriesSign = threads % 2 != 0;

        var res = new double[threads];

        var iterations = new AtomicInteger(iterationsCount);
        var shutdownIterations = new AtomicInteger(-1);

        for (int i = 0; i < threads; i++) {
            var firstItemDenominator = (i * 2) + 1;
            int finalCurrentSign = currentFirstSign;

            int finalI = i;
            new Thread(() -> {
                  res[finalI] = leibnizSeriesSum(
                      firstItemDenominator,
                      denominatorDiff,
                      finalCurrentSign,
                      alternateSeriesSign,
                      iteratorCdl,
                      iterations,
                      shutdownIterations
                );
                finishCdl.countDown();
            }).start();

            currentFirstSign *= -1;
        }

        try {
            finishCdl.await();
        } catch (InterruptedException e) {
            iterations.set(-1);
            System.err.println("interrupted");
            finishCdl.await();
        }
        return Arrays.stream(res).sum();
    }

    private static double leibnizSeriesSum(
        int firstItemDenominator,
        int denominatorDiff,
        int firstItemSign,
        boolean alternateSign,
        CountDownLatch syncIterationsCdl,
        AtomicInteger iterations,
        AtomicInteger shutdownIterations
    ) {
        double res = 0;
        long denominator = firstItemDenominator;

        var signMultiplier = alternateSign ? -1 : 1;
        var currentSign = firstItemSign;

        int i = 0;
        for (; i < iterations.get(); i++) {
            res += currentSign * (4d/denominator);

            denominator += denominatorDiff;
            currentSign *= signMultiplier;
        }

        System.err.println(Thread.currentThread().getName() + " ::: i = " + i);

        if (iterations.get() == -1) {
            int finalI = i;
            shutdownIterations.getAndUpdate((oldVal) -> Math.max(oldVal, finalI));
        }

        syncIterationsCdl.countDown();
        try {
            syncIterationsCdl.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.err.println(Thread.currentThread().getName() + " ::: shutdownIterations = " + shutdownIterations.get());

        for (; i < shutdownIterations.get(); i++) {
            res += currentSign * (4d/denominator);

            denominator += denominatorDiff;
            currentSign *= signMultiplier;
        }

        return res;
    }
}
