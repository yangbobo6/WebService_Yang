package com.yangbo.webserver.core.exception;

import com.yangbo.webserver.core.enumeration.HttpStatus;
import com.yangbo.webserver.core.exception.base.ServletException;

/**
 * @Author: yangbo
 * @Date: 2022-03-27-16:22
 * @Description:
 */
public class ResourceNotFoundException extends ServletException {
    private static HttpStatus status = HttpStatus.NOT_FOUND;
    public ResourceNotFoundException() {
        super(status);
    }

}
