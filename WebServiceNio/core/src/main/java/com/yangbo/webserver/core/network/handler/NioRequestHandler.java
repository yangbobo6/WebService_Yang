package com.yangbo.webserver.core.network.handler;

import com.yangbo.webserver.core.context.ServletContext;
import com.yangbo.webserver.core.context.WebApplication;
import com.yangbo.webserver.core.exception.FilterNotFoundException;
import com.yangbo.webserver.core.exception.ServletNotFoundException;
import com.yangbo.webserver.core.exception.handler.ExceptionHandler;
import com.yangbo.webserver.core.network.wrapper.NioSocketWrapper;
import com.yangbo.webserver.core.network.wrapper.SocketWrapper;
import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.resource.ResourceHandler;
import com.yangbo.webserver.core.response.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @Author: yangbo
 * @Date: 2022-03-31-9:30
 * @Description:
 */
@Getter
@Slf4j
@Setter
public class NioRequestHandler extends AbstractRequestHandler{

    public NioRequestHandler(SocketWrapper socketWrapper, 
                             ServletContext servletContext, 
                             ExceptionHandler exceptionHandler, 
                             ResourceHandler resourceHandler, 
                             Request request, Response response) throws ServletNotFoundException, FilterNotFoundException, ServletNotFoundException, FilterNotFoundException {
        super(socketWrapper, servletContext, exceptionHandler, resourceHandler, request, response);
    }

    @Override
    public void flushResponse() {
        isFinished = true;
        //先将字节数组存到byteBuffer中
        NioSocketWrapper nioSocketWrapper =(NioSocketWrapper)socketWrapper;
        ByteBuffer[] responseData =response.getResponseByteBuffer();
        try {
            //写道通道里面
            nioSocketWrapper.getSocketChannel().write(responseData);
            List<String> connection = request.getHeaders().get("Connection");
            if (connection != null && connection.get(0).equals("close")) {
                log.info("CLOSE: 客户端连接{} 已关闭", nioSocketWrapper.getSocketChannel());
                nioSocketWrapper.close();
            } else {
                // keep-alive 重新注册到Poller中
                log.info("KEEP-ALIVE: 客户端连接{} 重新注册到Poller中", nioSocketWrapper.getSocketChannel());
                nioSocketWrapper.getNioPoller().register(nioSocketWrapper.getSocketChannel(), false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        WebApplication.getServletContext().afterRequestDestroyed(request);
        
    }
}
