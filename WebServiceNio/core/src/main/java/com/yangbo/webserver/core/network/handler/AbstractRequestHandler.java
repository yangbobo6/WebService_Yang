package com.yangbo.webserver.core.network.handler;

import com.yangbo.webserver.core.context.ServletContext;
import com.yangbo.webserver.core.exception.FilterNotFoundException;
import com.yangbo.webserver.core.exception.ServletNotFoundException;
import com.yangbo.webserver.core.exception.base.ServletException;
import com.yangbo.webserver.core.exception.handler.ExceptionHandler;
import com.yangbo.webserver.core.filter.Filter;
import com.yangbo.webserver.core.filter.FilterChain;
import com.yangbo.webserver.core.network.wrapper.SocketWrapper;
import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.resource.ResourceHandler;
import com.yangbo.webserver.core.response.Response;
import com.yangbo.webserver.core.servlet.Servlet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import com.yangbo.webserver.core.exception.ServerErrorException;

import java.net.Socket;
import java.util.List;

/**
 * @Author: yangbo
 * @Date: 2022-03-25-21:15
 * @Description: RequestHandler 的父类，通过父类来复用成员变量和部分方法
 *  不同IO模型的RequestHandler基本是在将Response写回客户端这部分有不同的实现，在这里被设置为了抽象方法
 */
@Slf4j
@Getter
public abstract class AbstractRequestHandler implements FilterChain,Runnable {
    protected Response response;
    protected Request request;
    protected SocketWrapper socketWrapper;  //Socket包装类  socket
    protected ServletContext servletContext; //解析Web.xml解析
    protected ExceptionHandler exceptionHandler;
    protected ResourceHandler resourceHandler;
    protected boolean isFinished;
    protected Servlet servlet;          //自己定义的Servlet
    protected List<Filter> filters;
    private int filterIndex = 0;

    public AbstractRequestHandler(SocketWrapper socketWrapper,
                                  ServletContext servletContext,
                                  ExceptionHandler exceptionHandler,
                                  ResourceHandler resourceHandler,
                                  Request request, Response response) throws ServletNotFoundException, FilterNotFoundException, ServletNotFoundException, FilterNotFoundException {

        this.socketWrapper = socketWrapper;
        this.servletContext = servletContext;
        this.exceptionHandler = exceptionHandler;
        this.resourceHandler = resourceHandler;
        this.isFinished = false;
        this.request = request;
        this.response = response;

        request.setServletContext(servletContext);   //??? 用处？
        request.setRequestHandler(this);
        response.setRequestHandler(this);

        // 根据url查询匹配的servlet，结果是0个或1个,创建好servlet
        servlet = servletContext.mapServlet(request.getUrl());
        
        // 根据url查询匹配的filter，结果是0个或多个
        filters = servletContext.mapFilter(request.getUrl());
        log.info("------AbstractRequestHandler（ servlet filter） 创建完毕------");
    }

    /**
     * 入口
     */
    @Override
    public void run() {
        log.info("----线程开启-----");
        // 如果没有filter，则直接执行servlet
        if (filters.isEmpty()){
            log.info(filters.toString());
            service();
        } else {
            // 先执行filter
            doFilter(request, response);
        }
    }

    /**
     * 递归执行，自定义filter中如果同意放行，那么会调用filterChain(也就是requestHandler)的doiFilter方法，
     * 此时会执行下一个filter的doFilter方法；
     * 如果不放行，那么会在sendRedirect之后将响应数据写回客户端，结束；
     * 如果所有Filter都执行完毕，那么会调用service方法，执行servlet逻辑
     * @param request
     * @param response
     *
     * 这个是filterChains的 方法
     */
    @Override
    public void doFilter(Request request, Response response) {
        if (filterIndex < filters.size()) {
            //过滤器执行（login和log过滤器）
            filters.get(filterIndex++).doFilter(request, response, this);
        } else {
            service();
        }
    }

    /**
     * 根据不同的请求，调用不同的servlet
     * 调用servlet
     */
    private void service() {
        try {
            //处理动态资源，交由某个Servlet执行
            //Servlet是单例多线程
            //Servlet在RequestHandler中执行
            servlet.service(request, response);
        } catch (ServletException e) {
            exceptionHandler.handle(e, response, socketWrapper);
        } catch (Exception e) {
            //其他未知异常
            e.printStackTrace();
            exceptionHandler.handle(new ServerErrorException(), response, socketWrapper);
        } finally {
            if (!isFinished) {
                flushResponse();
            }
        }
        log.info("请求处理完毕");
    }
    
    /**
     * 响应数据写回到客户端
     */
    public abstract void flushResponse();
}
