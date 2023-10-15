package ru.nsu.fit.smolyakov.concurrency.lab13;

import java.util.Objects;

public class Philosopher implements Runnable {
    private final int eatingPeriodMillis;
    private final int thinkingPeriodMillis;

    private final int leftForkIdx;
    private final int rightForkIdx;

    private final Waiter waiter;

    private final String name;

    public Philosopher(int leftForkIdx,
                       int rightForkIdx,
                       int eatingPeriodMillis,
                       int thinkingPeriodMillis,
                       String name,
                       Waiter waiter) {
        if (eatingPeriodMillis < 0 || thinkingPeriodMillis < 0) {
            throw new IllegalArgumentException();
        }

        this.eatingPeriodMillis = eatingPeriodMillis;
        this.thinkingPeriodMillis = thinkingPeriodMillis;
        this.leftForkIdx = leftForkIdx;
        this.rightForkIdx = rightForkIdx;
        this.name = Objects.requireNonNull(name);
        this.waiter = Objects.requireNonNull(waiter);
    }

    public void run() {
        for (;;) {
            System.out.printf("[%s] trying to take left %d and right %d %n", this, leftForkIdx, rightForkIdx);
            waiter.takeForks(leftForkIdx, rightForkIdx);
            System.out.printf("[%s] took left %d and right %d %n", this, leftForkIdx, rightForkIdx);
            try {
                System.out.printf("     [%s] eating for %d secs %n", this, eatingPeriodMillis);
                Thread.sleep(eatingPeriodMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.printf("[%s] trying to put left %d and right %d %n", this, leftForkIdx, rightForkIdx);
            waiter.putForks(leftForkIdx, rightForkIdx);
            System.out.printf("[%s] succesfully put left %d and right %d %n", this, leftForkIdx, rightForkIdx);

            try {
                System.out.printf("     [%s] thinking for %d secs %n", this, eatingPeriodMillis);
                Thread.sleep(thinkingPeriodMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String toString() {
        return "philosopher " + name;
    }
}
