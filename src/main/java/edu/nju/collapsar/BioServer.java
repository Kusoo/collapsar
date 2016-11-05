package edu.nju.collapsar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {
    private final static int PORT = 8080;

    public void serve() {
        try {
            ServerSocket server = new ServerSocket();
            InetSocketAddress address = new InetSocketAddress(PORT);
            server.bind(address);
            while (true) {
                Socket client = server.accept();
                new Thread(new Worker(client)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Worker implements Runnable {
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public Worker(Socket socket) {
            this.socket = socket;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[2048];
            StringBuilder requestBuilder = new StringBuilder();
            int size = 0;
            try {
                size = inputStream.read(buffer);
            } catch (IOException e) {
                size = -1;
            }
            for (int i = 0; i < size; i++) {
                requestBuilder.append((char) buffer[i]);
            }
            String request = requestBuilder.toString();

            Response response = null;
        }
    }
}
