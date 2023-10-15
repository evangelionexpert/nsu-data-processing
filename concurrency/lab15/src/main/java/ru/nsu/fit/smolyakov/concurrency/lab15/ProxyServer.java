package ru.nsu.fit.smolyakov.concurrency.lab15;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
public class ProxyServer {
    private static final Logger log = Logger.getLogger("meme");
    public static final int BUF_SIZE = 256;

    public void start(SocketAddress proxy, SocketAddress destination) throws IOException {
        var selector = Selector.open();

        var proxySocket = ServerSocketChannel.open();
        proxySocket.bind(proxy);
        proxySocket.configureBlocking(false);
        proxySocket.register(selector, SelectionKey.OP_ACCEPT);

        var buffer = ByteBuffer.allocate(BUF_SIZE);
        var bufferAllocs = new HashMap<>();

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
                    clientSocket.register(selector, SelectionKey.OP_READ, destinationSocket);

                    if (destinationSocket.connect(destination)) {
                        log.info("connected yo");
                        destinationSocket.register(selector, SelectionKey.OP_READ, clientSocket);
                    } else {
                        log.info("not connectable yet, 'll try later");
                        destinationSocket.register(selector, SelectionKey.OP_CONNECT, clientSocket);
                    }
                }

                if (key.isConnectable()) {
                    log.info("became connectable");

                    var destinationSocket = (SocketChannel) key.channel();
                    if (destinationSocket.finishConnect()) {
                        log.info("connected successfully");
                        destinationSocket.register(selector, SelectionKey.OP_READ, key.attachment());
                    }
                }

                if (key.isReadable()) {
                    SocketChannel sender = (SocketChannel) key.channel();
                    SocketChannel reciever = (SocketChannel) key.attachment();

                    log.info("can read from somewhere");

                    var bytesRead = sender.read(buffer);
                    if (bytesRead == -1 /*EOF*/) {
//                        sender.shutdownInput();
//                        reciever.shutdownOutput();
                        log.info("shutting down sender input and reciever output");
                    } else {
                        buffer.flip();
                        var bytesWrote = reciever.write(buffer);
                        if (bytesWrote == -1 /*EOF*/) {
//                            sender.shutdownOutput();
//                            reciever.shutdownInput();
                            log.info("произошла хрень линия 65");
                        }
                        buffer.clear();
                    }
                }

                if (key.isWritable()) {
                    log.warning("кто такой ваш writeable");
                }

                iter.remove();
            }
        }

//        proxySocket.close();
//        destinationSocket.close();
    }
}
