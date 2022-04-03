package com.yangbo.webserver.core.network.dispatcher;

import com.yangbo.webserver.core.context.ServletContext;
import com.yangbo.webserver.core.context.WebApplication;
import com.yangbo.webserver.core.exception.handler.ExceptionHandler;
import com.yangbo.webserver.core.network.wrapper.SocketWrapper;
import com.yangbo.webserver.core.resource.ResourceHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: yangbo
 * @Date: 2022-03-19-22:16
 * @Description: 分发器的父类
 */
@Slf4j
public abstract class AbstractDispatcher {
    protected ThreadPoolExecutor pool;
    protected ServletContext servletContext;
    protected ExceptionHandler exceptionHandler;
    protected ResourceHandler resourceHandler;

    public AbstractDispatcher() {
        this.servletContext = WebApplication.getServletContext();
        this.exceptionHandler = new ExceptionHandler();
        this.resourceHandler = new ResourceHandler(exceptionHandler);

        ThreadFactory threadFactory = new ThreadFactory() {
            private int count;
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Boo Worker Poo - " + count++);
            }
        };
        this.pool = new ThreadPoolExecutor(100,
                100,
                1,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(200),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()

        );
        log.info("dispatcher 建立");
    }

    //关闭线程池
    public void shutdown(){
        pool.shutdown();
        servletContext.destroy();
    }

    public abstract void doDispatcher(SocketWrapper socketWrapper);

}
