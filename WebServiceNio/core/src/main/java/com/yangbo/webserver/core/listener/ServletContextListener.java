package com.yangbo.webserver.core.listener;

import com.yangbo.webserver.core.listener.event.ServletContextEvent;

import java.util.EventListener;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-22:36
 * @Description:  应用层上面的监听器
 */
public interface ServletContextListener extends EventListener {
    /**
     * 应用启动
     * @param sce
     */
    void contextInitialized(ServletContextEvent sce);

    /**
     * 应用关闭
     * @param sce
     */
    void contextDestroyed(ServletContextEvent sce);
}
