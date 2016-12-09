package edu.nju.collapsar.util;

import edu.nju.collapsar.Response;

import java.util.Date;

/**
 * Created by rico on 2016/11/5.
 */
public class ResponseHelper {
    public static void setHttpVersion(Response response){
        response.setHttpVersion("HTTP/1.1");
    }

    public static void setStatus200(Response response){
        response.setStatusCode("200");
    }

    public static void setStatus404(Response response) {
        response.setStatusCode("404");
    }

    public static void setDefaultHeader(Response response){
        response.setHeader("Date",new Date().toString());
        response.setHeader("Server","Collapsar1.0");
        response.setHeader("Connection","Keep-Alive");
        response.setHeader("Content-Type","text/html");
    }

    public static void quickSet200(Response response){
        setHttpVersion(response);
        setStatus200(response);
        setDefaultHeader(response);
    }

    public static void quickSet404(Response response){
        setHttpVersion(response);
        setStatus404(response);
        setDefaultHeader(response);
        response.write("Not Found!");
    }
}
