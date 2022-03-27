package com.yangbo.webserver.core.exception.handler;

import com.yangbo.webserver.core.exception.RequestInvalidException;
import com.yangbo.webserver.core.exception.base.ServletException;
import com.yangbo.webserver.core.network.wrapper.SocketWrapper;
import com.yangbo.webserver.core.response.Header;
import com.yangbo.webserver.core.response.Response;
import com.yangbo.webserver.core.utils.IOUtil;
import lombok.extern.slf4j.Slf4j;

import static com.yangbo.webserver.core.constant.ContextConstant.ERROR_PAGE;

/**
 * @Author: yangbo
 * @Date: 2022-03-27-11:53
 * @Description: 异常处理器
 * 会根据异常对应的HTTP Status设置response的状态以及相应的错误页面
 */
@Slf4j
public class ExceptionHandler {
    public void handler(ServletException e, Response response, SocketWrapper socketWrapper) {
        try {
            if(e instanceof RequestInvalidException){
                log.info("请求无效");
                socketWrapper.close();

            }else {
                log.info("抛出异常：{}",e.getClass().getName());
                e.printStackTrace();
                response.addHeader(new Header("Collection","close"));
                response.setStatus(e.getStatus());
                response.setBody(IOUtil.getByteFromFile(String.format(ERROR_PAGE, String.valueOf(e.getStatus().getCode()))));
            }
        }catch (Exception e1){
            e1.printStackTrace();
        }
    }

}
