package com.source.springmvc.annotation;

import java.lang.annotation.*;

/**
 * @Description:${Description}
 * @Author : Mr.Cheng
 * @Date:2017/7/2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}
