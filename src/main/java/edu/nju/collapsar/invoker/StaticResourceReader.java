package edu.nju.collapsar.invoker;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by LvBQ on 2016/11/3.
 */
public class StaticResourceReader {

    private final int BUFFER_SIZE = 1024;//The buffer size for reading

    public void read(String jarPath ,String path , OutputStream outputStream) {

        //get root path for the file
        InputStream is = null;
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file:" + jarPath)});
            is =  classLoader.getResourceAsStream(path);
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }

        byte[] readBytes = new byte[BUFFER_SIZE];
        try {
            int endMark = is.read(readBytes, 0, BUFFER_SIZE);
            while(endMark != -1){
                outputStream.write(readBytes,0,BUFFER_SIZE);
                endMark = is.read(readBytes,0,BUFFER_SIZE);
            }

        }catch (Exception e){}

    }


}
