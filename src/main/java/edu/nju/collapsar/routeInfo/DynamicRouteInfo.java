package edu.nju.collapsar.routeInfo;

import java.util.List;

/**
 * Created by yifei on 2016/11/4.
 */
public class DynamicRouteInfo extends RouteInfo{
    private String className = null;
    public DynamicRouteInfo(List<String> routes, String path) {
        super(routes, path);
    }
    public DynamicRouteInfo(List<String> routes, String className, String path) {
        super(routes, path);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
