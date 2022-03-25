package com.yangbo.webserver.core.network.handler;

import com.yangbo.webserver.core.response.Response;
import lombok.Getter;

/**
 * @Author: yangbo
 * @Date: 2022-03-25-21:15
 * @Description:
 */
@Getter
public abstract class AbstractRequestHandler {
    private Response response;
}
