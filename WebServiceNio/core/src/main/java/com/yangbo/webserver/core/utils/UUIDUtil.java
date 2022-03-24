package com.yangbo.webserver.core.utils;

import java.util.UUID;

/**
 * @Author: yangbo
 * @Date: 2022-03-24-15:14
 * @Description:  创建随机的uuid
 */
public class UUIDUtil {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-","").toUpperCase();
    }
}
