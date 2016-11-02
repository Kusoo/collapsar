package edu.nju.collapsar;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by rico on 2016/11/2.
 */
public class CollapsarServer {

    private ServerSocket serverSocket = null;


    public static void main(String[] args){
        CollapsarServer server = new CollapsarServer();
        server.serve();
    }

    public void serve(){
        try {
            serverSocket = new ServerSocket(8080,1, InetAddress.getByName("127.0.0.1"));
            Socket socket = null;
            while(true){
                socket = serverSocket.accept();
                new Thread(new Worker(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
