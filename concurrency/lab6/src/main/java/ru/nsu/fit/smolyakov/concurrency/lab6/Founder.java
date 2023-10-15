package ru.nsu.fit.smolyakov.concurrency.lab6;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Founder {
    private final List<Runnable> workers;
    private final Company company;
    private final CountDownLatch countDownLatch;
    public Founder(final Company company) {
        this.company = company;

        this.countDownLatch = new CountDownLatch(company.getDepartmentsCount());

        this.workers = company.
            .mapToObj(Department::new)
            .map(department -> {
                return (Runnable) () -> {
                    department.performCalculations();
                    countDownLatch.countDown();
                    System.out.printf("id %d: %d\n", Thread.currentThread().getId(), department.getCalculationResult());
                };
            })
            .toList();
    }

    public void start() {
        for (final Runnable worker : workers) {
            new Thread(worker).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        company.showCollaborativeResult();
    }
}
