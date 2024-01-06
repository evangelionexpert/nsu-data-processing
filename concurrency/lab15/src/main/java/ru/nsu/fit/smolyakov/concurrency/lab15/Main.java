package ru.nsu.fit.smolyakov.concurrency.lab15;

import sun.misc.Signal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            throw new IllegalArgumentException();
        }

        var proxyAddr = new InetSocketAddress("localhost", Integer.parseInt(args[0]));
        var destinationAddr = new InetSocketAddress(args[1], Integer.parseInt(args[2]));

        try (ProxyServer server = new ProxyServer(proxyAddr, destinationAddr)) {
            Signal.handle(
                new Signal("INT"),
                signal -> {
                    System.err.println("sigint recieved");
                    try {
                        server.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            );

            server.start();
        }
    }
}

