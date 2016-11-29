package edu.nju.collapsar.util;

import edu.nju.collapsar.routeInfo.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by yifei on 2016/11/4.
 */
public class ConfigManager {
    public enum ServerType{
        BIO,NIO,AIO
    }
    private static int port = 0;
    private static String ipAddress = null;
    private static String serverPath = null;
    private static ServerType serverType = null;
    //TODO 真实发布时该CONFIGPATH应当进行修改
    private final static String CONFIGPATH = System.getProperty("user.dir") + "/src/main/resources/conf/conf.json";
    private static ConfigManager managerInstance = null;
    private ConfigManager() {
        //参数初始化
        JSONReader jsonReader = JSONReader.getJSONReader();
        JSONObject root = jsonReader.getJSONFileContent(CONFIGPATH);
        JSONObject server = root.getJSONObject("server");
        port = server.getInt("port");
        ipAddress = server.getString("ip_address");
        switch (server.getString("server_type")){
            case "BIO":serverType = ServerType.BIO;
                       break;
            case "NIO":serverType = ServerType.NIO;
                       break;
            case "AIO":serverType = ServerType.AIO;
                       break;
            default:serverType = ServerType.BIO;
        }
        JSONArray services = server.getJSONArray("service");
        String[] jarNames = new String[services.size()];
        String[] documentRoots = new String[services.size()];
        for (int i = 0; i < services.size(); i++) {
            JSONObject temp = services.getJSONObject(i);
            jarNames[i] = temp.getString("jar_name");
            documentRoots[i] = temp.getString("document_root");
            if (documentRoots[i].startsWith("./")) {
                documentRoots[i] = documentRoots[i].replaceFirst("./", System.getProperty("user.dir") + "/");
            }
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

    public static String getIPAddress() {
        return ipAddress;
    }

    public static ServerType getServerType() {
        return serverType;
    }
}
