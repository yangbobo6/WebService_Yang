package com.yangbo.webserver.core.context.holder;

import com.yangbo.webserver.core.servlet.Servlet;
import lombok.Data;

/**
 * @Author: yangbo
 * @Date: 2022-03-23-11:11
 * @Description:
 */
@Data
public class ServletHolder {
    private Servlet servlet;
    private String servletClass;

    public ServletHolder(String servletClass) {
        this.servletClass = servletClass;
    }
}
