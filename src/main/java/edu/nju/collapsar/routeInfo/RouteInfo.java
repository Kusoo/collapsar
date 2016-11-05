package edu.nju.collapsar.routeInfo;

import java.util.List;

/**
 * Created by yifei on 2016/11/4.
 */
public abstract class RouteInfo {
    protected List<String> routes = null;
    protected String path = null;
    public RouteInfo(List<String> routes, String path) {
        this.path = path;
        this.routes = routes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getRoutes() {
        return routes;
    }

    public void setRoutes(List<String> routes) {
        this.routes = routes;
    }
}
