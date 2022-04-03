package com.yangbo.webserver.core.network.dispatcher;

import com.yangbo.webserver.core.exception.ServerErrorException;
import com.yangbo.webserver.core.exception.base.ServletException;
import com.yangbo.webserver.core.network.handler.NioRequestHandler;
import com.yangbo.webserver.core.network.wrapper.NioSocketWrapper;
import com.yangbo.webserver.core.network.wrapper.SocketWrapper;
import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.resource.ResourceHandler;
import com.yangbo.webserver.core.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Author: yangbo
 * @Date: 2022-03-30-13:39
 * @Description:
 */
@Slf4j
public class NioDispatcher extends AbstractDispatcher{

    /**
     * 分发请求，注意IO读取必须放在IO线程中进行，不能放到线程池中，否则会出现多个线程同时读同一个socket数据的情况
     * 1、读取数据
     * 2、构造request，response
     * 3、将业务放入到线程池中处理
     * @param socketWrapper
     */
    @Override
    public void doDispatcher(SocketWrapper socketWrapper) {
        NioSocketWrapper nioSocketWrapper = (NioSocketWrapper) socketWrapper;
        log.info("已经将请求放入worker线程池中");
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        log.info("开始读取request");
        Request request = null;
        Response response = null;
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (nioSocketWrapper.getSocketChannel().read(byteBuffer)>0){
                byteBuffer.flip();
                baos.write(byteBuffer.array());
            }
            baos.close();
            request = new Request(baos.toByteArray());
            response = new Response();
            pool.execute(new NioRequestHandler(socketWrapper,
                    servletContext,exceptionHandler,resourceHandler,request,response));
        }catch (IOException e){
            e.printStackTrace();
            exceptionHandler.handle(new ServerErrorException(),response,nioSocketWrapper);
        }catch (ServletException e){
            exceptionHandler.handle(e,response,nioSocketWrapper);
        }
    }
}
