package edu.nju.collapsar;

import java.net.Socket;

/**
 * Created by rico on 2016/11/2.
 */
public class Worker implements Runnable{

    private Socket socket = null;

    public Worker(Socket socket){
        this.socket = socket;
    }

    public void run() {

    }
}
