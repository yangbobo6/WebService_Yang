package com.yangbo.webserver.core.request;

import com.yangbo.webserver.core.constant.CharConstant;
import com.yangbo.webserver.core.context.ServletContext;
import com.yangbo.webserver.core.context.WebApplication;
import com.yangbo.webserver.core.cookie.Cookie;
import com.yangbo.webserver.core.enumeration.RequestMethod;
import com.yangbo.webserver.core.exception.RequestInvalidException;
import com.yangbo.webserver.core.exception.RequestParseException;
import com.yangbo.webserver.core.network.handler.AbstractRequestHandler;
import com.yangbo.webserver.core.request.dispatcher.ApplicationRequestDispatcher;
import com.yangbo.webserver.core.request.dispatcher.RequestDispatcher;
import com.yangbo.webserver.core.session.HttpSession;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-22:02
 * @Description: 对发送请求的拆解
 *
 * 当 通过浏览器在url地址上发送  get/post/delete/put 请求时，服务器接收浏览器端的request请求，
 * 通过解析request 分析发送的  url等
 *  发送 此请求 http://localhost:8080/login?username=yangbo&sex=m
 *
 */
@Getter
@Setter
@Slf4j
public class Request {

    private AbstractRequestHandler requestHandler;
    private RequestMethod method;    //请求方法
    private String url;                //请求url
    private Map<String, List<String>> params;  //请求参数  name = yangbo
    private Map<String, List<String>> headers; //请求头信息
    private Map<String, Object> attributes;   //可以向请求头中添加数据  ，  并获取
    private ServletContext servletContext;
    private Cookie[] cookies;
    private HttpSession session;

    public RequestDispatcher getRequestDispatcher(String url) {
        return new ApplicationRequestDispatcher(url);
    }

    /**
     * 获取queryString或者body（表单格式）的键值类型的数据
     * name：yangbo  sex：m  age:18
     * private Map<String, List<String>> params;
     * @param key
     * @return
     */
    public String getParameter(String key) {
        List<String> params = this.params.get(key);
        if (params == null) {
            return null;
        }
        return params.get(0);
    }

    /**
     * Request 的构造方法
     * 解析HTTP请求
     * 读取请求体只能使用字节流，使用字符流读不到
     * @param data
     * @throws RequestInvalidException
     * @throws RequestParseException
     */
    public Request(byte[] data) throws RequestInvalidException, RequestParseException {
        this.attributes = new HashMap<>();
        String[] lines = null;
        try {
            //支持中文
            lines = URLDecoder.decode(new String(data, Charset.forName("UTF-8")), "UTF-8").split(CharConstant.CRLF);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Request读取完毕");
        log.info("请求行: {}", Arrays.toString(lines));
        if (lines.length <= 1) {
            throw new RequestInvalidException();
        }

        try {
            //解析请求头
            parseHeaders(lines);

            //判断是否有请求体
            if (headers.containsKey("Content-Length") && !headers.get("Content-Length").get(0).equals("0")) {
                parseBody(lines[lines.length - 1]);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RequestParseException();
        }
        WebApplication.getServletContext().afterRequestCreated(this);
    }

    public void setAttribute(String key,Object value){
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }


    /**
     * 如果请求报文中携带JSESSIONID这个Cookie，那么取出对应的session
     * 否则创建一个Session，并在响应报文中添加一个响应头Set-Cookie: JSESSIONID=D5A5C79F3C8E8653BC8B4F0860BFDBCD
     * 所有从请求报文中得到的Cookie，都会在响应报文中返回
     * 服务器只会在客户端第一次请求响应的时候，在响应头上添加Set-Cookie：“JSESSIONID=XXXXXXX”信息，
     * 接下来在同一个会话的第二第三次响应头里，是不会添加Set-Cookie：“JSESSIONID=XXXXXXX”信息的；
     * 即，如果在Cookie中读到的JSESSIONID，那么不会创建新的Session，也不会在响应头中加入Set-Cookie：“JSESSIONID=XXXXXXX”
     * 如果没有读到，那么会创建新的Session，并在响应头中加入Set-Cookie：“JSESSIONID=XXXXXXX”
     * 如果没有调用getSession，那么不会创建新的Session
     *
     * @param createIfNotExists 如果为true，那么在不存在session时会创建一个新的session；否则会直接返回null
     * @return HttpSession
     */
    public HttpSession getSession(boolean createIfNotExists) {
        if (session != null) {
            return session;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getKey().equals("JSESSIONID")) {
                HttpSession currentSession = servletContext.getSession(cookie.getValue());
                if (currentSession != null) {
                    session = currentSession;
                    return session;
                }
            }
        }
        if (!createIfNotExists) {
            return null;
        }
        session = servletContext.createSession(requestHandler.getResponse());
        return session;
    }

    public HttpSession getSession() {
        return getSession(true);
    }

    public String getServletPath() {
        return url;
    }


    //解析请求头  第一行是请求行  方法  url
    private void parseHeaders(String[] lines){
        log.info("开始解析请求行");
        String firstLine = lines[0];
        //解析方法
        String[] firstLineSlices = firstLine.split(CharConstant.BLANK);
        this.method = RequestMethod.valueOf(firstLineSlices[0]);  //GET
        log.debug("method:{}", this.method);

        //解析URL
        String rawURL = firstLineSlices[1];
        String[] urlSlices = rawURL.split("\\?");  //?分开
        this.url = urlSlices[0];        ///login.html   ?   uname=aaa&pwd=123456
        log.debug("url:{}", this.url);

        //解析URL参数
        if (urlSlices.length > 1) {
            parseParams(urlSlices[1]);
        }
        log.debug("params:{}", this.params);

        //解析请求头
        log.info("解析请求头...");
        String header;
        this.headers =new HashMap<>();
        //将 请求头 里面的键值对全部放入map中
        for (int i = 1; i < lines.length; i++) {
            header = lines[i];
            if (header.equals("")) {
                break;
            }
            int colonIndex = header.indexOf(':');
            String key = header.substring(0, colonIndex);
            String[] values = header.substring(colonIndex + 2).split(",");
            headers.put(key, Arrays.asList(values));
        }
        log.debug("headers:{}", this.headers);


        //解析Cookie   headers  Cookie ： xxx=xxx ; xxx=xxx ; token=xxx
        if (headers.containsKey("Cookie")) {    //get(0)是取第一个cookie
            String[] rawCookies = headers.get("Cookie").get(0).split("; ");
            //放入到cookie里面  数组
            this.cookies = new Cookie[rawCookies.length];
            //将 cookie 放入 cookies的数组中
            for (int i = 0; i < rawCookies.length; i++) {
                String[] kv = rawCookies[i].split("=");
                this.cookies[i] = new Cookie(kv[0], kv[1]);
            }
            headers.remove("Cookie");
        } else {
            this.cookies = new Cookie[0];
        }
        log.info("Cookies:{}", Arrays.toString(cookies));
    }

    private void parseBody(String body) {
        log.info("解析请求体");
        byte[] bytes = body.getBytes(Charset.forName("UTF-8"));
        List<String> lengths = this.headers.get("Content-Length");
        if (lengths != null) {
            int length = Integer.parseInt(lengths.get(0));
            log.info("length:{}", length);
            parseParams(new String(bytes, 0, Math.min(length,bytes.length), Charset.forName("UTF-8")).trim());
        } else {
            parseParams(body.trim());
        }
        if (this.params == null) {
            this.params = new HashMap<>();
        }
    }


    private void parseParams(String params) {
        //解析参数        uname=aaa  &  pwd=123456  &  sex=男
        //private Map<String, List<String>> params;  放入
        String[] urlParams = params.split("&");
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        for (String param : urlParams) {
            String[] kv = param.split("=");
            String key = kv[0];
            String[] values = kv[1].split(",");

            this.params.put(key, Arrays.asList(values));
        }
    }
}
