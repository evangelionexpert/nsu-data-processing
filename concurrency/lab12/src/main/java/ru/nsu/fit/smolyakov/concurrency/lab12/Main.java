package ru.nsu.fit.smolyakov.concurrency.lab12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        int sleepMillis = (int) Duration.ofSeconds(5).toMillis();
        var list = new SynchronizedLinkedList<String>();

        new Timer().scheduleAtFixedRate(
            new TimerTask() {
                @Override
                public void run() {
                    list.sort(String::compareTo);

                    System.out.println("\n!! Printing current state:");
                    list.print(System.out);
                    System.out.println();
                    System.out.println();
                }
            },
            sleepMillis,
            sleepMillis
            );

        var reader = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            String line = reader.readLine();
            list.pushFront(line);
        }
    }
}

