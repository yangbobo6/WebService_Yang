package com.yangbo.webserver.core.listener.event;


import com.yangbo.webserver.core.context.ServletContext;
import com.yangbo.webserver.core.request.Request;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-22:41
 * @Description:  request相关事件
 */
public class ServletRequestEvent extends java.util.EventObject{
    private static final long serialVersionUID = -7467864054698729101L;

    private final transient Request request;


    public ServletRequestEvent(ServletContext sc, Request request) {
        super(sc);
        this.request = request;
    }


    public Request getServletRequest () {
        return this.request;
    }


    public ServletContext getServletContext () {
        return (ServletContext) super.getSource();
    }

}
