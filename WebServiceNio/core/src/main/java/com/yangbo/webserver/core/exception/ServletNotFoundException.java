package com.yangbo.webserver.core.exception;

import com.yangbo.webserver.core.enumeration.HttpStatus;
import com.yangbo.webserver.core.exception.base.ServletException;

/**
 * @Author: yangbo
 * @Date: 2022-03-23-19:30
 * @Description: 未找到对应的Servlet（web.xml配置错误）
 */
public class ServletNotFoundException extends ServletException {
    private static final HttpStatus status = HttpStatus.NOT_FOUND;
    public ServletNotFoundException() {
        super(status);
    }
}
