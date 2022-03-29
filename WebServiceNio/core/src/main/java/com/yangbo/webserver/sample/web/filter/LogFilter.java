package com.yangbo.webserver.sample.web.filter;

import com.yangbo.webserver.core.filter.Filter;
import com.yangbo.webserver.core.filter.FilterChain;
import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-22:06
 * @Description:
 */
@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init() {
        log.info("filter init...");
    }

    @Override
    public void doFilter(Request request, Response response, FilterChain filterChain) {
        log.info("{} before accessed, method is {}", request.getUrl(), request.getMethod());
        filterChain.doFilter(request, response);
        log.info("{} after accessed, method is {}", request.getUrl(), request.getMethod());
    }

    @Override
    public void destroy() {
        log.info("LogFilter destroy ....");
    }
}
