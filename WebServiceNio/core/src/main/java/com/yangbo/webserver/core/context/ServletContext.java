package com.yangbo.webserver.core.context;


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
import com.yangbo.webserver.core.listener.event.ServletContextEvent;
import com.yangbo.webserver.core.listener.event.ServletRequestEvent;
import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.response.Response;
import com.yangbo.webserver.core.servlet.Servlet;
import com.yangbo.webserver.core.session.HttpSession;
import com.yangbo.webserver.core.session.IdleSessionCleaner;
import com.yangbo.webserver.core.utils.UUIDUtil;
import com.yangbo.webserver.core.utils.XMLUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.util.AntPathMatcher;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
@Slf4j
@Data
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


    public ServletContext() throws Exception {
        init();
    }

    public void init() throws Exception {
        this.servlets = new HashMap<>();
        this.servletMapping = new HashMap<>();
        this.attributes = new ConcurrentHashMap<>();
        this.sessions = new ConcurrentHashMap<>();
        this.filters = new HashMap<>();
        this.filterMapping = new HashMap<>();
        this.matcher = new AntPathMatcher();
        //this.idleSessionCleaner = new IdleSessionCleaner();
        //this.idleSessionCleaner.start();   //session这块还不熟悉  等
        this.servletContextListeners = new ArrayList<>();
        this.servletRequestListeners = new ArrayList<>();
        this.httpSessionListeners = new ArrayList<>();
        parseConfig();
        ServletContextEvent servletContextEvent = new ServletContextEvent(this);
        for (ServletContextListener listener : servletContextListeners) {
            listener.contextInitialized(servletContextEvent);
        }
    }

    /**
     * 应用关闭前被调用
     */
    public void destroy() {
        //map  servlet 类名    servlet  filter servletContextEvent清除
        servlets.values().forEach(servletHolder -> {
            if (servletHolder.getServlet() != null) {
                servletHolder.getServlet().destroy();
            }
        });
        filters.values().forEach(filterHolder -> {
            if (filterHolder.getFilter() != null) {
                filterHolder.getFilter().destroy();
            }
        });
        ServletContextEvent servletContextEvent = new ServletContextEvent(this);
        for (ServletContextListener listener : servletContextListeners) {
            listener.contextDestroyed(servletContextEvent);
        }
    }


    /**
     * web.xml文件解析，比如servlet，filter，listener等
     * 
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void parseConfig() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, FileNotFoundException {

        File file = new File("E:\\Project\\javaProject\\WebServer\\WebService_Yang\\WebServiceNio\\core\\src\\main\\webapp\\WEB-INF\\web.xml");
        InputStream inputStream = new FileInputStream(file);

        //Document doc = XMLUtil.getDocument(ServletContext.class.getResourceAsStream("/web.xml"));
        Document doc = XMLUtil.getDocument(inputStream);
        Element root = doc.getRootElement();


        //解析servlet
        List<Element> servlets = root.elements("servlet");
        for (Element servletEle : servlets
        ) {
            String key = servletEle.element("servlet-name").getText();
            String value = servletEle.element("servlet-class").getText();
            //System.out.println(key + value + "----");
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
                //加入到httpSessionListener队列中去
                httpSessionListeners.add((HttpSessionListener) eventListener);
            }
            if (eventListener instanceof ServletRequestListener) {
                System.out.println(servletRequestListeners.size());
                //父类的add方法
                servletRequestListeners.add((ServletRequestListener) eventListener);
            }
        }
        
        log.info("对web.xml解析结果为：");
        
        log.info(this.servlets+"\n"+this.servletMapping
        +"\n"+this.filters+"\n"+filterMapping);
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
            log.info("精确匹配成功，url映射的class为："+servletAlias);
            return initAndGetServlet(servletAlias);
        }
        
        
        //路径匹配   如果servlet中不存在的，则用路径匹配
        List<String> matchPatterns = new ArrayList<>();
        Set<String> patterns = servletMapping.keySet();  //获取所有url路径
        log.info("路径匹配: \n"+patterns.toString());
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

        //如果servletHolder不存在
        if (servletHolder == null) {
            throw new ServletNotFoundException();
        }
        //如果servlet 为空
        if (servletHolder.getServlet() == null) {
            try {
                log.info("根据 url创建  servlet ");
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
        log.info("过滤器匹配的url :"+url);
        List<String> matchingPatterns = new ArrayList<>();
        
        //过滤器的url 只能 按照路径匹配
        Set<String> patterns = filterMapping.keySet();
        for (String pattern : patterns
        ) {
            if (matcher.match(pattern, url)) {
                matchingPatterns.add(pattern);
            }
        }
        
        Set<String> filterAliases = matchingPatterns.stream().flatMap(pattern -> this.filterMapping.get(pattern).stream()).collect(Collectors.toSet());
        log.info("过滤器filterAliases:"+filterAliases.toString());
        List<Filter> result = new ArrayList<>();
        for (String alias : filterAliases) {
            //全部初始化
            result.add(initAndGetFilter(alias));
        }
        return result;
    }

    /**
     * 反射的方式获取
     * 初始化并返回Filter实例，如果已经初始化过则直接返回
     * @param filterAlias
     * @return
     * @throws FilterNotFoundException
     */
    public Filter initAndGetFilter(String filterAlias) throws FilterNotFoundException {
        
        log.info("filter :"+filters.toString()+"\n"+"filterAlias:"+filterAlias);
        
        FilterHolder filterHolder = filters.get(filterAlias);
        if (filterHolder == null) {
            throw new FilterNotFoundException();
        }
        //如果过滤器内
        if (filterHolder.getFilter() == null) {
            try {
                log.info("filterHolder对象："+filterHolder.toString());
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
        response.addCookie(new Cookie("JSESSIONID", session.getId()));
        //sessionEvent  事件生成
        HttpSessionEvent httpSessionEvent = new HttpSessionEvent(session);
        //新创建一个session，触发session监听器，可能有多个
        for (HttpSessionListener listener : httpSessionListeners
        ) {
            //触发监听器
            listener.sessionCreated(httpSessionEvent);
        }
        return session;
    }

    /**
     * 获取session
     * @param JSESSIONID
     * @return
     */
    public HttpSession getSession(String JSESSIONID) {
        return sessions.get(JSESSIONID);
    }


    /**
     * 清除空闲的session    session时间过期，清除
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
    
    
    
    

    //再解析完毕 Request以后，监听
    public void afterRequestCreated(Request request) {
        ServletRequestEvent servletRequestEvent = new ServletRequestEvent(this, request);
        for (ServletRequestListener listener : servletRequestListeners) {
            listener.requestInitialized(servletRequestEvent);
        }
    }
    public void afterRequestDestroyed(Request request) {
        ServletRequestEvent servletRequestEvent = new ServletRequestEvent(this, request);
        for (ServletRequestListener listener : servletRequestListeners) {
            listener.requestDestroyed(servletRequestEvent);
        }
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }


}
