package com.yangbo.webserver.core.network.endpoint;

import com.yangbo.webserver.core.network.connector.BioAcceptor;
import com.yangbo.webserver.core.network.dispatcher.BioDispatcher;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author: yangbo
 * @Date: 2022-03-27-9:26
 * @Description:
 */
@Slf4j
public class BioEndpoint {
    private ServerSocket server;
    private BioDispatcher dispatcher;
    private BioAcceptor acceptor;
    private volatile boolean isRunning = true;

    public void start(){
        try {
            dispatcher = new BioDispatcher();
            server = new ServerSocket(8888);
            initAcceptor();
            log.info("服务器启动");

        }catch (Exception e){
            e.printStackTrace();
            log.info("服务器启动失败");
            close();
        }
    }

    public void initAcceptor(){
        acceptor = new BioAcceptor(this,dispatcher);

        //线程开启
        Thread t = new Thread(acceptor,"bio-acceptor");
        t.setDaemon(true);
        t.start();
    }

    public Socket accept() throws IOException {
        return server.accept();
    }

    public void close() {
        isRunning = false;
        dispatcher.shutdown();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
