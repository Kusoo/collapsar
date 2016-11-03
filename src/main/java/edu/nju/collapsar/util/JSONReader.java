package edu.nju.collapsar.util; /**
 * Created by yifei on 2016/11/2.
 */

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Iterator;

public class JSONReader {
    private final static String PROJECT_PATH = System.getProperty("user.dir");
    private final static String CONF_PATH = "/main/resources/conf/";
    private final static String CONF_FILE_NAME = "conf.json";
    public final static String CONF_FILE_PATH = PROJECT_PATH + CONF_PATH + CONF_FILE_NAME;//暴露给外部使用
    private static JSONReader instance = null;
    private final static int DEFAULT_SPACE = 100;
    private static File file = null;
    private static String content = null;
    private static CharBuffer cb = null;
    private static FileReader fr = null;
    private JSONReader() {}
    public static JSONReader getJSONReader() {
        if (null == instance) {
            instance = new JSONReader();
        }
        return instance;
    }

    public JSONObject getJSONFileContent(String fileName) {
        file = new File(fileName);
        Long filelength = file.length() + DEFAULT_SPACE;
        cb = CharBuffer.allocate(filelength.intValue());
        try {
            fr = new FileReader(file);
            fr.read(cb);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("配置文件没有找到！");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("配置文件读取失败！");
        }
        cb.flip();
        content = cb.toString();
        return JSONObject.fromObject(content);
    }
    public static void main(String[] arg) {
        JSONReader reader = JSONReader.getJSONReader();
        JSONObject root = reader.getJSONFileContent(CONF_FILE_PATH);
        JSONObject dynamicLoader = root.getJSONObject("dynamicLoader");
        JSONArray loadActions = dynamicLoader.getJSONArray("loadActions");
        Iterator it = loadActions.iterator();
        while (it.hasNext()) {
            JSONObject temp = (JSONObject) it.next();
            System.out.println(temp.getString("action") + temp.getString("name") + temp.getString("path"));
        }
    }


}
