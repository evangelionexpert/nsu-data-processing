package ru.nsu.fit.smolyakov.concurrency.lab13;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Fork {
    private final ReentrantLock lock = new ReentrantLock();
    private final String name;

    public Fork() {
        this("unnamed");
    };
    public Fork(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public void take() {
        lock.lock();
    }

    public void put() {
        lock.unlock();
    }

    public boolean isTaken() {
        return lock.isLocked();
    }

    public String toString() {
        return "fork " + name;
    }
}
