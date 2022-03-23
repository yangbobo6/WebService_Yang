package com.yangbo.webserver.core.exception.base;

import com.yangbo.webserver.core.enumeration.HttpStatus;
import lombok.Getter;

/**
 * @Author: yangbo
 * @Date: 2022-03-23-19:31
 * @Description:  跟异常
 */
@Getter
public class ServletException extends Exception{
    private HttpStatus status;
    public ServletException(HttpStatus status){
        this.status = status;
    }
}
