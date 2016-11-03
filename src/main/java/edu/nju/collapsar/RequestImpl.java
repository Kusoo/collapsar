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

    //I will fill the parameters with the url parameters and the post entities
    private Map<String, String> parameters;

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
    public void setHeaders(Map<String, String> map){
        if(null == requestHeaders){
            requestHeaders = new HashMap<String,String>();
        }
        requestHeaders.putAll(map);
    }
    public String getHeader(String key){
        if (requestHeaders == null) return null;
        return requestHeaders.get(key);
    }
    public Map<String, String> getHeaders(){
        return requestHeaders;
    }

    public void setParamters(String key, String value){
        if(null == parameters){
            parameters = new HashMap<String,String>();
        }
        parameters.put(key, value);
    }
    public void setParameters(Map<String, String>map){
        if(null == parameters){
            parameters = new HashMap<String,String>();
        }
        parameters.putAll(map);
    }
    public String getParameter(String key){
        if (parameters == null) return null;
        return parameters.get(key);
    }
    public Map<String, String> getParameters(){
        return parameters;
    }

}
