package edu.nju.collapsar.util;

import edu.nju.collapsar.routeInfo.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by yifei on 2016/11/4.
 */
public class ConfigManager {
    private static int port = 0;
    private static String ipAddress = null;
    private static String serverPath = null;
    private final static String CONFIGPATH = System.getProperty("user.dir") + "/src/main/resources/conf/conf.json";
    private static ConfigManager managerInstance = null;
    private ConfigManager() {
        //参数初始化
        JSONReader jsonReader = JSONReader.getJSONReader();
        JSONObject root = jsonReader.getJSONFileContent(CONFIGPATH);
        JSONObject server = root.getJSONObject("server");
        port = server.getInt("port");
        ipAddress = server.getString("ipAddress");
        JSONArray services = server.getJSONArray("service");
        String[] jarNames = new String[services.size()];
        String[] documentRoots = new String[services.size()];
        for (int i = 0; i < services.size(); i++) {
            JSONObject temp = services.getJSONObject(i);
            jarNames[i] = temp.getString("jarName");
            documentRoots[i] = temp.getString("documentRoot");
        }
        //初始化路由文件与routeManager
        RouteManager.init(services.size(), jarNames, documentRoots);
    }

    public static ConfigManager getConfigManager() {
        if (null == managerInstance) {
            managerInstance = new ConfigManager();
        }
        return managerInstance;
    }
    public static void init() {
        managerInstance = new ConfigManager();
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        ConfigManager.port = port;
    }

    public static String getIPAddress() {
        return ipAddress;
    }

    public static void setIPAddress(String ipAddress) {
        ConfigManager.ipAddress = ipAddress;
    }
}
