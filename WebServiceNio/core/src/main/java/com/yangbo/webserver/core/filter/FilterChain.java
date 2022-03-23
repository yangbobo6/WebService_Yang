package com.yangbo.webserver.core.filter;

import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.response.Response;

/**
 * @Author: yangbo
 * @Date: 2022-03-23-11:25
 * @Description:
 */
public interface FilterChain {
    /**
     * 当前filter放行，由后续的filter继续进行过滤
     * @param request
     * @param response
     */
    void doFilter(Request request, Response response) ;
}
