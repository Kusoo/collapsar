package edu.nju.collapsar;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AioServer {
    private final static int PORT = 8080;

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
                            String request = requestBuilder.toString();
                            ByteBuffer resBuffer = ByteBuffer.allocate(2048);

                            //process the write event
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
}
