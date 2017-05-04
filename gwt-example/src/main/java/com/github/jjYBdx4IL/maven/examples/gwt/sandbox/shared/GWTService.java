package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.shared;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.server.rpc.NoXsrfProtect;
import com.google.gwt.user.server.rpc.XsrfProtect;

@RemoteServiceRelativePath("gwtrpc")
@XsrfProtect
public interface GWTService extends RemoteService {

    String greetme(String username);
    void forceOOM();
    
    @NoXsrfProtect
    void login(String user, String password);
    void logout();
}
