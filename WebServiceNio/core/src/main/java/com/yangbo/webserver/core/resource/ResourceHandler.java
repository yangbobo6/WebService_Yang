package com.yangbo.webserver.core.resource;

import com.yangbo.webserver.core.exception.handler.ExceptionHandler;
import com.yangbo.webserver.core.network.wrapper.BioSocketWrapper;
import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.response.Response;

/**
 * @Author: yangbo
 * @Date: 2022-03-27-11:11
 * @Description: 对于所有的静态资源，交由ResourceHandler处理。
 * 它会取出对应的静态资源并写入输出流，如果文件未找到，那么会将请求再次交给ExceptionHandler处理。
 */
public class ResourceHandler {
    private ExceptionHandler exceptionHandler;
    public ResourceHandler(ExceptionHandler exceptionHandler){
        this.exceptionHandler = exceptionHandler;
    }

    public void handler(Request request, Response response, BioSocketWrapper bioSocketWrapper){

    }
}
