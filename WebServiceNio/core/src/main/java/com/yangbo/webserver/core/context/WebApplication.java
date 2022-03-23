package com.yangbo.webserver.core.context;

/**
 * @Author: yangbo
 * @Date: 2022-03-23-18:35
 * @Description: 静态持有servletContext，保持servletContext能在项目启动时就被初始化
 */
public class WebApplication {
    private static ServletContext servletContext;

    static {
        try {
            servletContext = new ServletContext();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static ServletContext getServletContext(){
        return servletContext;
    }
}
