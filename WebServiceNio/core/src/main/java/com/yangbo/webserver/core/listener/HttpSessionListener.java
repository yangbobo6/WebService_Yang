package com.yangbo.webserver.core.listener;

import com.yangbo.webserver.core.listener.event.HttpSessionEvent;

import java.util.EventListener;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-22:37
 * @Description:
 */
public interface HttpSessionListener extends EventListener {
    /**
     * session创建
     * @param se
     */
    void sessionCreated(HttpSessionEvent se);

    /**
     * session销毁
     * @param se
     */
    void sessionDestroyed(HttpSessionEvent se);

}
