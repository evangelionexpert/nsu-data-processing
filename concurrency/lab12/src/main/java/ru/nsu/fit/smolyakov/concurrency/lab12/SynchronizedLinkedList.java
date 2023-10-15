package ru.nsu.fit.smolyakov.concurrency.lab12;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class SynchronizedLinkedList<E> extends LinkedList<E> {
    @Override
    public synchronized Node<E> pushAfter(Node<E> node, E value) {
        return super.pushAfter(node, value);
    }

    @Override
    public synchronized Node<E> pushBefore(Node<E> node, E value) {
        return super.pushBefore(node, value);
    }

    @Override
    public synchronized void remove(Node<E> curr) {
        super.remove(curr);
    }

    @Override
    public synchronized Node<E> pushFront(E value) {
        return super.pushFront(value);
    }

    @Override
    public synchronized Node<E> pushBack(E value) {
        return super.pushBack(value);
    }

    @Override
    public synchronized void sort(Comparator<? super E> comparator) {
        super.sort(comparator);
    }

    @Override
    public synchronized void forEach(Consumer<E> consumer) {
        super.forEach(consumer);
    }

    @Override
    public synchronized void print(PrintStream printStream) {
        super.print(printStream);
    }

    @Override
    public synchronized int size() {
        return super.size();
    }
}
