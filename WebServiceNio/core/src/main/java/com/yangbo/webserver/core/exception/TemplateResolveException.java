package com.yangbo.webserver.core.exception;

import com.yangbo.webserver.core.enumeration.HttpStatus;
import com.yangbo.webserver.core.exception.base.ServletException;

/**
 * @Author: yangbo
 * @Date: 2022-03-27-16:28
 * @Description:
 */
public class TemplateResolveException extends ServletException {
    private static HttpStatus status = HttpStatus.NOT_FOUND;

    public TemplateResolveException() {
        super(status);
    }
}
