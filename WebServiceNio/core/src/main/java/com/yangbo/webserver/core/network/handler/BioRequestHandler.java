package com.yangbo.webserver.core.network.handler;

import com.yangbo.webserver.core.context.ServletContext;
import com.yangbo.webserver.core.context.WebApplication;
import com.yangbo.webserver.core.exception.FilterNotFoundException;
import com.yangbo.webserver.core.exception.ServletNotFoundException;
import com.yangbo.webserver.core.exception.handler.ExceptionHandler;
import com.yangbo.webserver.core.network.wrapper.BioSocketWrapper;
import com.yangbo.webserver.core.network.wrapper.SocketWrapper;
import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.resource.ResourceHandler;
import com.yangbo.webserver.core.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.net.Socket;

/**
 * @Author: yangbo
 * @Date: 2022-03-26-11:08
 * @Description:
 */
@Slf4j
public class BioRequestHandler extends AbstractRequestHandler{

    public BioRequestHandler(SocketWrapper socketWrapper, ServletContext servletContext, ExceptionHandler exceptionHandler, ResourceHandler resourceHandler, Request request, Response response) throws FilterNotFoundException, ServletNotFoundException {
        super(socketWrapper,servletContext,exceptionHandler,resourceHandler,request,response);
    }

    //写完后立即关闭
    @Override
    public void flushResponse() {
        isFinished = true;
        BioSocketWrapper bioSocketWrapper = (BioSocketWrapper) socketWrapper;
        byte[] bytes = response.getResponseBytes();

        OutputStream os = null;
        try {
            os = bioSocketWrapper.getSocket().getOutputStream();
            os.write(bytes);
            os.flush();

        }catch (Exception e){
            e.printStackTrace();
            log.info("socket closed");
        }finally {
            try {
                os.close();
                bioSocketWrapper.getSocket().close();
            }catch (Exception e){
                e.printStackTrace();
                log.info("关闭socket");
            }
        }
        //WebApplication.getServletContext().afterRequestDestroyed(request);

    }

}
