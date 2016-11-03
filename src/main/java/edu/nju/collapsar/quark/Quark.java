package edu.nju.collapsar.quark;

import edu.nju.collapsar.Request;
import edu.nju.collapsar.Response;

/**
 * Created by LvBQ on 2016/11/2.
 */
public interface Quark {

    void doGet(Request request, Response response);

    void doPost(Request request, Response response);

    void doPut(Request request, Response response);

    void doDelete(Request request, Response response);

}
