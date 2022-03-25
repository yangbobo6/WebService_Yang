package com.yangbo.webserver.sample.web.listener;

import com.yangbo.webserver.core.listener.HttpSessionListener;
import com.yangbo.webserver.core.listener.ServletContextListener;
import com.yangbo.webserver.core.listener.event.HttpSessionEvent;
import com.yangbo.webserver.core.listener.event.ServletContextEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-22:06
 * @Description:
 */
@Slf4j
public class ServletContextAndSessionListener implements ServletContextListener, HttpSessionListener {
    //定义一个int 类型变量
    private AtomicInteger sessionCount = new AtomicInteger();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        //session 会话数加一
        log.info("session created ,count = {}",this.sessionCount.incrementAndGet());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        //session 会话数减一
        log.info("session destroyed ,count = {} ",sessionCount.decrementAndGet());
    }


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("servlet context init ...");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("servlet context destroyed");
    }
}
