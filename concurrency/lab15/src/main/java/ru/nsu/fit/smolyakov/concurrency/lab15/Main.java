package ru.nsu.fit.smolyakov.concurrency.lab15;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        new ProxyServer().start(
            new InetSocketAddress("localhost", 10050),
            new InetSocketAddress("lib.ru", 80));
    }
}

