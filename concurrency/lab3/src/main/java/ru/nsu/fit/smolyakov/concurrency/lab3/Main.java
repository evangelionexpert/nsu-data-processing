package ru.nsu.fit.smolyakov.concurrency.lab3;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    private static void printStrings(List<String> strings) {
//        System.err.println("thread " + Thread.currentThread().getId() + " started!");
        strings.forEach(System.err::println);
    }

    public static void main(String[] args) {
        new Thread(() -> printStrings(List.of("1 ab", "1 oba", "1 387", "1 1"))).start();
        new Thread(() -> printStrings(List.of("2 single string"))).start();
        new Thread(() -> printStrings(List.of("3", "3 three"))).start();
        new Thread(() -> printStrings(List.of("4 !!!!!!", "4 probably", "4 for sure", "4 is four", "4 is 4", "4 4 4"))).start();
    }
}
