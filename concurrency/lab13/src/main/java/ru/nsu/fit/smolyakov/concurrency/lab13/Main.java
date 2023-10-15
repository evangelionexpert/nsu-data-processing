package ru.nsu.fit.smolyakov.concurrency.lab13;

import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int n = 3;
        int eatMillis = 1000;
        int thinkMillis = 1000;

        var waiter = new Waiter(n);

        IntStream.range(0, n)
            .mapToObj(idx ->
                new Philosopher(
                    idx,
                    (idx+1) % n,
                    eatMillis,
                    thinkMillis,
                    Integer.toString(idx),
                    waiter
                )
            )
            .map(Thread::new)
            .forEach(Thread::start);
    }
}
