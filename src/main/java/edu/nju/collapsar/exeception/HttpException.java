package edu.nju.collapsar.exeception;

/**
 * Created by rico on 2016/11/29.
 */
public class HttpException extends Exception{
    private static final long serialVersionUID = 1L;

    private String statusCode;

    public HttpException(String code,String message){
        super(message);
        this.statusCode = code;
    }

}
