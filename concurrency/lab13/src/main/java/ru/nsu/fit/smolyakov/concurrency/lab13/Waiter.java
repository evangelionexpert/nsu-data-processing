package ru.nsu.fit.smolyakov.concurrency.lab13;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class Waiter {
    private final List<Fork> forks;
    public Waiter(int forksAmount) {
        this.forks = IntStream.range(0, forksAmount)
            .mapToObj(Integer::toString)
            .map(Fork::new)
            .toList();
    }
    public synchronized void takeForks(int fst, int snd) {
        while (forks.get(fst).isTaken() || forks.get(snd).isTaken()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        forks.get(fst).take();
        forks.get(snd).take();
    }

    public synchronized void putForks(int fst, int snd) {
        forks.get(fst).put();
        forks.get(snd).put();

        notifyAll();
    }
}
