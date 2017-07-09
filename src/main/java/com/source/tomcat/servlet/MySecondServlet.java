package com.source.tomcat.servlet;

import com.source.tomcat.http.Request;
import com.source.tomcat.http.Response;
import com.source.tomcat.http.Servlet;

/**
 * @Description:
 * @Author : Mr.Cheng
 * @Date:2017/7/9
 */

public class MySecondServlet extends Servlet{
    @Override
    public void doGet(Request request, Response response) throws Exception {
        response.write("this is my second GetSerlvet");
    }

    @Override
    public void doPost(Request request, Response response) throws Exception {
        response.write("this is my second PostSerlvet");
    }
}
