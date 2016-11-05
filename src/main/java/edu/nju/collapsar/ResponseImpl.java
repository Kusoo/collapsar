package edu.nju.collapsar;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by rico on 2016/11/2.
 */
public class ResponseImpl implements Response{

    private StringBuffer contentBuffer = new StringBuffer("");

    private String httpVersion = null;
    private String statusCode = null;
    private String status = null;
    private Map<String,String> header = null;

    private Map<String,String> statusMap = null;

    private final String BlankLine = "\r\n";

    public ResponseImpl(){
        header = new HashMap<>();
        statusMap = new HashMap<>();
        statusMap.put("100","Continue");
        statusMap.put("101","Switching Protocols");

        statusMap.put("200","OK");
        statusMap.put("201","Created");
        statusMap.put("202","Accepted");
        statusMap.put("203","Non-Authoritative Information");
        statusMap.put("204","No Content");
        statusMap.put("205","Reset Content");
        statusMap.put("206","Partial Content");

        statusMap.put("300","Multiple Choices");
        statusMap.put("301","Moved Permanently");
        statusMap.put("302","Found");
        statusMap.put("303","See Other");
        statusMap.put("304","Not Modified");
        statusMap.put("305","Use Proxy");
        statusMap.put("307","Temporary Redirect");

        statusMap.put("400","Bad Request");
        statusMap.put("401","Unauthorized");
        statusMap.put("402","Payment Required");
        statusMap.put("403","Forbidden");
        statusMap.put("404","Not Found");
        statusMap.put("405","Method Not Allowed");
        statusMap.put("406","Not Acceptable");
        statusMap.put("407","Proxy Authentication Required");
        statusMap.put("408","Request Time-out");
        statusMap.put("409","Conflict");
        statusMap.put("410","Gone");
        statusMap.put("411","Length Required");
        statusMap.put("412","Precondition Failed");
        statusMap.put("413","Request Entity Too Large");
        statusMap.put("414","Request-URI Too Large");
        statusMap.put("415","Unsupported Media Type");
        statusMap.put("416","Requested range not satisfiable");
        statusMap.put("417","Expectation Failed");

        statusMap.put("500","Internal Server Error");
        statusMap.put("501","Not Implemented");
        statusMap.put("502","Bad Gateway");
        statusMap.put("503","Service Unavailable");
        statusMap.put("504","Gateway Time-out");
        statusMap.put("505","HTTP Version not supported");
    }

    public String getHttpVersion(){
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion){
        this.httpVersion = httpVersion;
    }

    public String getStatusCode(){
        return statusCode;
    }

    public void setStatusCode(String statusCode){
        Pattern pattern = Pattern.compile("[1-5]{1}[0-9]{2}");
        if(pattern.matcher(statusCode).matches()){
            this.statusCode = statusCode;
            setStatus(statusMap.get(statusCode));
        }
    }


    private void setStatus(String status){
        this.status = status;
    }

    public Map<String,String> getHeaders(){
        return header;
    }

    public void setHeaders(Map<String,String> map){
        this.header = map;
    }

    public String getHeader(String key){
        if (header == null){ return null;}
        return header.get(key);
    }

    public void setHeader(String key,String value){
        if(header == null){ header = new HashMap<String,String>();}
        header.put(key,value);
    }

    public String generateResponseMessage(){
        StringBuilder builder = new StringBuilder();
        builder.append(httpVersion).append(" ").append(statusCode).append(" ").append(status).append(BlankLine);
        for(Map.Entry entry:header.entrySet()){
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append(BlankLine);
        }
        builder.append(BlankLine);
        builder.append(contentBuffer);
        return builder.toString();
    }

    @Override
    public void write(String content) {
        contentBuffer.append(content);
    }

    @Override
    public String toString(){
        return contentBuffer.toString();
    }

}
