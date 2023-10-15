package ru.nsu.fit.smolyakov.concurrency.lab9;

import java.util.Objects;

public class Philosopher implements Runnable {
    private final int eatingPeriodMillis;
    private final int thinkingPeriodMillis;

    private final Fork leftFork;
    private final Fork rightFork;

    private final String name;

    public Philosopher(Fork leftFork, Fork rightFork, int eatingPeriodMillis, int thinkingPeriodMillis, String name) {
        if (eatingPeriodMillis < 0 || thinkingPeriodMillis < 0) {
            throw new IllegalArgumentException();
        }

        this.eatingPeriodMillis = eatingPeriodMillis;
        this.thinkingPeriodMillis = thinkingPeriodMillis;
        this.leftFork = Objects.requireNonNull(leftFork);
        this.rightFork = Objects.requireNonNull(rightFork);
        this.name = Objects.requireNonNull(name);
    }

    public void run() {
        for (;;) {
            leftFork.take();
            System.out.printf("[%s] took left (%s) %n", this, leftFork);
            rightFork.take();
            System.out.printf("[%s] took right (%s) %n", this, rightFork);

            try {
                System.out.printf("     [%s] eating for %d secs %n", this, eatingPeriodMillis);
                Thread.sleep(eatingPeriodMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.printf("[%s] put left (%s) %n", this, leftFork);
            leftFork.put();
            System.out.printf("[%s] put right (%s) %n", this, rightFork);
            rightFork.put();

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
