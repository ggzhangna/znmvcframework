package com.zn.mvcframework.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 启动入口
 *
 * @author zhangna12
 * @date 2018-11-02
 */
public class ZNDispatcherServlet extends HttpServlet {

    private static final long serialVerisonUID = 1L;

    //跟web.xml中的param-name的值一致
    private static final String LOCATION = "contextConfigLocation";

    //保存所有配置信息
    private Properties p = new Properties();

    //保存所有被扫描到的相关的类名
    private List<String> classNames = new ArrayList<String>();

    //核心IOC容器，保存所有初始化的Bean
    private Map<String, Object> ioc  = new HashMap<String, Object>();

    //保存所有的url和方法的映射关系
    private Map<String, Method> handlerMapping = new HashMap<String, Method>();

    public ZNDispatcherServlet(){
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException{

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException , IOException{
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException , IOException{

    }

}
