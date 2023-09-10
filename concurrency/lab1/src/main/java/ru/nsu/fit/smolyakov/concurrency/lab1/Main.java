package ru.nsu.fit.smolyakov.concurrency.lab1;

import java.util.stream.IntStream;

public class Main {
    private static void printLines(int lines) {
        long threadId = Thread.currentThread().getId();
        IntStream.range(0, lines)
            .mapToObj(lineNo -> "[thread %d] hello this is line %d".formatted(threadId, lineNo))
            .forEachOrdered(System.out::println);
    }

    public static void main(String[] args) {
        new Thread(() -> printLines(10)).start();
        printLines(10);
    }
}
