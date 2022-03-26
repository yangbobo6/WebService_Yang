package com.yangbo.webserver.core.network;

import com.yangbo.webserver.core.network.handler.BioRequestHandler;
import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author: yangbo
 * @Date: 2022-03-26-10:01
 * @Description:
 */
@Slf4j
public class BioTest {
    private ServerSocket serverSocket;
    private boolean isRunning;

    public static void main(String[] args) {
        BioTest bioTest = new BioTest();
        bioTest.start();

    }

    //启动服务
    public void start() {
        try {
            serverSocket = new ServerSocket(8888);
            isRunning = true;
            //等待接受处理
            receiver();

        } catch (Exception e) {
            e.printStackTrace();
            log.info("服务器启动失败");
            stop();
        }

    }

    public void receiver() {
        try {
            while(isRunning){
                Socket socket = serverSocket.accept();
                log.info("浏览器和服务器 建立连接");
                InputStream inputStream = socket.getInputStream();

                byte[] bytes = inputStream.readAllBytes();
                inputStream.read(bytes);
                String str = new String(bytes);
                log.info(str);

                //请求区  将浏览器请求request解析
                Request request = new Request(bytes);
                log.info("-------"+request.getUrl());

                //响应区
                Response response = new Response();
                BioRequestHandler bioRequestHandler = new BioRequestHandler(response,socket);
                bioRequestHandler.flushResponse();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("接受处理问题");
        }
    }

    public void stop(){
        isRunning = false;
        try {
            serverSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
