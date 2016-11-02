package edu.nju.collapsar;

import java.util.Map;

/**
 * Created by rico on 2016/11/2.
 */
public interface Request {
    String getType();

    String getUrl();

    String getHttpVersion();

    String getHeader(String key);

    Map<String,String> getHeaders();

    String getParameter(String key);

    Map<String,String> getParameters();

}
