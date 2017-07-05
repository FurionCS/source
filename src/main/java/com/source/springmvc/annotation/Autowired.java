package com.source.springmvc.annotation;

import java.lang.annotation.*;

/**
 * @Description:模拟spring mvc中的Autowired
 * @Author : Mr.Cheng
 * @Date:2017/7/2
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    String value() default "";
}
