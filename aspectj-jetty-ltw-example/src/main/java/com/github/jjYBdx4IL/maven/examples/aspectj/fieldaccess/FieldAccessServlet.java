package com.github.jjYBdx4IL.maven.examples.aspectj.fieldaccess;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jjYBdx4IL
 */
@SuppressWarnings("serial")
public class FieldAccessServlet extends HttpServlet {

    /**
     * In this example we weave access to a field.
     * Servlet classes get instantiated only once per servlet context, they must be thread-safe.
     * The idea here is to "inject" a {@link java.lang.ThreadLocal<T>}, or rather return that one's value when
     * the field is accessed.
     */
    private String message = "Load-time weaving failed!";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/plain");
        resp.getWriter().append(message);
    }

}
