package edu.nju.collapsar.quark;

import edu.nju.collapsar.Request;
import edu.nju.collapsar.Response;

/**
 * Created by LvBQ on 2016/11/2.
 */
public class QuarkImpl implements Quark {
    @Override
    public void doGet(Request request, Response response) {
        response.write("DoGet");
    }

    @Override
    public void doPost(Request request, Response response) {
        response.write("DoPost");
    }

    @Override
    public void doPut(Request request, Response response) {
        response.write("DoPut");
    }

    @Override
    public void doDelete(Request request, Response response) {
        response.write("DoDelete");
    }

}
