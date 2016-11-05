package edu.nju.collapsar.routeInfo;

import java.util.List;

/**
 * Created by yifei on 2016/11/4.
 */
public abstract class RouteInfo {
    protected List<String> routes = null;
    protected String jarPath = null;
    public RouteInfo(List<String> routes, String jarPath) {
        this.jarPath = jarPath;
        this.routes = routes;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public List<String> getRoutes() {
        return routes;
    }

    public void setRoutes(List<String> routes) {
        this.routes = routes;
    }
}
