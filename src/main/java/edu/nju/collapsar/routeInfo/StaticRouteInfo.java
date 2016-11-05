package edu.nju.collapsar.routeInfo;

import java.util.List;

/**
 * Created by yifei on 2016/11/4.
 */
public class StaticRouteInfo extends RouteInfo{
    private String filePath = null;
    public StaticRouteInfo(List<String> routes, String jarPath) {
        super(routes, jarPath);
    }
    public StaticRouteInfo(List<String> routes, String filePath, String jarPath) {
        super(routes, jarPath);
        this.setFilePath(filePath);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
