package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.server;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.shared.GWTService;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GWTServiceImpl extends XsrfProtectedServiceServlet implements GWTService {

    @Override
    public String greetme(String username) {
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
    public boolean login(String user, String password) {
        if (!password.equals("secret")) {
            return false;
        }
        
        HttpServletRequest request = this.getThreadLocalRequest();
        HttpSession session = request.getSession(true);
        
        session.setAttribute("username", user);
        session.setAttribute("authenticated", true);
        
        return true;
    }

}
