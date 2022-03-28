package com.yangbo.webserver.core.network.dispatcher;

import com.yangbo.webserver.core.exception.RequestInvalidException;
import com.yangbo.webserver.core.network.handler.BioRequestHandler;
import com.yangbo.webserver.core.network.wrapper.BioSocketWrapper;
import com.yangbo.webserver.core.network.wrapper.SocketWrapper;
import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.net.Socket;

/**
 * @Author: yangbo
 * @Date: 2022-03-27-9:26
 * @Description:
 */
@Slf4j
public class BioDispatcher extends AbstractDispatcher {

    @Override
    public void doDispatcher(SocketWrapper socketWrapper) {
        BioSocketWrapper bioSocketWrapper = (BioSocketWrapper) socketWrapper;
        //得到这个网络请求
        Socket socket = bioSocketWrapper.getSocket();
        Request request = null;
        Response response = null;
        try {
            BufferedInputStream bin = new BufferedInputStream(socket.getInputStream());
            byte[] bytes = new byte[bin.available()];
            int len = bin.read(bytes);
            if(len<=0){
                throw new RequestInvalidException();
            }
            // 这里这里不要把bin关掉，把in关掉就等同于把socket关掉
            //解析请求
            response = new Response();
            request = new Request(bytes);
            //线程池执行
            pool.execute(new BioRequestHandler(socketWrapper,servletContext,exceptionHandler,resourceHandler,request,response));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
