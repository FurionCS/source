package com.source.springmvc.annotation;

import java.lang.annotation.*;

/**
 * @Description:${Description}
 * @Author : Mr.Cheng
 * @Date:2017/7/2
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";
    boolean required() default true;
}
