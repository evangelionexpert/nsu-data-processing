package ru.nsu.fit.smolyakov.concurrency.lab7;

public class AtomicDouble {
    double value;

    public AtomicDouble(double value) {
        this.value = value;
    }

    public synchronized void add(double add) {
        value += add;
    }

    public synchronized double get() {
        return value;
    }
}
