package com.yangbo.webserver.core.network.wrapper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;

/**
 * @Author: yangbo
 * @Date: 2022-03-27-9:27
 * @Description:  BIO的socket包装类
 */
@Slf4j
@Getter
public class BioSocketWrapper implements SocketWrapper{
    private Socket socket;
    public BioSocketWrapper(Socket socket){
        this.socket = socket;
    }
    public void close() throws IOException {
        socket.close();
    }
}
