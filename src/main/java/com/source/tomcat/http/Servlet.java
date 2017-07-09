package com.source.tomcat.http;

import com.sun.org.apache.regexp.internal.RE;

/**
 * @Description:
 * @Author : Mr.Cheng
 * @Date:2017/7/9
 */

public abstract  class Servlet {

    public void service(Request request,Response response) throws  Exception{
        if("GET".equalsIgnoreCase(request.getMethod())){
            doGet(request,response);
        }else if("POST".equalsIgnoreCase(request.getMethod())){
            doPost(request,response);
        }
    }

    public abstract void doGet(Request request,Response response) throws  Exception;

    public abstract  void  doPost(Request request,Response response) throws Exception;
}
