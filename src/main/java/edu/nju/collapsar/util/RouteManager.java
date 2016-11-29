package edu.nju.collapsar.util; /**
 * Created by yifei on 2016/11/4.
 */
import edu.nju.collapsar.routeInfo.DynamicRouteInfo;
import edu.nju.collapsar.routeInfo.RouteInfo;
import edu.nju.collapsar.routeInfo.StaticRouteInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RouteManager {
    private static int webappNum = 0;
    private static boolean ruleState = true;
    private  String baseRoute = null;
    private  String jarPath = null;
    private  String documentRoot = null;
    private  List<RouteInfo> staticRouteMap = null;
    private  List<RouteInfo> dynamicRouteMap = null;
    private static List<RouteManager> managerInstances = null;
    private final static String RESOURCESPATH = "resources/";//自动加在filePath前的前缀
    public static void init(int num, String[] jarNames, String[] documentRoots) {
        webappNum = num;
        managerInstances = new ArrayList<RouteManager>();
        RouteManager newManager = null;
        for (int i = 0; i < num; i++) {
            newManager = new RouteManager(documentRoots[i], jarNames[i]);
            managerInstances.add(newManager);
        }
    }
    private RouteManager(String documentRoot, String jarName) {
        if ((null != documentRoot) && (!"".equals(documentRoot))) {
            if (documentRoot.endsWith("/")) {
                documentRoot = documentRoot.substring(0, documentRoot.length() - 1);
            }
        }
        this.documentRoot = documentRoot;
        String routeConfigPath = documentRoot + "/route.json";
        JSONReader jsonReader = JSONReader.getJSONReader();
        JSONObject root = jsonReader.getJSONFileContent(routeConfigPath);
        JSONObject rules = root.getJSONObject("rules");
        String stateStr = rules.getString("ruleState");
        if ((null != stateStr) && ("on".equals(stateStr))) {
            ruleState = true;
        } else if ((null != stateStr) && ("off".equals(stateStr))) {
            ruleState = false;
        }
        this.jarPath = documentRoot + "/" + jarName;
        try {
            baseRoute = rules.getString("baseRoute");
        } catch (Exception e) {
            e.printStackTrace();
            baseRoute = null;
        }
        if ((null != baseRoute) && (!"".equals(baseRoute))) {
            if (!baseRoute.startsWith("/")) {
                baseRoute = "/" + baseRoute;
            }
            if (baseRoute.endsWith("/")) {
                baseRoute = baseRoute.substring(0, baseRoute.length() - 1);
            }
        }

        JSONArray staticRoutes = root.getJSONArray("staticRouteRules");
        Iterator<JSONObject> it = staticRoutes.iterator();
        RouteInfo routeInfo = null;
        String filePath = null;
        List<String> routes = null;
        staticRouteMap = new ArrayList<RouteInfo>();
        while(it.hasNext()) {
            JSONObject temp = it.next();
            JSONArray jsonRoutes = temp.getJSONArray("staticRoutes");
            String route = null;
            routes = new ArrayList<String>();
            for (int i = 0; i < jsonRoutes.size(); i++) {
                route = jsonRoutes.getString(i);
                if (route.startsWith("./")) {
                    route = route.replaceFirst("./", baseRoute + "/");
                }
                if (!route.startsWith("/")) {
                    route ="/" + route;
                }
                routes.add(route);
            }

            filePath = temp.getString("filepath");
            if (filePath.startsWith("/")) {
                filePath = filePath.substring(1);
            }
            filePath = RESOURCESPATH + filePath;
            routeInfo = new StaticRouteInfo(routes, filePath, jarPath);
            staticRouteMap.add(routeInfo);
        }
        JSONArray dynamicActions = root.getJSONArray("dynamicActions");
        routeInfo = null;
        String classname = null;
        routes = null;
        dynamicRouteMap = new ArrayList<RouteInfo>();
        it = dynamicActions.iterator();
        while(it.hasNext()) {
            JSONObject temp = it.next();
            JSONArray jsonRoutes = temp.getJSONArray("dynamicRoutes");
            String route = null;
            routes = new ArrayList<String>();
            for (int i = 0; i < jsonRoutes.size(); i++) {
                route = jsonRoutes.getString(i);
                if (route.startsWith("./")) {
                    route = route.replaceFirst("./", baseRoute + "/");
                }
                if (!route.startsWith("/")) {
                    route = "/" + route;
                }
                routes.add(route);
            }

            classname = temp.getString("className");
            routeInfo = new DynamicRouteInfo(routes, classname, jarPath);
            staticRouteMap.add(routeInfo);
        }

    }

    private RouteInfo defaultRouting(String url) {
        String filePath = url;
        if((null != baseRoute) && (filePath.startsWith(baseRoute))) {
            if ((filePath.length() > baseRoute.length()) && ('/' == filePath.charAt(baseRoute.length()))) {
                filePath = filePath.substring(baseRoute.length());
            }
        }
        List<String> routes = new ArrayList<String>();
        routes.add(filePath);
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        filePath = RESOURCESPATH + filePath;//默认为静态文件
        RouteInfo routeResult = new StaticRouteInfo(routes, filePath, jarPath);
        return routeResult;
    }

    private RouteInfo routingUsingRules(String url) {
        RouteInfo routeResult = null;
        RouteInfo temp;
        Iterator<RouteInfo> it = staticRouteMap.iterator();
        if (null != url) {
            while (it.hasNext()) {
                temp = it.next();
                List<String> routes = temp.getRoutes();
                if (routes.contains(url)) {
                    routeResult = temp;
                    break;
                }
            }
            if (null == routeResult) {
                it = dynamicRouteMap.iterator();
                while (it.hasNext()) {
                    temp = it.next();
                    List<String> routes = temp.getRoutes();
                    if (routes.contains(url)) {
                        routeResult = temp;
                        break;
                    }
                }
            }
        }
//        if (null == routeResult) {
//            routeResult = defaultRouting(url);
//        }
        return routeResult;
    }

    //唯一对外接口，给出静态或动态的路由信息
    //可能结果：
    //null:表示路由未找到
    //DynamicRouteInfo类型:表示是动态路由，jar包地址，
    public static RouteInfo getRouting(String url) {
        //去掉url中的参数?部分
        int index = url.indexOf('?');
        if (index >= 0) {
            url = url.substring(0, index);
        }
        String[] parts = url.split("/");
        String hint = null;
        if (parts.length >= 2) {
            hint = "/" + parts[1];
        }

        RouteInfo resultRouteInfo = null;
        int specialNum = -1;//用于保存该url符合哪一个路由的basePath
        for (int i = 0; i < managerInstances.size(); i++) {
            RouteManager manger = managerInstances.get(i);
            if (hint.equalsIgnoreCase(manger.baseRoute)) {
                specialNum = i;
                resultRouteInfo = manger.routingUsingRules(url);
                break;
            }
        }
        if (null == resultRouteInfo) {
            for (int i = 0; i < managerInstances.size(); i++) {
                if (specialNum != i) {
                    RouteManager manger = managerInstances.get(i);
                    resultRouteInfo = manger.routingUsingRules(url);
                    if (null != resultRouteInfo) {
                        break;
                    }
                }
            }
        }

        //这里针对静态文件进行默认寻址，用是否含有“.”来确定是否为静态文件
        if ((null == resultRouteInfo) && (specialNum >= 0) && (url.contains("."))) {
            resultRouteInfo = managerInstances.get(specialNum).defaultRouting(url);
        }

        return resultRouteInfo;
    }
}
