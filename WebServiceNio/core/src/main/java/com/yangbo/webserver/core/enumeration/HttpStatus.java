package com.yangbo.webserver.core.enumeration;

/**
 * @Author: yangbo
 * @Date: 2022-03-23-19:33
 * @Description:
 */
public enum HttpStatus {
    OK(200),NOT_FOUND(404),INTERNAL_SERVER_ERROR(500),BAD_REQUEST(400),MOVED_TEMPORARILY(302);
    private int code;
    HttpStatus(int code){
        this.code = code;
    }
    public int getCode(){
        return code;
    }
}
