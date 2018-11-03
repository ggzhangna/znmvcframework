package com.zn.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * 创建ZNController注解
 *
 * @author zhangna12
 * @date 2018-11-02
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZNController {
    String value() default "";
}
