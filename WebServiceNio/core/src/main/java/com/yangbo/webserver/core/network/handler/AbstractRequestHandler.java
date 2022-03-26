package com.yangbo.webserver.core.network.handler;

import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.response.Response;
import lombok.Getter;

import java.net.Socket;

/**
 * @Author: yangbo
 * @Date: 2022-03-25-21:15
 * @Description: RequestHandler 的父类，通过父类来复用成员变量和部分方法
 *  不同IO模型的RequestHandler基本是在将Response写回客户端这部分有不同的实现，在这里被设置为了抽象方法
 */
@Getter
public abstract class AbstractRequestHandler {
    protected Response response;
    protected Request request;
    protected boolean isFinished;
    protected Socket socket;

    public AbstractRequestHandler(Response response,Socket socket){
        this.response = response;
        this.socket = socket;
    }


    /**
     * 响应数据写回到客户端
     */
    public abstract void flushResponse();


}
