package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.server;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.shared.GWTService;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GWTServiceImpl extends XsrfProtectedServiceServlet implements GWTService {

    private static final String SESSION_ATTRNAME_USER = "username";
    private static final String SESSION_ATTRNAME_AUTHENTICATED = "authenticated";
    
    @Override
    public String greetme(String username) {
        Boolean authenticated = (Boolean) getSession().getAttribute(SESSION_ATTRNAME_AUTHENTICATED);
        if (authenticated == null || !authenticated.booleanValue()) {
            return "Hello unauthenticated user!";
        }
        return "Hello " + username.toUpperCase() + "!";
    }

    @Override
    public void forceOOM() {
        Set<byte[]> set = new HashSet<byte[]>();
        while (true) {
            set.add(new byte[1048576]);
        }
    }

    @Override
    public void login(String user, String password) {
        if (!password.equals("secret")) {
            throw new RuntimeException("auth failed");
        }
        
        getSession().setAttribute(SESSION_ATTRNAME_USER, user);
        getSession().setAttribute(SESSION_ATTRNAME_AUTHENTICATED, true);
    }
    
    private HttpSession getSession() {
        HttpServletRequest request = this.getThreadLocalRequest();
        HttpSession session = request.getSession();
        return session;
    }

    @Override
    public void logout() {
        getSession().removeAttribute(SESSION_ATTRNAME_USER);
        getSession().removeAttribute(SESSION_ATTRNAME_AUTHENTICATED);
        
    }

}
