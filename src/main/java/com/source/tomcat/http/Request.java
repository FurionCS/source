package com.source.tomcat.http;

import jdk.internal.util.xml.impl.Input;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @Description:request 对象
 * @Author : Mr.Cheng
 * @Date:2017/7/9
 */

public class Request {

    private String method;

    private String url;

    private String params;

    private InputStream in;
    public Request(InputStream in){
        this.in=in;
        try {
            String content="";
            byte [] buff=new byte[1024];
            int len=0;
            if((len=in.read(buff))>0){
                content=new String(buff,0,len);
            }
            String line=content.split("\n")[0];
            String[] arr=line.split("\\s");
            this.method=arr[0];
            String [] urls=arr[1].split("\\?");
            this.url=urls[0];
            if(urls.length>1) {
                this.params =urls[1];
            }
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getMethod(){
        return this.method;
    }

    public Map<String,String> getParameters(){
        //分析this.params 封装成一个对象
        return null;
    }

    public String getUrl(){
        return this.url;
    }



}
