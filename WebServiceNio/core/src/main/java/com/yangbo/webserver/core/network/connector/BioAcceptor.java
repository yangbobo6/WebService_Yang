package com.yangbo.webserver.core.network.connector;

import com.yangbo.webserver.core.network.dispatcher.BioDispatcher;
import com.yangbo.webserver.core.network.endpoint.BioEndpoint;
import com.yangbo.webserver.core.network.wrapper.BioSocketWrapper;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

/**
 * @Author: yangbo
 * @Date: 2022-03-27-9:25
 * @Description:
 */
@Slf4j
public class BioAcceptor implements Runnable{
    private BioEndpoint server;
    private BioDispatcher dispatcher;
    public BioAcceptor(BioEndpoint server,BioDispatcher dispatcher){
        log.info("创建acceptor");
        this.dispatcher = dispatcher;
        this.server = server;
    }

    @Override
    public void run() {
        log.info("开始监听");
        Socket client;
        try {
            //接收网络请求，client是个socket
            client = server.accept();
            log.info("连接成功+1   "+client);
            //连接成功，连接数加一
            dispatcher.doDispatcher(new BioSocketWrapper(client));

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
