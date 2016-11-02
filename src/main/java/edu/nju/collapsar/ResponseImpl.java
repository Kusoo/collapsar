package edu.nju.collapsar;

/**
 * Created by rico on 2016/11/2.
 */
public class ResponseImpl implements Response{

    private StringBuffer contentBuffer = new StringBuffer("");

    @Override
    public void write(String content) {
        contentBuffer.append(content);
    }

    @Override
    public String toString(){
        return contentBuffer.toString();
    }

}
