package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.shared.GWTService;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.shared.GWTServiceAsync;

public interface ResourceBundle extends ClientBundle {

    public static final ResourceBundle RES = GWT.create(ResourceBundle.class);
    public static final ClientMessages message = (ClientMessages) GWT.create(ClientMessages.class);
    public static final GWTServiceAsync asyncService = GWT.create(GWTService.class);
    
    @Source("Style.css")
    @CssResource.NotStrict
    Style getStyle();
}
