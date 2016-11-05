package edu.nju.collapsar.util; /**
 * Created by yifei on 2016/11/2.
 */

import net.sf.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;

public class JSONReader {
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


}
