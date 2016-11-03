package edu.nju.collapsar.invoker;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by LvBQ on 2016/11/3.
 */
public class StaticResourceReader {

    private String jarPath = "F:/Workspace/Github/collapsar/classes/artifacts/quarkImpl/CollapsarUser.jar";
    private String rootPath = "resources/";
    private final int Max_Buffer_Size = 1024 * 10;// Max size of a file
    private final int buffer_size = 100;

    public byte[] read(String path) {
        //get root path for the file
        InputStream is = null;
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file:" + jarPath)});
            is =  classLoader.getResourceAsStream(rootPath + path);
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }

        byte[] resultBytes = new byte[Max_Buffer_Size];
        try {
            is.read(resultBytes, 0, resultBytes.length);
        }catch (Exception e){}

        return resultBytes;
    }


}
