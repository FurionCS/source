package com.source.tomcat.http;

import java.io.OutputStream;

/**
 * @Description:
 * @Author : Mr.Cheng
 * @Date:2017/7/9
 */
public class Response {


    private OutputStream out;
    public  Response(OutputStream out){
        this.out=out;
    }

    public void write(String outStr) throws Exception{
        out.write(outStr.getBytes());
        out.flush();
    }

}
