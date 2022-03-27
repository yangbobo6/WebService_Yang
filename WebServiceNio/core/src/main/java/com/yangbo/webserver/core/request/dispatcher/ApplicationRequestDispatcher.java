package com.yangbo.webserver.core.request.dispatcher;

import com.yangbo.webserver.core.exception.ResourceNotFoundException;
import com.yangbo.webserver.core.exception.base.ServletException;
import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.request.dispatcher.RequestDispatcher;
import com.yangbo.webserver.core.resource.ResourceHandler;
import com.yangbo.webserver.core.response.Response;
import com.yangbo.webserver.core.template.TemplateResolver;
import com.yangbo.webserver.core.utils.IOUtil;
import com.yangbo.webserver.core.utils.MimeTypeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @Author: yangbo
 * @Date: 2022-03-27-11:02
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ApplicationRequestDispatcher implements RequestDispatcher {
    private String url;

    @Override
    public void forward(Request request, Response response) throws ServletException, IOException {
        if (ResourceHandler.class.getResource(url) == null) {
            throw new ResourceNotFoundException();
        }
        log.info("forward至{}页面", url);
        String body = TemplateResolver.resolve(new String(IOUtil.getByteFromFile(url), Charset.forName("UTF-8")), request);
        response.setContentType(MimeTypeUtil.getTypes(url));
        response.setBody(body.getBytes(Charset.forName("UTF-8")));
    }
}
