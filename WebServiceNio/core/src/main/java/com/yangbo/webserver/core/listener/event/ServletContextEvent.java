package com.yangbo.webserver.core.listener.event;

import com.yangbo.webserver.core.context.ServletContext;

import java.util.EventObject;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-22:40
 * @Description:
 */
public class ServletContextEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ServletContextEvent(ServletContext source) {
        super(source);
    }

    public ServletContext getServletContext(){
        return (ServletContext)super.getSource();
    }


}
