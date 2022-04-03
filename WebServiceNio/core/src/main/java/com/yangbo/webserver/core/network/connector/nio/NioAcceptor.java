package com.yangbo.webserver.core.network.connector.nio;

import com.yangbo.webserver.core.network.endpoint.NioEndpoint;
import com.yangbo.webserver.core.network.wrapper.NioSocketWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * @Author: yangbo
 * @Date: 2022-03-30-13:40
 * @Description:
 */
@Slf4j
public class NioAcceptor implements Runnable {

    private NioEndpoint nioEndpoint;
    public NioAcceptor(NioEndpoint nioEndpoint){
        this.nioEndpoint = nioEndpoint;
    }


    @Override
    public void run() {
        log.info("{}开始监听,开启线程",Thread.currentThread().getName());
        while(nioEndpoint.isRunning()){
            SocketChannel client;
            try {
                client = nioEndpoint.acceptor();
                if(client==null){
                    continue;
                }
                client.configureBlocking(false);
                log.info("Acceptor接收到连接请求");
                nioEndpoint.registerToPoller(client);
                log.info("socketWrapper:{}", client);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }




}
