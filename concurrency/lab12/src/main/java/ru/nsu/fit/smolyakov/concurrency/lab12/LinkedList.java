package ru.nsu.fit.smolyakov.concurrency.lab12;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;

public class LinkedList<E> {
    public static class Node<T> {
        private T value;
        private Node<T> next;
        private Node<T> prev;

        private Node(T value) {
            this(value, null, null);
            this.prev = this;
            this.next = this;
        }
        private Node(T value, Node<T> prev, Node<T> next) {
            this.value = value;
            this.next = next;
            this.prev = prev;
        }

        public T getValue() {
            return value;
        }
    }
    private final Node<E> sentinel = new Node<>(null);

    public LinkedList() {
    }

    public Node<E> pushAfter(Node<E> node, E value) {
        var insertedNode = new Node<>(value, node, node.next);
        node.next.prev = insertedNode;
        node.next = insertedNode;

        return insertedNode;
    }

    public Node<E> pushBefore(Node<E> node, E value) {
        return pushAfter(node.prev, value);
    }

    public void remove(Node<E> curr) {
        if (curr == null) {
            throw new NullPointerException();
        }

        var prev = curr.prev;
        var next = curr.next;

        if (prev == curr && curr == next) {
            throw new IllegalStateException();
        }

        prev.next = next;
        next.prev = prev;
        curr = null;
    }

    public Node<E> pushFront(E value) {
        return pushBefore(sentinel, value);
    }

    public Node<E> pushBack(E value) {
        return pushAfter(sentinel, value);
    }

    private void swapValues(Node<E> fst, Node<E> snd) {
        var tmp = fst.value;
        fst.value = snd.value;
        snd.value = tmp;
    }

    public void sort(Comparator<? super E> comparator) {
        for (var node1 = sentinel.next; node1 != sentinel.prev; node1 = node1.next) {
            for (var node2 = sentinel.next; node2 != sentinel.prev; node2 = node2.next) {
                if (comparator.compare(node2.value, node2.next.value) > 0) {
                    swapValues(node2, node2.next);
                }
            }
        }
    }

    public void forEach(Consumer<E> consumer) {
        for (var node = sentinel.next; node != sentinel; node = node.next) {
            consumer.accept(node.value);
        }
    }

    public void print(PrintStream printStream) {
        this.forEach(printStream::println);
    }

    public int size() {
        int counter = 0;
        for (var node = sentinel.next; node != sentinel; node = node.next) {
            counter++;
        }
        return counter;
    }
}
