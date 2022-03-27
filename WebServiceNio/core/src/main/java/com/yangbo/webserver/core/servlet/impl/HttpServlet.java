package com.yangbo.webserver.core.servlet.impl;

import com.yangbo.webserver.core.enumeration.RequestMethod;
import com.yangbo.webserver.core.exception.base.ServletException;
import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.response.Response;
import com.yangbo.webserver.core.servlet.Servlet;

import java.io.IOException;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-22:00
 * @Description:  根据http请求，实现不同路由的转发
 */
public abstract class HttpServlet implements Servlet {

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void service(Request request, Response response) throws ServletException, IOException {
        if(request.getMethod() == RequestMethod.GET){
            doGet(request,response);
        }else if(request.getMethod() == RequestMethod.POST){
            doPost(request,response);
        }else if(request.getMethod() == RequestMethod.PUT){
            doPut(request,response);
        }else if(request.getMethod() == RequestMethod.DELETE){
            doDelete(request,response);
        }
    }

    public void doGet(Request request, Response response) throws ServletException, IOException {
    }

    public void doPost(Request request, Response response) throws ServletException, IOException {
    }

    public void doPut(Request request, Response response) throws ServletException, IOException {
    }

    public void doDelete(Request request, Response response) throws ServletException, IOException {
    }
}
