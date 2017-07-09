package com.source.tomcat.catalina;

import com.source.tomcat.http.Request;
import com.source.tomcat.http.Response;
import com.source.tomcat.http.Servlet;
import com.source.tomcat.servlet.MyServlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author : Mr.Cheng
 * @Date:2017/7/8
 */

public class Tomcat {

    private int port=8080;

    private Properties webXml=new Properties();

    Map<Pattern,Class<?>> serlvetMap=new HashMap<Pattern,Class<?>>();
    public Tomcat(){}

    public Tomcat(int port){
        this.port=port;
    }

    public void start() throws  Exception{
        try {
            FileInputStream fis=new FileInputStream(this.getClass().getClassLoader().getResource("").getPath()+"web.properties");

            webXml.load(fis);
            fis.close();

            for(Object k:webXml.keySet()){
                String key=k.toString();
                if(key.endsWith(".url")){
                   String name=key.replaceAll("\\.url","");
                    String url=webXml.getProperty(key).toString();
                    String className=webXml.getProperty(name+".className").toString();
                    String urlPattern=url.replaceAll("\\.","\\\\.").replaceAll("\\*",".*");
                    Pattern pattern=Pattern.compile(urlPattern);
                    serlvetMap.put(pattern,Class.forName(className));
                }
            }
            ServerSocket server=new ServerSocket(this.port);
            System.out.println("tomcat 启动成功，端口号为："+this.port);
            while (true) {
                Socket client = server.accept();  //阻塞

                // Servlet 和 Request . Response
                InputStream in=client.getInputStream();
                OutputStream out=client.getOutputStream();
                Request request=new Request(in);
                Response response=new Response(out);

                //Tomcat 初始化前，myServlet 没有加载
                String url=request.getUrl();

                try {
                    boolean isPattern = false;
                    for (Map.Entry<Pattern, Class<?>> entry : serlvetMap.entrySet()) {
                        if (entry.getKey().matcher(url).matches()) {
                            Servlet servlet = (Servlet) entry.getValue().newInstance();
                            servlet.service(request, response);
                            isPattern = true;
                            break;
                        }
                    }
                        if (!isPattern) {
                            response.write("404 not fouund");
                        }
                }catch (Exception e){
                    response.write("500 \n"+e.getMessage()+"\n"+Arrays.toString(e.getStackTrace()));
                }
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws  Exception{
        Tomcat tomcat=new Tomcat(8080);
        tomcat.start();
    }



}


















