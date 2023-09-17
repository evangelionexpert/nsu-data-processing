package ru.nsu.fit.smolyakov.concurrency.lab7;

import java.util.concurrent.CountDownLatch;

public class PiFounder {
    /**
     * threads = 2
     * 1) +1/1 +1/5  +1/9 +1/13 +1/17 +1/21
     * 2) -1/3 -1/7 -1/11 -1/15 -1/19 -1/23
     *
     * <p></p>thread = 3
     * 1) +1/1  -1/7 +1/13 -1/19
     * 2) -1/3  +1/9 -1/15 +1/21
     * 3) +1/5 -1/11 +1/17 -1/23
     *
     * <p></p>threads = 4
     * 1) +1/1  +1/9 +1/17
     * 2) -1/3 -1/11 -1/19
     * 3) +1/5 +1/13 +1/21
     * 4) -1/7 -1/15 -1/23
     *
     * <p></p>...
     *
     * @param iterations итератионс
     * @return ретурн
     */
    public static double parallelLeibniz(int iterations, int threads) throws InterruptedException {
        if (threads < 1 || iterations < 1) {
            throw new IllegalArgumentException();
        }

        var countDownLatch = new CountDownLatch(threads);
        var res = new AtomicDouble(0);

        var denominatorDiff = threads * 2;
        var currentFirstSign = 1;

        var alternateSeriesSign = threads % 2 != 0;

        for (int i = 0; i < threads; i++) {
            var firstItemDenominator = (i * 2) + 1;
            int finalCurrentSign = currentFirstSign;

            new Thread(() -> {
                double sum = leibnizSeriesSum(
                    iterations,
                    firstItemDenominator,
                    denominatorDiff,
                    finalCurrentSign,
                    alternateSeriesSign
                );
                res.add(sum);
                countDownLatch.countDown();
            }).start();

            currentFirstSign *= -1;
        }

        countDownLatch.await();
        return res.get() * 4;
    }

    private static double leibnizSeriesSum(
        int iterations,
        int firstItemDenominator,
        int denominatorDiff,
        int firstItemSign,
        boolean alternateSign
    ) {
        double res = 0;
        int denominator = firstItemDenominator;

        var signMultiplier = alternateSign ? -1 : 1;
        var currentSign = firstItemSign;

        for (int i = 0; i < iterations; i++) {
            res += currentSign * (1d/denominator);

            System.out.println(Thread.currentThread().getName() + " ::: " + currentSign + "/" + denominator);

            denominator += denominatorDiff;
            currentSign *= signMultiplier;
        }

        return res;
    }
}
