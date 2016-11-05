package edu.nju.collapsar;

import edu.nju.collapsar.invoker.Invoker;
import edu.nju.collapsar.invoker.StaticResourceReader;
import edu.nju.collapsar.routeInfo.DynamicRouteInfo;
import edu.nju.collapsar.routeInfo.RouteInfo;
import edu.nju.collapsar.routeInfo.StaticRouteInfo;
import edu.nju.collapsar.util.ResponseHelper;
import edu.nju.collapsar.util.RouteManager;
import org.omg.CORBA.portable.ResponseHandler;

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
            String requestStr = requestBuilder.toString();

            Request request = RequestParser.parse(requestStr);
            Response response = new ResponseImpl();

            handle(request,response);
        }

        private void handle(Request request,Response response){
            RouteInfo routeInfo = RouteManager.getRouteManager().getRouting(request.getUrl());
            if(routeInfo instanceof DynamicRouteInfo){
                Invoker invoker = new Invoker();
                invoker.invoke(routeInfo.getJarPath(),((DynamicRouteInfo) routeInfo).getClassName(),request,response);
                try {
                    ResponseHelper.quickSet(response);
                    outputStream.write(response.generateResponseMessage().getBytes());
                    outputStream.flush();
                    outputStream.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                StaticResourceReader reader = new StaticResourceReader();
                reader.read(routeInfo.getJarPath(),((StaticRouteInfo) routeInfo).getFilePath(),outputStream);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
