package edu.nju.collapsar.util;

import edu.nju.collapsar.RequestImpl;
import edu.nju.collapsar.exeception.BadRequestException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rico on 2016/11/2.
 */
public class RequestParser {

    private RequestParser(){

    }

    public static RequestImpl parse(String netInput){
        RequestImpl request = new RequestImpl();

        if(netInput==null||netInput.equals("")) return request;

        String[] lines = netInput.split("\r");
        String[] pieces = lines[0].split(" ");
        request.setType(pieces[0].toUpperCase());
        request.setUrl(pieces[1]);
        request.setHttpVersion(pieces[2].toUpperCase());

        if(request.getType().equalsIgnoreCase("GET")){
            extractHeaders(request,lines);
            fillParametersWithUrl(request);
        } else if(request.getType().equalsIgnoreCase("POST")){
            extractHeaders(request,lines);
            fillParametersWithUrl(request);
            String contentType = request.getHeader("Content-Type");
            if(contentType.contains("application/x-www-form-urlencoded")){
                int blankIndex = getBlankIndex(lines);
                String paramStr = lines[blankIndex+1];
                String[] params = paramStr.split("&");
                fillParametersWithUrlPiece(request,params);
            } else if(contentType.contains("multipart/form-data")){
                String boundary = "--"+contentType.split("boundary=")[1];
                String[] blocks = netInput.split(boundary);
                for(int i = 1,n = blocks.length;i < n - 1;i++){
                    String[] inblock = blocks[i].split("\r");
                    int blankIndex = 0;
                    String key = null;
                    for(int j = 0,m= inblock.length;j < m;j++){
                        if(inblock[j].contains("Content-Disposition")){
                            String[] infos  = inblock[j].split(":")[1].split(";");
                            for(int k = 0,u = infos.length;k < u;k++){
                                if(infos[k].contains("=")){
                                    if(infos[k].split("=")[0].equals("name")){
                                        key = infos[k].split("=")[1].replaceAll("\"","");
                                    }
                                }
                                if("\n".equals(infos[k])){
                                    blankIndex = k;
                                }
                            }
                        }
                    }
                    request.setParamters(key,inblock[blankIndex+1]);
                }

            } else if(contentType.contains("application/json")){
                int blankIndex = getBlankIndex(lines);
                request.setParamters("json",lines[blankIndex+1]);
            } else if(contentType.contains("text/xml")){
                int blankIndex = getBlankIndex(lines);
                StringBuilder builder = new StringBuilder();
                for(int i = blankIndex + 1,n = lines.length;i < n;i++){
                    builder.append(lines[i]);
                }
                request.setParamters("xml",builder.toString());
            }
        }

        return request;
    }

    private static int getBlankIndex(String[] lines){
        int blankIndex = 0;
        for(int i = 0,n = lines.length;i < n;i++){
            if("\n".equals(lines[i])){
                blankIndex = i;
                break;
            }
        }
        return blankIndex;
    }

    private static void extractHeaders(RequestImpl request,String[] lines){
        Map<String,String> headers = new HashMap<String, String>();
        for(int i = 1,n = lines.length;i < n&&!lines[i].equals("\n");i++){
            String[] head = lines[i].split(":",2);
            headers.put(head[0].trim(),head[1].trim());
        }
        request.setHeaders(headers);
    }

    private static void fillParametersWithUrl(RequestImpl request){
        String url = urlDecode(request.getUrl(),"UTF-8");
        if(url == null || url.equals("") || !url.contains("?")) return;
        String paramStr = url.split("\\u003F")[1];                              //split with '?'
        if(paramStr == null || paramStr.equals("")) return;
        String[] params = paramStr.split("&");
        fillParametersWithUrlPiece(request,params);
    }

    private static void fillParametersWithUrlPiece(RequestImpl request,String[] pairs){
        Map<String,String> urlParams = new HashMap<String, String>();
        for(String param:pairs){
            if(param != null && !param.equals("") && param.contains("=")){
                String[] pair = param.split("=");
                urlParams.put(pair[0].trim(),pair[1].trim());
            }
        }
        request.setParameters(urlParams);
    }

    private static String urlDecode(String s,String code){
        String url;
        try {
            url = URLDecoder.decode(s,code);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            url = "";
        }
        return url;
    }
}
