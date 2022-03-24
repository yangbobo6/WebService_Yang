package com.yangbo.webserver.core.context;


import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.yangbo.webserver.core.context.holder.FilterHolder;
import com.yangbo.webserver.core.context.holder.ServletHolder;
import com.yangbo.webserver.core.cookie.Cookie;
import com.yangbo.webserver.core.exception.FilterNotFoundException;
import com.yangbo.webserver.core.exception.ServletNotFoundException;
import com.yangbo.webserver.core.filter.Filter;
import com.yangbo.webserver.core.listener.HttpSessionListener;
import com.yangbo.webserver.core.listener.ServletContextListener;
import com.yangbo.webserver.core.listener.ServletRequestListener;
import com.yangbo.webserver.core.listener.event.HttpSessionEvent;
import com.yangbo.webserver.core.response.Response;
import com.yangbo.webserver.core.servlet.Servlet;
import com.yangbo.webserver.core.session.HttpSession;
import com.yangbo.webserver.core.session.IdleSessionCleaner;
import com.yangbo.webserver.core.utils.UUIDUtil;
import com.yangbo.webserver.core.utils.XMLUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.util.AntPathMatcher;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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


    public ServletContext() throws IllegalAccessException, ClassNotFoundException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        init();
    }

    public void init() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.servlets = new HashMap<>();
        this.servletMapping = new HashMap<>();
        this.attributes = new ConcurrentHashMap<>();
        this.sessions = new ConcurrentHashMap<>();
        this.filters = new HashMap<>();
        this.filterMapping = new HashMap<>();
        this.matcher = new AntPathMatcher();
        this.idleSessionCleaner = new IdleSessionCleaner();
        this.idleSessionCleaner.start();   //session这块还不熟悉  等
        parseConfig();
    }


    /**
     * web.xml文件解析，比如servlet，filter，listener等
     *
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void parseConfig() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Document doc = XMLUtil.getDocument(ServletContext.class.getResourceAsStream("/web.xml"));
        Element root = doc.getRootElement();


        //解析servlet
        List<Element> servlets = root.elements("servlet");
        for (Element servletEle : servlets
        ) {
            String key = servletEle.element("servlet-name").getText();
            String value = servletEle.element("servlet-class").getText();
            this.servlets.put(key, new ServletHolder(value));
        }

        List<Element> servletMapping = root.elements("servlet-mapping");
        for (Element mapping : servletMapping) {
            List<Element> urlPatterns = mapping.elements("url-pattern");
            String value = mapping.element("servlet-name").getText();
            for (Element urlPattern : urlPatterns) {
                this.servletMapping.put(urlPattern.getText(), value);
            }
        }

        // 解析 filter
        List<Element> filters = root.elements("filter");
        for (Element filterEle : filters) {
            String key = filterEle.element("filter-name").getText();
            String value = filterEle.element("filter-class").getText();
            this.filters.put(key, new FilterHolder(value));
        }

        //list<filter-mapping>
        List<Element> elements = root.elements("filter-mapping");

        for (Element mapping : elements
        ) {
            //url-pattern 是列表
            List<Element> urlPatterns = mapping.elements("url-pattern");
            String value = mapping.element("filter-name").getText();
            //解析 url-pattern列表
            for (Element urlPattern : urlPatterns
            ) {
                //是否有列表  没有就先建立
                List<String> values = this.filterMapping.get(urlPattern.getText());
                if (values == null) {
                    values = new ArrayList<>();
                    this.filterMapping.put(urlPattern.getText(), values);
                }
                values.add(value);
            }

        }


        //解析listener
        Element listener = root.element("listener");
        List<Element> listenerEles = listener.elements("listener-class");
        for (Element listenerEle : listenerEles
        ) {
            //通过web.xml创建EventListener对象
            EventListener eventListener = (EventListener) Class.forName(listenerEle.getText()).getConstructor().newInstance();
            if (eventListener instanceof ServletContextListener) {
                servletContextListeners.add((ServletContextListener) eventListener);
            }
            if (eventListener instanceof HttpSessionListener) {
                httpSessionListeners.add((HttpSessionListener) eventListener);
            }
            if (eventListener instanceof ServletRequestListener) {
                servletRequestListeners.add((ServletRequestListener) eventListener);
            }
        }
    }


    /**
     * 由URL得到对应的一个Servlet实例
     *
     * @param url
     * @return
     * @throws ServletNotFoundException
     */
    public Servlet mapServlet(String url) throws ServletNotFoundException {
        //精确匹配 (解析之后的键值对存在map里面)  url --> 别名
        String servletAlias = servletMapping.get(url);
        if (servletAlias != null) {
            return initAndGetServlet(servletAlias);
        }
        //路径匹配
        List<String> matchPatterns = new ArrayList<>();
        Set<String> patterns = servletMapping.keySet();  //获取所有url路径
        for (String pattern : patterns
        ) {
            if (matcher.match(pattern, url)) {
                matchPatterns.add(pattern);
            }
        }
        if (!matchPatterns.isEmpty()) {
            //精确匹配优先原则，获取最准确的
            Comparator<String> patternComparator = matcher.getPatternComparator(url);
            Collections.sort(matchPatterns, patternComparator);
            String bestMatch = matchPatterns.get(0);
            return initAndGetServlet(bestMatch);
        }
        //如果都没匹配到，则是首页的servlet
        return initAndGetServlet(DEFAULT_SERVLET_ALIAS);

    }


    /**
     * 初始化并获取Servlet实例，如果已经初始化过则直接返回
     *
     * @param servletAlias
     * @return
     * @throws ServletNotFoundException
     */
    private Servlet initAndGetServlet(String servletAlias) throws ServletNotFoundException {
        // 通过别名  寻找 Class类
        ServletHolder servletHolder = servlets.get(servletAlias);
        if (servletHolder == null) {
            throw new ServletNotFoundException();
        }
        if (servletHolder.getServlet() == null) {
            try {
                Servlet servlet = (Servlet) Class.forName(servletHolder.getServletClass()).getConstructor().newInstance();
                //初始化
                servlet.init();
                servletHolder.setServlet(servlet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return servletHolder.getServlet();
    }

    /**
     * 由URL得到一系列匹配的Filter实例
     *
     * @param url
     * @return
     */
    public List<Filter> mapFilter(String url) throws FilterNotFoundException {
        List<String> matchingPatterns = new ArrayList<>();
        Set<String> patterns = filterMapping.keySet();
        for (String pattern : patterns
        ) {
            if (matcher.match(pattern, url)) {
                matchingPatterns.add(pattern);
            }
        }
        Set<String> filterAliases = matchingPatterns.stream().flatMap(pattern -> this.filterMapping.get(pattern).stream()).collect(Collectors.toSet());
        List<Filter> result = new ArrayList<>();
        for (String alias : filterAliases
        ) {
            //全部初始化
            result.add(initAndGetFilter(alias));
        }
        return result;
    }

    /**
     * 反射的方式获取
     * 初始化并返回Filter实例，如果已经初始化过则直接返回
     *
     * @param filterAlias
     * @return
     * @throws FilterNotFoundException
     */
    public Filter initAndGetFilter(String filterAlias) throws FilterNotFoundException {
        FilterHolder filterHolder = filters.get(filterAlias);
        if (filterHolder == null) {
            throw new FilterNotFoundException();
        }
        if (filterHolder.getFilter() == null) {
            try {
                Filter filter = (Filter) Class.forName(filterHolder.getFilterClass()).getConstructor().newInstance();
                filter.init();
                filterHolder.setFilter(filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filterHolder.getFilter();
    }

    /*
     * 对session的操作
     * */

    /**
     * 创建session
     *
     * @param response
     * @return
     */
    public HttpSession createSession(Response response) {
        HttpSession session = new HttpSession(UUIDUtil.uuid());
        sessions.put(session.getId(), session);
        response.addCookie(new Cookie("JSESSIONID",session.getId()));
        HttpSessionEvent httpSessionEvent = new HttpSessionEvent(session);
        for (HttpSessionListener listener:httpSessionListeners
             ) {
            listener.sessionCreated(httpSessionEvent);
        }
        return session;
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
