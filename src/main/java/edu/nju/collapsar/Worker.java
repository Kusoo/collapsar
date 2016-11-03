package edu.nju.collapsar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Worker implements Runnable {
    private final ByteBuffer buffer = ByteBuffer.allocate(2048);
    private SocketChannel socketChannel;
    private Charset charset = Charset.forName("UTF-8");

    public Worker(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void run() {
        buffer.clear();
        try {
            socketChannel.read(buffer);
            String requestStr = new String(buffer.array());

            //response to not file-request
            Response response = new Response() {
                public void write(String content) {
                    try {
                        socketChannel.write(charset.encode(CharBuffer.wrap(content)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
