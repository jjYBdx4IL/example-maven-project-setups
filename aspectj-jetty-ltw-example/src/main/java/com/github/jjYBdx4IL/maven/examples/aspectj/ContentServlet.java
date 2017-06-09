package com.github.jjYBdx4IL.maven.examples.aspectj;

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
@SuppressWarnings("serial")
@Tx
public class ContentServlet extends HttpServlet implements ISetMessage {

    private static final Logger LOG = LoggerFactory.getLogger(ContentServlet.class);
    
    private String message = "Load-time weaving failed!";
    
    @Override
    public void init() throws ServletException {
        LOG.info("ContentServlet.init()");
        LOG.info("before super.init() call");
        super.init();
        LOG.info("after super.init() call");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/plain");
        resp.getWriter().append(message);
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

}
