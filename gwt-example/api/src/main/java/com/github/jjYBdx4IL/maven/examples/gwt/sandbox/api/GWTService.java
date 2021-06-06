package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.jpa.ExampleItem;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.server.rpc.NoXsrfProtect;
import com.google.gwt.user.server.rpc.XsrfProtect;
import java.util.List;

@RemoteServiceRelativePath("gwtrpc")
@XsrfProtect
public interface GWTService extends RemoteService {

    String greetme(String username);
    void forceOOM();
    List<ExampleItem> getExampleItems();
    void addExampleItem(ExampleItem item);
    
    @NoXsrfProtect
    void login(String user, String password);
    void logout();
}
