package edu.nju.collapsar.quark;

import edu.nju.collapsar.Request;
import edu.nju.collapsar.Response;

/**
 * Created by LvBQ on 2016/11/2.
 */
public class QuarkImpl implements Quark {
    public void doGet(Request request, Response response) {
        response.write("DoGet");
    }
    
    public void doPost(Request request, Response response) {
        response.write("DoPost");
    }

    public void doPut(Request request, Response response) {
        response.write("DoPut");
    }

    public void doDelete(Request request, Response response) {
        response.write("DoDelete");
    }

}
