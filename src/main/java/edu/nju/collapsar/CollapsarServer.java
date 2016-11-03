package edu.nju.collapsar;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CollapsarServer {

    private final static int BUFFER_SIZE = 8912;
    private final static int PORT = 8080;

    private ServerSocketChannel serverChannel;
    private Selector selector;

    private ExecutorService executor;

    public static void main(String[] args) throws Exception {
        CollapsarServer server = new CollapsarServer();
        server.serve();
    }

    public void serve() throws IOException {
        int cpuNum = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(cpuNum);

        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        ServerSocket ss = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(PORT);
        ss.bind(address);

        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        SelectionKey key = null;
        while (true) {
            selector.select();
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                key = it.next();
                it.remove();
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    Worker worker = new Worker(socketChannel);
                    executor.execute(worker);
                }
            }
        }
    }

}
