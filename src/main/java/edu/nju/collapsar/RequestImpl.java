package edu.nju.collapsar;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rico on 2016/11/2.
 */
public class RequestImpl implements Request{



    private String type = null;
    private String url = null;
    private String httpVersion = null;
    private Map<String, String> requestHeaders;
    private Map<String, String> paramters;

    public void setType(String s){
        this.type = s;
    }
    public String getType(){
        return this.type;
    }
    public void setUrl(String s){
        this.url = s;
    }
    public String getUrl(){
        return this.url;
    }
    public void setHttpVersion(String s){
        this.httpVersion = s;
    }

    public String getHttpVersion(){
        return this.httpVersion;
    }

    public void setHeader(String key, String value){
        if(null == requestHeaders){
            requestHeaders = new HashMap<String,String>();
        }
        requestHeaders.put(key, value);
    }
    public void setHeaders(Map<String, String>map){
        if(null == requestHeaders){
            requestHeaders = new HashMap<String,String>();
        }
        requestHeaders.putAll(map);
    }
    public String getHeader(String key){
        return requestHeaders.get(key);
    }
    public Map<String, String> getHeaders(){
        return requestHeaders;
    }

    public void setParamters(String key, String value){
        if(null == paramters){
            paramters = new HashMap<String,String>();
        }
        paramters.put(key, value);
    }
    public void setParamters(Map<String, String>map){
        if(null == paramters){
            paramters = new HashMap<String,String>();
        }
        paramters.putAll(map);
    }
    public String getParameter(String key){
        return paramters.get(key);
    }
    public Map<String, String> getParameters(){
        return paramters;
    }

}
