package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.server;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.shared.GWTService;
import java.util.HashSet;
import java.util.Set;

public class GWTServiceImpl extends RemoteServiceServlet implements GWTService {

    @Override
    public String greetme(String username) {
        return "Hello "+username.toUpperCase()+"!";
    }

    @Override
    public void forceOOM() {
        Set<byte[]> set = new HashSet<byte[]>();
        while(true) {
            set.add(new byte[1048576]);
        }
    }

}
