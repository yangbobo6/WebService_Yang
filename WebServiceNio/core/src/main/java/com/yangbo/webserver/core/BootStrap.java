package com.yangbo.webserver.core;

import com.yangbo.webserver.core.network.endpoint.BioEndpoint;
import com.yangbo.webserver.core.network.endpoint.Endpoint;

import java.util.Scanner;

/**
 * @Author: yangbo
 * @Date: 2022-03-19-16:49
 * @Description:  程序的入口
 */
public class BootStrap {

    public static void main(String[] args) throws Exception {
        BioEndpoint endpoint = new BioEndpoint();
        endpoint.start();
        //加入外部输入EXIT  退出
        Scanner scanner = new Scanner(System.in);
        String order;                                                                                                                               
        while (scanner.hasNext()) {
            order = scanner.next();
            if (order.equals("EXIT")) {
                endpoint.close();
                System.exit(0);
            }
        }
    }
}
