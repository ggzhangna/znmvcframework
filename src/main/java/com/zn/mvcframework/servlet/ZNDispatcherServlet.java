package com.zn.mvcframework.servlet;

import com.zn.mvcframework.annotation.ZNAutowired;
import com.zn.mvcframework.annotation.ZNController;
import com.zn.mvcframework.annotation.ZNRequestMapping;
import com.zn.mvcframework.annotation.ZNService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
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

        //1、加载配置文件
        doLoadConfig(config.getInitParameter(LOCATION));

        //2、扫描所有相关的类
        doScanner(p.getProperty("scanPackage"));

        //3、初始化所有相关类的实例，并保存到IOC容器中
        doInstance();

        //4、依赖注入
        doAutowired();

        //5、构造HandlerMapping
        initHandlerMapping();

        //6、提示信息
        System.out.println("zn mvcframework is init");

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException , IOException{
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException , IOException{
        try{
            doDispatch(req,resp);
        }catch (Exception e){
            resp.getWriter().write("500 Exception, Detail:\r\n" + Arrays.toString(e.getStackTrace()).
                    replaceAll("\\[|\\]]","").replaceAll(",\\s","\r\n"));
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        if(this.handlerMapping.isEmpty()){
            return;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");

        if(!this.handlerMapping.containsKey(url)){
            resp.getWriter().write("404 Not Found!");
            return;
        }

        Map<String,String[]> params = req.getParameterMap();
        Method method = this.handlerMapping.get(url);
        //获取方法的参数列表
        Class<?>[] parameterTypes = method.getParameterTypes();
        //获取请求的参数
        Map<String,String[]> parameterMap = req.getParameterMap();
        //保存参数值
        Object[] paramValues = new Object[parameterTypes.length];
        //方法的参数列表
        for(int i=0;i<parameterTypes.length;i++){
            //根据参数名称，做某些处理
            Class parameterType = parameterTypes[i];
            if(parameterType == HttpServletRequest.class){
                //参数类型已明确，强转类型
                paramValues[i] = req;
                continue;
            }else if(parameterType == HttpServletResponse.class){
                paramValues[i] = resp;
                continue;
            }else if(parameterType == String.class){
                for(Map.Entry<String,String[]> param : parameterMap.entrySet()){
                    String value = Arrays.toString(param.getValue())
                            .replaceAll("\\[|\\]","")
                            .replaceAll(",\\s",",");
                    paramValues[i] = value;
                }
            }

            try{
                String beanName = lowerFirstCase(method.getDeclaringClass().getSimpleName());
                //利用反射机制调用
                method.invoke(this.ioc.get(beanName),paramValues);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 将文件读取到Properties对象当中
     * @param location
     */
    private void doLoadConfig(String location){
        InputStream fis = null;
        try{
            fis = this.getClass().getClassLoader().getResourceAsStream(location);
            //1、读取配置文件
            p.load(fis);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(null != fis){
                    fis.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 递归扫描出所有的Class文件
     * @param packageName
     */
    private void doScanner(String packageName){
        //将所有的包路径转换为文件路径
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.","/"));
        File dir = new File(url.getFile());
        for(File file : dir.listFiles()){
            //如果是文件夹，继续递归
            if(file.isDirectory()){
                doScanner(packageName+"."+file.getName());
            }else {
                classNames.add(packageName+"."+file.getName().replace(".class","").trim());
            }
        }
    }

    /**
     * 首字母小写
     * IOC容器的key默认是类名首字母小写，如果自己设置类名，则优先使用自定义的
     * @param str
     * @return
     */
    private String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 初始化所有相关的类，并放到IOC容器中。
     */
    private void doInstance(){
        if(classNames.size() == 0){
            return;
        }
        try{
            for(String className : classNames){
                Class<?> clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(ZNController.class)){
                    //默认将首字母小写作为beanName
                    String beanName = lowerFirstCase(clazz.getSimpleName());
                    ioc.put(beanName, clazz.newInstance());
                }else if(clazz.isAnnotationPresent(ZNService.class)){
                    ZNService service = clazz.getAnnotation(ZNService.class);
                    String beanName = service.value();
                    //如果用户设置了名字，就用用户自己设置
                    if(!"".equals(beanName.trim())){
                        ioc.put(beanName,clazz.newInstance());
                        continue;
                    }
                    //如果自己没设，就按接口类型创建一个实例
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for(Class<?> i : interfaces){
                        ioc.put(i.getName(), clazz.newInstance());
                    }
                }else {
                    continue;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 将初始化到IOC容器中的类，需要赋值的字段进行赋值
     */
    private void doAutowired(){
        if(ioc.isEmpty()){
            return;
        }
        for(Map.Entry<String,Object> entry : ioc.entrySet()){
            //拿到实例对象中的所有属性
            Field [] fields = entry.getValue().getClass().getDeclaredFields();
            for(Field field : fields){
                if(!field.isAnnotationPresent(ZNAutowired.class)){
                    continue;
                }
                ZNAutowired autowired = field.getAnnotation(ZNAutowired.class);
                String beanName = autowired.value().trim();
                if("".equals(beanName)){
                    beanName = field.getType().getName();
                }
                field.setAccessible(true); //设置私有属性的访问权限
                try{
                    field.set(entry.getValue(),ioc.get(beanName));
                }catch (Exception e){
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    /**
     * 将ZNRequestMapping中配置的信息和Method进行关联，并保存这些关系
     */
    private void initHandlerMapping(){
        if(ioc.isEmpty()){
            return;
        }
        for(Map.Entry<String,Object> entry : ioc.entrySet()){
            Class<?> clazz = entry.getValue().getClass();
            if(!clazz.isAnnotationPresent(ZNController.class)){
                continue;
            }
            String baseUrl = "";
            //获取Controller的url配置
            if(clazz.isAnnotationPresent(ZNRequestMapping.class)){
                ZNRequestMapping requestMapping = clazz.getAnnotation(ZNRequestMapping.class);
                baseUrl = requestMapping.value();
            }

            //获取Method的url配置
            Method [] methods = clazz.getMethods();
            for(Method method : methods){
                //没有加RequestMapping注解的直接忽略
                if(!method.isAnnotationPresent(ZNRequestMapping.class)){
                    continue;
                }
                //映射url
                ZNRequestMapping requestMapping = method.getAnnotation(ZNRequestMapping.class);
                String url = ("/"+baseUrl+"/"+requestMapping.value()).replaceAll("/+","/");
                handlerMapping.put(url,method);
                System.out.println("mapped " + url + "," + method);
            }








        }




    }




}
