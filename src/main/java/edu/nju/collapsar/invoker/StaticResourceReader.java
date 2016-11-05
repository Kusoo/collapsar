package edu.nju.collapsar.invoker;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by LvBQ on 2016/11/3.
 */
public class StaticResourceReader {

    public InputStream read(String jarPath ,String path) {

        InputStream is = null;
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file:" + jarPath)});
            is =  classLoader.getResourceAsStream("resources/" + path);
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return  is;


    }


}
