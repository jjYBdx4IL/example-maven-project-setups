package com.github.jjYBdx4IL.maven.examples.dynproxy;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ContentServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ContentServlet.class);
    
    @Override
    public void init() throws ServletException {
        LOG.info("ContentServlet.init()");
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/plain");
        resp.getWriter().append("Hello!");
    }
    
}
