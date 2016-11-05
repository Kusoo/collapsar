package edu.nju.collapsar;

/**
 * Created by rico on 2016/11/2.
 */
public interface Response {
    void write(String content);

    void setHeader(String key,String value);

    void setHttpVersion(String httpVersion);

    void setStatusCode(String code);

    public String generateResponseMessage();
}
