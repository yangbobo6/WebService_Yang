package com.yangbo.webserver.core.network.wrapper;

import java.io.IOException;

/**
 * @Author: yangbo
 * @Date: 2022-03-27-9:27
 * @Description:
 */
public interface SocketWrapper {
    void close() throws IOException;
}
