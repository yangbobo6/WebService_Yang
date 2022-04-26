package com.yangbo.webserver.core.listener.event;

import com.yangbo.webserver.core.session.HttpSession;

import java.util.EventObject;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-22:40
 * @Description:  session相关事件，生成session的时候调用，监听session
 */
public class HttpSessionEvent extends EventObject {

    private static final long serialVersionUID = -7622791603672342895L;
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    //自定义的httpsession
    public HttpSessionEvent(HttpSession source) {
        super(source);
    }

    public HttpSession getSession(){
        return (HttpSession) super.getSource();
    }
}
