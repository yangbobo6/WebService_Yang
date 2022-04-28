package com.yangbo.webserver.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: yangbo
 * @Date: 2022-03-27-15:02
 * @Description: IO流的工具类
 */
@Slf4j
public class IOUtil {
    
    public static byte[] getByteFromFile(String fileName) throws IOException {
        //通过login.html文件名  获取字节流,返回给template模板类
        InputStream in = IOUtil.class.getResourceAsStream(fileName);
        if (in == null) {
            log.info("Not Found File:{}", fileName);
            throw new FileNotFoundException();
        }
        log.info("正在读取文件");
        return getByteFromStream(in);
    }

    private static byte[] getByteFromStream(InputStream in) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = in.read(buffer)) != -1) {
            outStream.write(buffer,0,len);
        }
        outStream.close();
        in.close();
        return outStream.toByteArray();
    }
}
