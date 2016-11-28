package edu.nju.collapsar.exeception;

/**
 * Created by rico on 2016/11/28.
 */
public class BadRequestException extends Exception{
    private static final long serialVersionUID = 574308401L;

    public BadRequestException(String s){
        super(s);
    }
}
