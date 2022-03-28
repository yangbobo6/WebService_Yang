package com.yangbo.webserver.core.exception;

import com.yangbo.webserver.core.enumeration.HttpStatus;
import com.yangbo.webserver.core.exception.base.ServletException;

/**
 * @Author: yangbo
 * @Date: 2022-03-28-10:13
 * @Description:
 */
public class ServerErrorException extends ServletException {
    private static final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    public ServerErrorException() {
        super(status);
    }
}
