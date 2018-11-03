package com.zn.demo.service.impl;

import com.zn.demo.service.IDemoService;

/**
 * 请填写类的描述
 *
 * @author zhangna12
 * @date 2018-11-03
 */
public class DemoService implements IDemoService {
    public String get(String name) {
        return "My name is " + name;
    }
}
