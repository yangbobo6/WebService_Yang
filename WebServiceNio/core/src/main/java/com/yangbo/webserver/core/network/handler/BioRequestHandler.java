package com.yangbo.webserver.core.network.handler;

import com.yangbo.webserver.core.context.WebApplication;
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

    public BioRequestHandler(Response response, Socket socket) {
        super(response,socket);
    }

    //写完后立即关闭
    @Override
    public void flushResponse() {
        isFinished = true;
        byte[] bytes = response.getResponseBytes();

        OutputStream os = null;
        try {
            os = socket.getOutputStream();
            os.write(bytes);
            os.flush();

        }catch (Exception e){
            e.printStackTrace();
            log.info("socket closed");
        }finally {
            try {
                os.close();
                socket.close();
            }catch (Exception e){
                e.printStackTrace();
                log.info("关闭socket");
            }
        }
        //WebApplication.getServletContext().afterRequestDestroyed(request);

    }
}
