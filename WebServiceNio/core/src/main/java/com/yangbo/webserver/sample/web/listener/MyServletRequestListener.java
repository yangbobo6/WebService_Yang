package com.yangbo.webserver.sample.web.listener;

import com.yangbo.webserver.core.listener.ServletRequestListener;
import com.yangbo.webserver.core.listener.event.ServletRequestEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-22:07
 * @Description:
 */
@Slf4j
public class MyServletRequestListener implements ServletRequestListener {
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        log.info("request init....bo");
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        log.info("request destroyed....bo");
    }
}
