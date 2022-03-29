package com.yangbo.webserver.sample.web.filter;

import com.yangbo.webserver.core.filter.Filter;
import com.yangbo.webserver.core.filter.FilterChain;
import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-22:05
 * @Description:
 */
@Slf4j
public class LoginFilter implements Filter {

    @Override
    public void init() {
        log.info("loginFilter init ...");
    }

    @Override
    public void doFilter(Request request, Response response, FilterChain filterChain) {
        log.info("当前访问的servletPath:{}", request.getServletPath());
        // login直接放行，其他页面访问均需要登录
        if (request.getServletPath().equals("/login") || request.getServletPath().startsWith("/views/errors")) {
            log.info("直接放行");
            filterChain.doFilter(request, response);
        } else {
            log.info("检查是否登录...");
            if (request.getSession(false) != null && request.getSession().getAttribute("username") != null) {
                log.info("已登录，通过检查...");
                filterChain.doFilter(request, response);
            } else {
                log.info("未登录,401");
                // 未登录。重定向至登录页面
                response.sendRedirect("/login");
            }
        }
    }

    @Override
    public void destroy() {

    }
}
