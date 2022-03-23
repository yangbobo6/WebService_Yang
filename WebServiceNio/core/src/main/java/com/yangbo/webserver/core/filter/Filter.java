package com.yangbo.webserver.core.filter;

import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.response.Response;

/**
 * @Author: yangbo
 * @Date: 2022-03-23-11:24
 * @Description:
 */
public interface Filter {
    /**
     * 过滤器初始化
     */
    void init();

    /**
     * 过滤
     * @param request
     * @param response
     * @param filterChain
     */
    void doFilter(Request request, Response response, FilterChain filterChain) ;

    /**
     * 过滤器销毁
     */
    void destroy();
}
