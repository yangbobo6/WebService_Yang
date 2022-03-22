package com.yangbo.webserver.core.servlet;

import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.response.Response;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-21:57
 * @Description:  servlet 对Request和Response
 */
public interface Servlet {
    public void init();

    public void service(Request request, Response response);
}
