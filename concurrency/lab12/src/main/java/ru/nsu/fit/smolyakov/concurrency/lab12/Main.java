package ru.nsu.fit.smolyakov.concurrency.lab12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        int sleepMillis = (int) Duration.ofSeconds(5).toMillis();
        var list = new SynchronizedLinkedList<String>();

        new Thread(() -> {
            for (;;) {
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                list.sort(String::compareTo);

                System.out.println("\n!! Printing current state:");
                list.print(System.out);
                System.out.println();
                System.out.println();
            }
        }).start();

        var reader = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            String line = reader.readLine();
            list.pushFront(line);
        }
    }
}

