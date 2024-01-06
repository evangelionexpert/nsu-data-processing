package ru.nsu.fit.smolyakov.concurrency.lab16;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class NaiveHttpClient implements AutoCloseable {
    private final static int WIDTH = 80;
    private final static int BUF_LINES = 100500;
    private final String hostname;
    private final int port;
    private final String path;
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(BUF_LINES, true);
    private final AtomicBoolean done = new AtomicBoolean(false);
    private final Socket socket;

    public NaiveHttpClient(String hostname) throws IOException {
        this(hostname, 80);
    }

    public NaiveHttpClient(String hostname, int port) throws IOException {
        if (hostname == null) {
            throw new NullPointerException("hostname is null");
        } else if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("port is not in range (0, 65535)");
        }

        int index = hostname.indexOf("/");
        if (index == -1) {
            this.hostname = hostname;
            this.path = "/";
        } else {
            this.hostname = hostname.substring(0, index);
            this.path = hostname.substring(index);
        }

        this.port = port;
        this.socket = new Socket(this.hostname, this.port);
    }

    private static String generateGetRequest(String host, String path) {
        return """
            GET %s HTTP/1.1
            Host: %s
            User-Agent: sobaka
            Connection: close
                        
            """.formatted(path, host);
    }

    public void sendGetRequest() throws IOException {
        System.err.printf("Connecting to %s, port %d\n\n", this.hostname, this.port);

        new Thread(() -> {
            try {
                var request = generateGetRequest(this.hostname, this.path);
                System.out.println(request);

                PrintWriter writer = new PrintWriter(socket.getOutputStream(), false);

                writer.write(request);
                writer.flush();

                var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                char[] buf = new char[WIDTH];
                int n;
                while ((n = reader.read(buf, 0, WIDTH)) > 0) {
                    try {
                        queue.put(String.valueOf(buf, 0, n));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                done.set(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public String getResponseLine() {
        if (done.get() && queue.isEmpty()) {
            return null;
        } else {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void close() throws Exception {
        socket.close();
    }
}
