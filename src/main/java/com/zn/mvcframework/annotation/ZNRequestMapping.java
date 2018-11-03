package com.zn.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * 创建ZNRequestMapping注解
 *
 * @author zhangna12
 * @date 2018-11-03
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZNRequestMapping {
    String value() default "";
}
