package com.source.springmvc.annotation;

import java.lang.annotation.*;

/**
 * @Description:模拟springmvc中 controller
 * @Target表示注解使用的范围
 * @Target(ElementType.TYPE)   //接口、类、枚举、注解
 * @Target(ElementType.FIELD) //字段、枚举的常量
 * @Target(ElementType.METHOD) //方法
 * @Target(ElementType.PARAMETER) //方法参数
 * @Target(ElementType.CONSTRUCTOR)  //构造函数
 * @Target(ElementType.LOCAL_VARIABLE)//局部变量
 * @Target(ElementType.ANNOTATION_TYPE)//注解
 * @Target(ElementType.PACKAGE) ///包
 * @Document：说明该注解将被包含在javadoc中
 * @Retention(RetentionPolicy.RUNTIME)定义的这个注解是注解会在class字节码文件中存在，在运行时可以通过反射获取到。
 * @Author : Mr.Cheng
 * @Date:2017/7/2
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}
