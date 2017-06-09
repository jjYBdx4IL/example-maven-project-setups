package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.dto.ExampleItemDTO;
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
    List<ExampleItemDTO> getExampleItems();
    void addExampleItem(ExampleItemDTO item);
    
    @NoXsrfProtect
    void login(String user, String password);
    void logout();
}
