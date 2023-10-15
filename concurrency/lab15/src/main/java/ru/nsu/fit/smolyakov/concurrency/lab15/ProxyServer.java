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

    public void start(SocketAddress proxyAddr, SocketAddress destinationAddr) throws IOException {
        var selector = Selector.open();

        var proxySocket = ServerSocketChannel.open();
        proxySocket.bind(proxyAddr);
        proxySocket.configureBlocking(false);
        proxySocket.register(selector, SelectionKey.OP_ACCEPT);

        var buf = ByteBuffer.allocate(BUF_SIZE);
        var bufMap = new HashMap<SocketChannel, ByteBuffer>();

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

                    if (destinationSocket.connect(destinationAddr)) {
                        log.info("connected yo");
                        destinationSocket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, clientSocket);
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

                            sender.register(selector, sender.validOps() & ~SelectionKey.OP_READ, reciever);
                            reciever.register(selector, reciever.validOps() & ~SelectionKey.OP_WRITE, sender);

                            sender.register(selector, 0);
                            reciever.register(selector, 0);

                            log.info("shutting down sender input and reciever output");
                        } else {
                            bufMap.put(reciever, buf.duplicate());
//                            reciever.register(selector, reciever.validOps() | SelectionKey.OP_WRITE, sender);
                        }
                    }
                }

                if (key.isWritable()) {
                    log.warning("кто такой ваш writeable");

                    SocketChannel reciever = (SocketChannel) key.channel();
                    SocketChannel sender = (SocketChannel) key.attachment();

                    var savedBuf = bufMap.get(reciever);
                    if (savedBuf != null) {
                        bufMap.remove(reciever);
                        savedBuf.flip();
                        var bytesWrote = reciever.write(savedBuf);
                        savedBuf.clear();
                    }

//                    reciever.register(selector, reciever.validOps() & ~SelectionKey.OP_WRITE, sender);
                }

                iter.remove();
            }
        }

        proxySocket.close();
    }
}
