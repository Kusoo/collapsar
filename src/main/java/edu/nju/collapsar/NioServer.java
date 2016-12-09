package edu.nju.collapsar;

import edu.nju.collapsar.invoker.Invoker;
import edu.nju.collapsar.invoker.StaticResourceReader;
import edu.nju.collapsar.routeInfo.DynamicRouteInfo;
import edu.nju.collapsar.routeInfo.RouteInfo;
import edu.nju.collapsar.routeInfo.StaticRouteInfo;
import edu.nju.collapsar.util.ConfigManager;
import edu.nju.collapsar.util.RequestParser;
import edu.nju.collapsar.util.ResponseHelper;
import edu.nju.collapsar.util.RouteManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioServer {
    private int PORT;
    private final int BUFFER_SIZE = 1024;

    private ServerSocketChannel serverChannel;
    private Selector selector;

    private ExecutorService executor;

    public NioServer(){
        PORT = ConfigManager.getPort();
    }

    public void serve(){
        int cpuNum = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(cpuNum);

        try {
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
                        key.cancel();
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        Worker worker = new Worker(socketChannel);
                        executor.execute(worker);
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private class Worker implements Runnable {
        private ByteBuffer reqBuffer = ByteBuffer.allocate(2048);
        private ByteBuffer resBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        private SocketChannel socketChannel;

        public Worker(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        public void run() {
            try {
                int size = socketChannel.read(reqBuffer);
                reqBuffer.flip();
                byte[] requestBuffer = new byte[size];
                reqBuffer.get(requestBuffer);
                String requestStr = new String(requestBuffer);

                Request request = RequestParser.parse(requestStr);
                Response response = new ResponseImpl();

                handle(request, response);
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void handle(Request request, Response response) {
            RouteInfo routeInfo = RouteManager.getRouting(request.getUrl());
            if (routeInfo instanceof DynamicRouteInfo) {
                Invoker invoker = new Invoker();
                invoker.invoke(routeInfo.getJarPath(), ((DynamicRouteInfo) routeInfo).getClassName(), request, response);
                try {
                    ResponseHelper.quickSet200(response);
                    socketChannel.write(ByteBuffer.wrap(response.generateResponseMessage().getBytes()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                StaticResourceReader reader = new StaticResourceReader();
                InputStream is = reader.read(routeInfo.getJarPath(), ((StaticRouteInfo) routeInfo).getFilePath());
                if(is != null){
                    ReadableByteChannel fileChannel = Channels.newChannel(is);
                    try {
                        while (true) {
                            resBuffer.clear();
                            int r = fileChannel.read(resBuffer);
                            if (r == -1) {
                                break;
                            }
                            resBuffer.flip();
                            socketChannel.write(resBuffer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    //Request a non exist file
                    //TODO: handle exception
                    ResponseHelper.quickSet404(response);
                    try {
                        socketChannel.write(ByteBuffer.wrap(response.generateResponseMessage().getBytes()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
