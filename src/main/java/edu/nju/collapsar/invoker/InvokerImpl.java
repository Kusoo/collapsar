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
public class InvokerImpl  implements Invoker{

    private String jarPath = "F:/Workspace/Github/collapsar/classes/artifacts/quarkImpl/CollapsarUser.jar";
    private String invokingClass = "nju.edu.collapsarUser.CollapsarUser";
    private Quark quarkImpl = null;

    public void invoke(String className, Request request, Response response) {
        //Load class
        invokingClass = className;
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
            case "Get":
                quarkImpl.doGet(request,response);
                break;
            case "Post":
                quarkImpl.doPost(request,response);
                break;
            case "Put":
                quarkImpl.doPut(request,response);
                break;
            case "Delete":
                quarkImpl.doDelete(request,response);
               break;
            default:
                break;
        }
    }
}
