package ru.nsu.fit.smolyakov.concurrency.lab3;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public class SplitIntoThreads {
    /**
     *
     * @param from inclusive
     * @param to exclusive
     */
    public record Range(int from, int to) {};

    /**
     * 8 strings, 3 cores: 3 3 2
     * [0 2]
     * [3 5]
     * [6 7]
     *
     * 8 strings, 4 cores: 2 2 2 2
     * 8 strings, 5 cores: 2 2 2 1 1
     * 16 strings 5 cores: 4 3 3 3 3
     *
     * @param rangeConsumer
     * @param total
     * @param threads
     */
    public static void splitIntoThreads(Consumer<Range> rangeConsumer, Range total, int threads) {
        var div = total.to / threads;
        var remainder = total.to % threads;
        var remainderUsed = 0;

        for (int i = 0; i < threads; i++) {
            int from = total.from + remainderUsed + i * div;
            int to = total.from + remainderUsed + (i + 1) * div;

            if (remainderUsed < remainder) {
                remainderUsed++;
                to++;
            }

            int finalTo = to;
            new Thread(
                () -> rangeConsumer.accept(new Range(from, finalTo)),
                "Thread " + (i + 1)
            ).start();
        }
    }
}
