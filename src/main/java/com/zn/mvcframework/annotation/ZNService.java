package com.zn.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * 创建ZNService注解
 *
 * @author zhangna12
 * @date 2018-11-03
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZNService {
    String value() default "";
}
