package com.yangbo.webserver.core.network.endpoint;

/**
 * @Author: yangbo
 * @Date: 2022-03-19-16:57
 * @Description:  控制程序的启动和关闭  抽象接口，容易扩展
 */
public abstract class Endpoint {

    //启动服务器
    public abstract void start(int port);

    //关闭服务
    public abstract void close();

}
