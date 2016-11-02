package edu.nju.collapsar;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rico on 2016/11/2.
 */
public class RequestParser {
    public static RequestImpl parse(String netInput){
        RequestImpl request = new RequestImpl();

        if(netInput==null||netInput=="") return request;

        String[] lines = netInput.split("\r");
        String[] blocks = lines[0].split(" ");
        request.setType(blocks[0]);
        request.setUrl(blocks[1]);
        request.setHttpVersion(blocks[2]);

        if(request.getType().equalsIgnoreCase("GET")){
            Map<String,String> headers = new HashMap<String, String>();
            for(int i = 1,n = lines.length;i < n;i++){
                String[] head = lines[i].split(":",2);
                headers.put(head[0],head[1]);
            }
            request.setHeaders(headers);
        } else if(request.getType().equalsIgnoreCase("POST")){

        }

        return request;
    }
}
