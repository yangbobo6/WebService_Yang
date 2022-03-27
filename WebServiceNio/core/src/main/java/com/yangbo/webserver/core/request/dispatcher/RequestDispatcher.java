package com.yangbo.webserver.core.request.dispatcher;

import com.yangbo.webserver.core.exception.base.ServletException;
import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.response.Response;

import java.io.IOException;

/**
 * @Author: yangbo
 * @Date: 2022-03-27-11:01
 * @Description:
 */
public interface RequestDispatcher {
    void forward(Request request, Response response) throws ServletException, IOException;
}
