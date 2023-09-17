package ru.nsu.fit.smolyakov.concurrency.lab3;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Main {
    private static void printStrings(String[] strings, int from, int to) {
        var threadName = Thread.currentThread().getName();
        IntStream.range(from, to)
            .mapToObj(index -> "[%s] %s".formatted(threadName, strings[index]))
            .forEachOrdered(System.out::println);
    }

    public static void main(String[] args) {
        String[] strings = {
            "one",
            "two",
            "three",
            "four",
            "five",
            "six",
            "seven",
            "eight",
            "nine",
            "ten",
            "eleven"
        };

        int threadsAmount = 4;

        SplitIntoThreads.splitIntoThreads(
            range -> printStrings(strings, range.from(), range.to()),
            new SplitIntoThreads.Range(0, strings.length),
            threadsAmount
        );
    }
}
