package edu.nju.collapsar.invoker;

import edu.nju.collapsar.Request;
import edu.nju.collapsar.Response;

/**
 * Created by LvBQ on 2016/11/2.
 */
public interface Invoker {
    void invoke(Request request , Response response);
}
