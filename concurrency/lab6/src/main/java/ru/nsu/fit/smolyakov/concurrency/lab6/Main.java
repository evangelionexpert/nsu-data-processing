package ru.nsu.fit.smolyakov.concurrency.lab6;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        var company = new Company(24);
        var founder = new Founder(company);
        founder.start();
    }
}
