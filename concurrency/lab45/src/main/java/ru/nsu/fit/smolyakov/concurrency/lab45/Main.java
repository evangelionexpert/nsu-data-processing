package ru.nsu.fit.smolyakov.concurrency.lab45;

public class Main {
    private static void printImportantText() {
        System.out.println("ive started speaking");

        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("system out print ");

            try {
                Thread.sleep(133);
            } catch (InterruptedException e) {
                System.out.println("i was interrupted oops, stopping");
                return;
            }
        }

        System.out.println("ive stopped speaking");
    }

    public static void main(String[] args) {
        var thread = new Thread(Main::printImportantText);

        thread.start();
        System.out.println("MAIN started child thread, going to sleep");
        try {
            thread.join(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        thread.interrupt();

        System.out.println("MAIN stopped child thread");
    }
}
