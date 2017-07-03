package com.source.springmvc.servlet;

import com.source.springmvc.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author : Mr.Cheng
 * @Date:2017/7/2
 */

public class DispatcherServlet extends HttpServlet {


    private List<String> classNames=new ArrayList<String>();

    private Map<String,Object> instanceMapping=new HashMap<String,Object>();

    private List<Handler> handlerMapping=new ArrayList<Handler>();
    //private Map<Pattern,Handler> handlerMapping=new HashMap<Pattern, Handler>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      // System.out.println(req.getRequestURI());
        //从上面已经初始化的信息中匹配
        //拿着用户请求的url找到对应的method
        try {
            boolean isMathcer = pattern(req, resp);
            if (!isMathcer) {
                resp.getWriter().write("404 not found");
            }
        }catch (Exception e){
            resp.getWriter().write("500 Exception"+ Arrays.toString(e.getStackTrace()).replaceAll("\\[\\]",""));
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1: 读取配置文件
        String scanPackage=config.getInitParameter("scanPackage");
        //2:扫描指定包路径下的类
        scanClass(scanPackage);
        //3:把这些扫描出来的类进行实例化
        instance();
        //4:建立依赖关系，自动依赖注入
        autowired();
        //5:建立url和method的映射关系（handlerMapping)
        handlerMapping();
        System.out.println("mvc");

    }

    private void scanClass(String scanPackage){
        //拿到包路径，转化为文件路径
        URL url=this.getClass().getClassLoader().getResource("/"+scanPackage.replace("//",","));
        File dir=new File(url.getFile());
        //递归找到所有class文件
        for(File file :dir.listFiles()){
            if(file.isDirectory()){
                scanClass(scanPackage+"."+file.getName());
            }else{
                String className=scanPackage+"."+file.getName().replace(".class","");
                classNames.add(className);
            }
        }

    }

    private void instance(){
        //利用反射机制将扫描到的类名实例化
        if(classNames.size()==0){return;}
        for(String className:classNames){
            try {
                Class<?> clazz = Class.forName(className);
                //找加了@Controller',@Service
                if(clazz.isAnnotationPresent(Controller.class)){
                    String beanName=lowerFirstChar(clazz.getSimpleName());
                    instanceMapping.put(beanName,clazz.newInstance());
                }else if(clazz.isAnnotationPresent(Service.class)){
                    Service service=clazz.getAnnotation(Service.class);
                    String beanName=service.value();
                    if(!"".equals(beanName.trim())){
                        instanceMapping.put(beanName,clazz.newInstance());
                        continue;
                    }
                    //如果自己没有取名字
                    Class<?>[] interfaces=clazz.getInterfaces();
                    for(Class<?> i:interfaces){
                        instanceMapping.put(i.getName(),clazz.newInstance());
                    }
                }else{
                    continue;
                }
            }catch (Exception e){
                continue;
            }
        }
    }

    private  void autowired(){
        if(instanceMapping.isEmpty()){return;}
        for(Map.Entry<String,Object> entry:instanceMapping.entrySet()){
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for(Field field:fields){
                if(!field.isAnnotationPresent(Autowired.class)){continue;}
                Autowired autowired=field.getAnnotation(Autowired.class);
                String beanName=autowired.value().trim();
                if("".equals(beanName)){
                    beanName=field.getType().getName();
                }
                field.setAccessible(true); //如果是私有，设置访问权限

                try {
                    field.set(entry.getValue(),instanceMapping.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handlerMapping(){
        if(instanceMapping.isEmpty()){return;}

        for(Map.Entry<String,Object> entry:instanceMapping.entrySet()){
            Class<?> clazz=entry.getValue().getClass();
            if(!clazz.isAnnotationPresent(Controller.class)){continue;}

            String url="";
            if(clazz.isAnnotationPresent(RequestMapping.class)){
                RequestMapping requestMapping=clazz.getAnnotation(RequestMapping.class);
                url=requestMapping.value();

            }
            Method[] methods=clazz.getMethods();
            for(Method method:methods){
                if(!method.isAnnotationPresent(RequestMapping.class)){ continue;}
                RequestMapping requestMapping=clazz.getAnnotation(RequestMapping.class);
                String regex="/"+url+requestMapping.value();
                regex=regex.replaceAll("/+","/").replaceAll("\\*",".*");

                Map<String,Integer> paramMapping=new HashMap<String, Integer>();
                Annotation [][] pa=method.getParameterAnnotations();
                for(int i = 0;i<pa.length;i++){
                    for(Annotation a:pa[i]){
                        if(a instanceof RequestParam){
                            String paramName=((RequestParam)a).value();
                            if(!"".equals(paramName.trim())){
                                paramMapping.put(paramName,i);
                            }
                        }
                    }
                }

                Class<?>[] paramsTypes=method.getParameterTypes();
                for(int i = 0;i<paramsTypes.length;i++){
                    Class<?> type=paramsTypes[i];
                    if(type==HttpServletRequest.class||
                            type==HttpServletResponse.class){
                        paramMapping.put(type.getName(),i);
                    }
                }
                handlerMapping.add(new Handler(entry.getValue(),method,paramMapping,Pattern.compile(regex)));
            }
        }

    }

    private  boolean pattern(HttpServletRequest req,HttpServletResponse reps) throws  Exception{
        if(handlerMapping.isEmpty()){return false;}
        String url=req.getRequestURI();
        String contextPath=req.getContextPath();
        url.replace(contextPath,"").replaceAll("/+","/");

        for(Handler handler :handlerMapping){
            try{
                Matcher matcher=handler.pattern.matcher(url);
                if(!matcher.matches()){continue;}
                    Class<?> [] paramTyps=handler.method.getParameterTypes();
                    Object[] paramValues=new Object[paramTyps.length];

                    Map<String,String[]> params=req.getParameterMap();
                    for(Map.Entry<String,String[]> param:params.entrySet()){
                       String value=Arrays.toString(param.getValue()).replaceAll("\\]|\\[","").replaceAll(",\\s","");

                        if(!handler.paramMapping.containsKey(param.getKey())){
                            continue;
                        }
                       int index= handler.paramMapping.get(param.getKey());
                        //涉及到类型转化
                        paramValues[index]=castStringValue(value,paramTyps[index]);
                    }
                    int reqIndex=handler.paramMapping.get(HttpServletRequest.class.getName());
                    paramValues[reqIndex]=req;
                    int repsIndex=handler.paramMapping.get(HttpServletResponse.class.getName());
                    paramValues[repsIndex]=reps;
                    //需要对象，方法
                    handler.method.invoke(handler.controller,paramValues);
                    return true;
            }catch (Exception e){
                throw e;
            }
        }
        return false;
    }

    private Object castStringValue(String value,Class<?> clazz){
        if(clazz==String.class){
            return value;
        }else if(clazz==Integer.class){
            return Integer.valueOf(value);
        }else if(clazz==int.class){
            return Integer.valueOf(value).intValue();
        }else{
            return null;
        }

    }
    public String lowerFirstChar(String str){
        char[] chars=str.toCharArray();
        chars[0]+=32;
        return String.valueOf(chars);
    }

    private class Handler{
        protected  Pattern pattern;
        protected  Object controller;
        protected Method method;
        protected Map<String,Integer> paramMapping;

        protected Handler(Object controller,Method method,Map<String,Integer> map,Pattern pattern ){
            this.controller=controller;
            this.method=method;
            this.paramMapping=map;
            this.pattern=pattern;
        }
    }
}
