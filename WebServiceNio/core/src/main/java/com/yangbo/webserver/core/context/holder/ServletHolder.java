package com.yangbo.webserver.core.context.holder;

import com.yangbo.webserver.core.servlet.Servlet;

/**
 * @Author: yangbo
 * @Date: 2022-03-23-11:11
 * @Description:
 */
public class ServletHolder {
    private Servlet servlet;
    private String servletClass;

    public ServletHolder(String servletClass) {
        this.servletClass = servletClass;
    }
}
