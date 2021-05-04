package com.github.jjYBdx4IL.maven.examples.dynproxy;

import java.io.IOException;
import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 * @author jjYBdx4IL
 */
public class AOPProxyServlet extends GenericServlet {

    private Servlet delegateProxy = null;
    
    public AOPProxyServlet() {
    }

    @Override
    public void init() throws ServletException {
        super.init();
        String delegateClassName = getInitParameter("delegateClass");
        try {
            delegateProxy = ServletProxy.newInstance((Servlet) Class.forName(delegateClassName).newInstance());
            delegateProxy.init(getServletConfig());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | ServletException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    public void destroy() {
        delegateProxy.destroy();
        super.destroy();
    }
    
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        delegateProxy.service(req, res);
    }

}
