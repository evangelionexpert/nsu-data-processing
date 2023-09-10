package ru.nsu.fit.smolyakov.concurrency.lab2;

import java.util.stream.IntStream;

public class Main {
    private static void printLines(int lines) {
        long threadId = Thread.currentThread().getId();
        IntStream.range(0, lines)
            .mapToObj(lineNo -> "[thread %d] hello this is line %d".formatted(threadId, lineNo))
            .forEach(System.err::println);
    }

    public static void main(String[] args) {
        var thread = new Thread(() -> printLines(10));

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        printLines(10);
    }
}
