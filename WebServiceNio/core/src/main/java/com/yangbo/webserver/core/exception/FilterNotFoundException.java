package com.yangbo.webserver.core.exception;

import com.yangbo.webserver.core.enumeration.HttpStatus;
import com.yangbo.webserver.core.exception.base.ServletException;

/**
 * @Author: yangbo
 * @Date: 2022-03-24-8:59
 * @Description: 未找到对应的Servlet（web.xml配置错误）
 */
public class FilterNotFoundException extends ServletException {
    private static final HttpStatus status = HttpStatus.NOT_FOUND;
    public FilterNotFoundException() {
        super(status);
    }
}
