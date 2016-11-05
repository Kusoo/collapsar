package edu.nju.collapsar;

import edu.nju.collapsar.invoker.Invoker;
import edu.nju.collapsar.invoker.StaticResourceReader;
import edu.nju.collapsar.routeInfo.DynamicRouteInfo;
import edu.nju.collapsar.routeInfo.RouteInfo;
import edu.nju.collapsar.routeInfo.StaticRouteInfo;
import edu.nju.collapsar.util.ResponseHelper;
import edu.nju.collapsar.util.RouteManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.Executors;

public class AioServer {
    private final static int PORT = 8080;
    private final int BUFFER_SIZE = 1024;

    public void serve() {
        int cpuNum = Runtime.getRuntime().availableProcessors();
        try {
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(cpuNum));
            AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group);
            InetSocketAddress address = new InetSocketAddress(PORT);
            server.bind(address);

            //process the accept event
            server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                @Override
                public void completed(AsynchronousSocketChannel client, Object attachment) {
                    ByteBuffer reqBuffer = ByteBuffer.allocate(2048);

                    //process the read event
                    client.read(reqBuffer, reqBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            attachment.flip();
                            byte[] buffer = new byte[attachment.limit()];
                            attachment.get(buffer);
                            StringBuilder requestBuilder = new StringBuilder();
                            for (int i = 0; i < buffer.length; i++) {
                                requestBuilder.append((char) buffer[i]);
                            }
                            String requestStr = requestBuilder.toString();
                            Request request = RequestParser.parse(requestStr);
                            Response response = new ResponseImpl();

                            Worker worker = new Worker(client,request,response);
                            worker.work();

                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            try {
                                client.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void failed(Throwable exc, Object attachment) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Worker{
        private AsynchronousSocketChannel client;
        private Request request;
        private Response response;
        private ReadableByteChannel fileChannel;
        private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        public Worker(AsynchronousSocketChannel client,Request request, Response response){
            this.client = client;
            this.request = request;
            this.response = response;
        }
        public void work(){
            RouteInfo routeInfo = RouteManager.getRouteManager().getRouting(request.getUrl());
            if (routeInfo instanceof DynamicRouteInfo) {
                Invoker invoker = new Invoker();
                invoker.invoke(routeInfo.getJarPath(), ((DynamicRouteInfo) routeInfo).getClassName(), request, response);
                ResponseHelper.quickSet(response);
                ByteBuffer resBuffer = ByteBuffer.wrap(response.generateResponseMessage().getBytes());
                client.write(resBuffer, resBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        try {
                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        try {
                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                StaticResourceReader reader = new StaticResourceReader();
                InputStream is = reader.read(routeInfo.getJarPath(), ((StaticRouteInfo) routeInfo).getFilePath());
                fileChannel = Channels.newChannel(is);
                writeStaticFile();
            }
        }

        private void writeStaticFile(){
            //process the write event
            buffer.clear();
            int readNum = 0;
            try {
                readNum = fileChannel.read( buffer );
                if(readNum == -1) {
                    client.close();
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            buffer.flip();
            client.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    writeStaticFile();
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
