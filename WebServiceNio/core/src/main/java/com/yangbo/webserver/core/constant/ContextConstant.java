package com.yangbo.webserver.core.constant;

/**
 * @Author: yangbo
 * @Date: 2022-03-23-18:48
 * @Description:
 */
public class ContextConstant {
    public static final String ERROR_PAGE = "/errors/%s.html";
    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    public static final String DEFAULT_SERVLET_ALIAS = "DefaultServlet";
    /**
     * 300s 5min 过期
     */
    public static final int DEFAULT_SESSION_EXPIRE_TIME = 300;
}
