package com.yangbo.webserver.core.listener;

import com.yangbo.webserver.core.listener.event.ServletRequestEvent;

import java.util.EventListener;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-22:37
 * @Description:
 */
public interface ServletRequestListener extends EventListener {
    /**
     * 请求初始化
     * @param sre
     */
    void requestInitialized(ServletRequestEvent sre);

    /**
     * 请求销毁
     * @param sre
     */
    void requestDestroyed(ServletRequestEvent sre);


}
