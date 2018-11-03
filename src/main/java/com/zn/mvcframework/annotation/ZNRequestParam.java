package com.zn.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * 创建ZNRequestParam注解
 *
 * @author zhangna12
 * @date 2018-11-03
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZNRequestParam {
    String value() default "";
}
