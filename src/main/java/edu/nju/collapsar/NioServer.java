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

public class NioServer {
    private final static int PORT = 8080;

    private ServerSocketChannel serverChannel;
    private Selector selector;

    private ExecutorService executor;

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

    private class Worker implements Runnable{
        private final ByteBuffer buffer = ByteBuffer.allocate(2048);
        private SocketChannel socketChannel;

        public Worker(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        public void run() {
            buffer.clear();
            try {
                socketChannel.read(buffer);
                String requestStr = new String(buffer.array());
                //parse the request string
                Request request = null;

                //response to not file-request
                Response response = null;
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
