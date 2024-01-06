package ru.nsu.fit.smolyakov.concurrency.lab15;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;
public class ProxyServer implements AutoCloseable {
    private static final Logger log = Logger.getLogger("meme");
    public static final int BUF_SIZE = 16384;

    private final SocketAddress proxyAddr;
    private final SocketAddress destinationAddr;

    private Selector selector = null;
    private ServerSocketChannel proxySocket = null;

    public ProxyServer(SocketAddress proxyAddr, SocketAddress destinationAddr) {
        this.proxyAddr = Objects.requireNonNull(proxyAddr);
        this.destinationAddr = Objects.requireNonNull(destinationAddr);
    }

    public void start() throws IOException {
        this.selector = Selector.open();
        this.proxySocket = ServerSocketChannel.open();

        proxySocket.bind(proxyAddr);
        proxySocket.configureBlocking(false);
        proxySocket.register(selector, SelectionKey.OP_ACCEPT);

        log.info("listening on " + proxyAddr );

        var buf = ByteBuffer.allocate(BUF_SIZE);
        var bufMap = new HashMap<SocketChannel, ByteBuffer>();

        try {
            while (selector.isOpen()) {
                selector.select();

                var iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    var key = iter.next();
                    System.out.println(key);

                    if (key.isAcceptable()) {
                        log.info("pending accept");

                        var destinationSocket = SocketChannel.open();
                        destinationSocket.configureBlocking(false);

                        var clientSocket = proxySocket.accept();
                        clientSocket.configureBlocking(false);
                        clientSocket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, destinationSocket);

                        try {
                            if (destinationSocket.connect(destinationAddr)) {
                                log.info("quickly connected to " + destinationAddr);
                                destinationSocket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, clientSocket);
                            } else {
                                log.info("not connectable yet, 'll try later " + destinationAddr);
                                destinationSocket.register(selector, SelectionKey.OP_CONNECT, clientSocket);
                            }
                        } catch (Exception e) {
                            log.warning("unable to connect");
                            destinationSocket.close();
                            clientSocket.close();
                        }
                    }

                    if (key.isConnectable()) {
                        log.info("became connectable");

                        var destinationSocket = (SocketChannel) key.channel();
                        if (destinationSocket.finishConnect()) {
                            log.info("connected successfully");
                            destinationSocket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, key.attachment());
                        }
                    }

                    if (key.isReadable()) {
                        log.info("can read from somewhere");

                        SocketChannel sender = (SocketChannel) key.channel();
                        SocketChannel reciever = (SocketChannel) key.attachment();

                        if (!bufMap.containsKey(reciever)) {
                            buf.clear();
                            var bytesRead = sender.read(buf);
                            if (bytesRead == -1 /*EOF*/) {
                                sender.shutdownInput();
                                reciever.shutdownOutput();

                                sender.register(selector, key.interestOps() & ~SelectionKey.OP_READ, reciever);
                                reciever.register(selector, reciever.keyFor(selector).interestOps() & ~SelectionKey.OP_WRITE, sender);

                                log.info("shutting down sender input and reciever output");
                            } else {
                                var savedBuf = buf.duplicate();
                                savedBuf.flip();
                                bufMap.put(reciever, savedBuf);
                                reciever.register(selector, reciever.keyFor(selector).interestOps() | SelectionKey.OP_WRITE, sender);
                            }
                        }
                    }

                    if (key.isWritable()) {
                        log.info("кто такой ваш writeable");

                        SocketChannel reciever = (SocketChannel) key.channel();
                        SocketChannel sender = (SocketChannel) key.attachment();

                        var savedBuf = bufMap.get(reciever);
                        if (savedBuf != null) {
                            reciever.write(savedBuf);
                            if (!savedBuf.hasRemaining()) {
                                bufMap.remove(reciever);
                            }
                        }

                        reciever.register(selector, key.interestOps() & ~SelectionKey.OP_WRITE, sender);
                    }

                    iter.remove();
                }
            }
        } catch (ClosedSelectorException e) {
            log.info("select impossible, goin back: " + e);
            return;
        }

        System.out.println("server done");
    }

    @Override
    public void close() throws Exception {
        if (selector != null) {
            selector.close();
            selector = null;
            log.info("closed selector");
        }

        if (proxySocket != null) {
            proxySocket.close();
            proxySocket = null;
            log.info("closed proxy socket");
        }
    }
}
