package edu.nju.collapsar;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by rico on 2016/11/2.
 */
public class Worker implements Runnable{

    private Socket socket = null;
    private Request request = null;

    public Worker(Socket socket){
        this.socket = socket;
    }

    public void run() {

    }

}
