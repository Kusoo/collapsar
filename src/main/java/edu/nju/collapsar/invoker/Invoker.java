package edu.nju.collapsar.invoker;

import edu.nju.collapsar.Request;
import edu.nju.collapsar.Response;
import edu.nju.collapsar.quark.Quark;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by LvBQ on 2016/11/2.
 */
public class Invoker {

    private Quark quarkImpl = null;

    public void invoke(String jarPath , String className, Request request, Response response) {
        //Load class
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file:" + jarPath)});
            Class<?> implClass = classLoader.loadClass(className);
            quarkImpl = (Quark) implClass.newInstance();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //get request type
        switch (request.getType()){
            case "GET":
                quarkImpl.doGet(request,response);
                break;
            case "POST":
                quarkImpl.doPost(request,response);
                break;
            case "PUT":
                quarkImpl.doPut(request,response);
                break;
            case "DELETE":
                quarkImpl.doDelete(request,response);
               break;
            default:
                break;
        }
    }
}
