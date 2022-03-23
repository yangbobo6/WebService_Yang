package com.yangbo.webserver.core.context;


import com.yangbo.webserver.core.context.holder.FilterHolder;
import com.yangbo.webserver.core.context.holder.ServletHolder;
import com.yangbo.webserver.core.exception.ServletNotFoundException;
import com.yangbo.webserver.core.listener.HttpSessionListener;
import com.yangbo.webserver.core.listener.ServletContextListener;
import com.yangbo.webserver.core.listener.ServletRequestListener;
import com.yangbo.webserver.core.listener.event.HttpSessionEvent;
import com.yangbo.webserver.core.servlet.Servlet;
import com.yangbo.webserver.core.session.HttpSession;
import com.yangbo.webserver.core.session.IdleSessionCleaner;
import org.springframework.util.AntPathMatcher;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.yangbo.webserver.core.constant.ContextConstant.DEFAULT_SERVLET_ALIAS;
import static com.yangbo.webserver.core.constant.ContextConstant.DEFAULT_SESSION_EXPIRE_TIME;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-22:46
 * @Description: 对web.xml的解析
 */

public class ServletContext {
    /**
     * 别名->类名     一对一关系
     * 一个Servlet类只能有一个Servlet别名，一个Servlet别名只能对应一个Servlet类
     */
    private Map<String, ServletHolder> servlets;

    /**
     * 一个Servlet可以对应多个URL，一个URL只能对应一个Servlet
     * URL Pattern -> Servlet别名
     */
    private Map<String, String> servletMapping;

    /**
     * 别名->类名   过滤器
     */
    private Map<String, FilterHolder> filters;

    /**
     * URL Pattern -> 别名列表，注意同一个URLPattern可以对应多个Filter，但只能对应一个Servlet
     */
    private Map<String, List<String>> filterMapping;

    /**
     * 监听器们
     */
    private List<ServletContextListener> servletContextListeners;
    private List<HttpSessionListener> httpSessionListeners;
    private List<ServletRequestListener> servletRequestListeners;

    /**
     * 域
     */
    private Map<String, Object> attributes;
    /**
     * 整个应用对应的session们
     */
    private Map<String, HttpSession> sessions;
    /**
     * 路径匹配器，由Spring提供
     */
    private AntPathMatcher matcher;

    private IdleSessionCleaner idleSessionCleaner;


    public ServletContext() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        init();
    }

    public void init() {
        this.servlets = new HashMap<>();
        this.servletMapping = new HashMap<>();
        this.attributes = new ConcurrentHashMap<>();
        this.sessions = new ConcurrentHashMap<>();
        this.filters = new HashMap<>();
        this.filterMapping = new HashMap<>();
        this.matcher = new AntPathMatcher();
        this.idleSessionCleaner = new IdleSessionCleaner();
        this.idleSessionCleaner.start();
    }

    /**
     * 由URL得到对应的一个Servlet实例
     *
     * @param url
     * @return
     * @throws ServletNotFoundException
     */
    public Servlet mapServlet(String url) throws ServletNotFoundException {
        //精确匹配
        String servletAlias = servletMapping.get(url);
        if (servletAlias!=null){
            return initAndGetServlet(servletAlias);
        }

    }

    private Servlet initAndGetServlet(String servletAlias) throws ServletNotFoundException{
        ServletHolder servletHolder = servlets.get(servletAlias);
        if(servletHolder == null){
            throw new ServletNotFoundException();
        }


    }


    /**
     * 清除空闲的session
     * 由于ConcurrentHashMap是线程安全的，所以remove不需要进行加锁
     */
    public void cleanIdleSessions() {
        for (Iterator<Map.Entry<String, HttpSession>> it = sessions.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, HttpSession> entry = it.next();
            if (Duration.between(entry.getValue().getLastAccessed(), Instant.now()).getSeconds() >= DEFAULT_SESSION_EXPIRE_TIME) {
//                log.info("该session {} 已过期", entry.getKey());
                afterSessionDestroyed(entry.getValue());
                it.remove();
            }
        }
    }

    private void afterSessionDestroyed(HttpSession session) {
        HttpSessionEvent httpSessionEvent = new HttpSessionEvent(session);
        for (HttpSessionListener listener : httpSessionListeners) {
            listener.sessionDestroyed(httpSessionEvent);
        }
    }

    /**
     * 销毁session
     *
     * @param session
     */
    public void invalidateSession(HttpSession session) {
        sessions.remove(session.getId());
        afterSessionDestroyed(session);
    }


}
