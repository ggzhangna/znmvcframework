package com.zn.demo.mvc.action;

import com.zn.demo.service.IDemoService;
import com.zn.mvcframework.annotation.ZNAutowired;
import com.zn.mvcframework.annotation.ZNController;
import com.zn.mvcframework.annotation.ZNRequestMapping;
import com.zn.mvcframework.annotation.ZNRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhangna12
 * @date 2018-11-02
 */
@ZNController
@ZNRequestMapping("/demo")
public class DemoAction {

    @ZNAutowired
    private IDemoService demoService;

    @ZNRequestMapping("/query.json")
    public void query(HttpServletRequest req, HttpServletResponse resp, @ZNRequestParam("name") String name){
        String result = demoService.get(name);
        try {
            resp.getWriter().write(result);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @ZNRequestMapping("/add.json")
    public void add(HttpServletRequest req, HttpServletResponse resp,
                    @ZNRequestParam("a")Integer a,@ZNRequestParam("b")Integer b){
        try{
            resp.getWriter().write(a + "+" + b + "=" + (a+b));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void remove(HttpServletRequest req, HttpServletResponse resp,
                       @ZNRequestParam("id")Integer id){

    }
}
