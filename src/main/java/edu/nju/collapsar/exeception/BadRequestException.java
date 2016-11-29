package edu.nju.collapsar.exeception;

/**
 * Created by rico on 2016/11/28.
 */
public class BadRequestException extends HttpException{

    public BadRequestException(String code,String message){
        super(code,message);
    }
}
