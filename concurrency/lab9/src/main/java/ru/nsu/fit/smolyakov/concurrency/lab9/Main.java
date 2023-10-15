package ru.nsu.fit.smolyakov.concurrency.lab9;

import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int n = 5;
        int eatMillis = 1000;
        int thinkMillis = 1000;

        List<Fork> forks = IntStream.range(0, n)
            .mapToObj(Integer::toString)
            .map(Fork::new)
            .toList();

        IntStream.range(0, n)
            .mapToObj(idx ->
                new Philosopher(
//                    forks.get(idx),
//                    forks.get((idx+1) % n),
                    forks.get(Integer.min(idx, (idx+1) % n)),
                    forks.get(Integer.max(idx, (idx+1) % n)),
                    eatMillis,
                    thinkMillis,
                    Integer.toString(idx)
                )
            )
            .map(Thread::new)
            .forEach(Thread::start);

    }
}

