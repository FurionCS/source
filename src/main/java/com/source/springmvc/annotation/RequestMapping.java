package com.source.springmvc.annotation;

import java.lang.annotation.*;

/**
 * @Description:${Description}
 * @Author : Mr.Cheng
 * @Date:2017/7/2
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default "";
}
