package com.yangbo.webserver.core.servlet.impl;

import com.yangbo.webserver.core.enumeration.RequestMethod;
import com.yangbo.webserver.core.exception.base.ServletException;
import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.response.Response;
import com.yangbo.webserver.core.servlet.Servlet;

import java.io.IOException;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-21:59
 * @Description: 如果当前url没有匹配任何的servlet，就会调用默认Servlet，它可以处理静态资源
 */
public class DefaultServlet extends HttpServlet {
    @Override
    public void service(Request request, Response response) throws ServletException, IOException {
        if(request.getMethod()== RequestMethod.GET){
            //定位到首页
            if(request.getUrl().equals("/")){
                request.setUrl("/index.html");
            }
            request.getRequestDispatcher(request.getUrl()).forward(request,response);
        }
    }
}
