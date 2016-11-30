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
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioServer {
    private int PORT = 8080;
    private final int BUFFER_SIZE = 1024;

    public BioServer(){
        PORT = ConfigManager.getPort();
    }

    public void serve() {
        try {
            int cpuNum = Runtime.getRuntime().availableProcessors();
            ExecutorService executor = Executors.newFixedThreadPool(cpuNum*4);
            ServerSocket server = new ServerSocket();
            InetSocketAddress address = new InetSocketAddress(PORT);
            server.bind(address);
            while (true) {
                Socket client = server.accept();
                executor.execute(new Worker(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Worker implements Runnable {
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        private long startTime;
        private int surviveTime = 5;
        private int maxServingNum = 10;
        private int servingNum = 0;
        private boolean isFirstRequest = true;

        public Worker(Socket socket) {
            this.socket = socket;
            startTime = System.currentTimeMillis();
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Map<String,String> parseKeepAlive(String keepAliveStr){
            Map<String,String> map = new HashMap<>();
            if(keepAliveStr.contains(",")){
                for(String piece:keepAliveStr.split(",")){
                    fillMap(map,piece);
                }
            } else {
                fillMap(map,keepAliveStr);
            }
            return map;
        }

        private void fillMap(Map<String,String> map,String keepAliveStr){
            String[] blocks = keepAliveStr.split("=");
            if(blocks.length>=2){
                map.put(blocks[0].trim().toUpperCase(),blocks[1].trim());
            }
        }

        @Override
        public void run() {
            while((System.currentTimeMillis() - startTime)/1000<surviveTime&&servingNum<maxServingNum){
                byte[] buffer = new byte[BUFFER_SIZE];
                StringBuilder requestBuilder = new StringBuilder();
                int size;
                try {
                    if(inputStream.available()>0){
                        do{
                            size = inputStream.read(buffer);
                            for (int i = 0; i < size; i++) {
                                requestBuilder.append((char) buffer[i]);
                            }
                        } while (size == BUFFER_SIZE);
                        String requestStr = requestBuilder.toString();

                        Request request = RequestParser.parse(requestStr);
                        Response response = new ResponseImpl();

                        //如果是初次请求设置最大请求时间和最大服务次数
                        if(isFirstRequest){
                            int clientSurviveTime = 5;
                            int clientMaxservingNum = 10;
                            String keepAlivestr = request.getHeader("Keep-Alive");
                            if( null != keepAlivestr){
                                Map<String,String> map = parseKeepAlive(keepAlivestr);
                                clientSurviveTime = map.get("TIMEOUT")!=null?Integer.parseInt(map.get("TIMEOUT")):60;
                                clientMaxservingNum = map.get("MAX")!=null?Integer.parseInt(map.get("MAX")):10;
                            }
                            surviveTime = clientSurviveTime;
                            maxServingNum = clientMaxservingNum;
                            isFirstRequest = false;
                        }
                        servingNum++;
                        System.out.println(servingNum);
                        handle(request,response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //直到最大存活时间和最大服务次数任意一个达到时，关闭TCP连接
            try {
                outputStream.close();
                socket.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        private void handle(Request request,Response response){
            RouteInfo routeInfo = RouteManager.getRouting(request.getUrl());
            if (null == routeInfo){
                try {
                    ResponseHelper.quickSet404(response);
                    outputStream.write(response.generateResponseMessage().getBytes());
                    outputStream.flush();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            if(routeInfo instanceof DynamicRouteInfo){
                Invoker invoker = new Invoker();
                invoker.invoke(routeInfo.getJarPath(),((DynamicRouteInfo) routeInfo).getClassName(),request,response);
                try {
                    ResponseHelper.quickSet200(response);
                    outputStream.write(response.generateResponseMessage().getBytes());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                StaticResourceReader reader = new StaticResourceReader();
                InputStream is = reader.read(routeInfo.getJarPath(),((StaticRouteInfo) routeInfo).getFilePath());
                if(is != null) {
                    byte[] readBytes = new byte[BUFFER_SIZE];
                    try {
                        int byteNum = is.read(readBytes, 0, BUFFER_SIZE);
                        while (byteNum != -1) {
                            outputStream.write(readBytes, 0, byteNum);
                            byteNum = is.read(readBytes, 0, BUFFER_SIZE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            outputStream.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    //Request a non exist file
                    //TODO: handle exception
                    System.out.println("Missing file: " + ((StaticRouteInfo) routeInfo).getFilePath());
                }
            }
        }
    }
}
