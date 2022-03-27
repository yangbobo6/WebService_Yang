package com.yangbo.webserver.sample.web.servlet;

import com.yangbo.webserver.core.exception.base.ServletException;
import com.yangbo.webserver.core.request.Request;
import com.yangbo.webserver.core.response.Response;
import com.yangbo.webserver.core.servlet.Servlet;
import com.yangbo.webserver.core.servlet.impl.HttpServlet;

import java.io.IOException;

/**
 * @Author: yangbo
 * @Date: 2022-03-20-21:46
 * @Description:
 */
public class LogoutServlet extends HttpServlet {
    @Override
    public void doGet(Request request, Response response) throws ServletException, IOException {
        request.getRequestDispatcher("views/logout.html").forward(request,response);
    }

    @Override
    public void doPost(Request request, Response response) throws ServletException, IOException {
        request.getSession().removeAttribute("username");
        request.getSession().invalidate();
        response.sendRedirect("/login");
    }
}
