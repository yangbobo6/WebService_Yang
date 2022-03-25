package com.yangbo.webserver.sample;

import com.yangbo.webserver.core.context.ServletContext;
import com.yangbo.webserver.sample.web.filter.LoginFilter;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: yangbo
 * @Date: 2022-03-19-16:50
 * @Description: 程序启动--
 */
public class Main {
    public static void main(String[] args) throws IOException {
        InputStream resourceAsStream = LoginFilter.class.getResourceAsStream("/WEB-INF/web.xml");
        byte[] bytes = new byte[0];
        bytes = new byte[resourceAsStream.available()];
        resourceAsStream.read(bytes);
        String str = new String(bytes);
        System.out.println(str);
    }
}
