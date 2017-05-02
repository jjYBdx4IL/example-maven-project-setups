package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.shared;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface GWTService extends RemoteService {

    String greetme(String username);
    void forceOOM();
}
