package com.zn.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * 创建ZNAutowired注解
 *
 * @author zhangna12
 * @date 2018-11-03
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZNAutowired {
    String value() default "";
}
