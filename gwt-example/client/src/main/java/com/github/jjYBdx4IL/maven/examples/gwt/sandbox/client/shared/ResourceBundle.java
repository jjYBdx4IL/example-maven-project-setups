package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.shared;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface ResourceBundle extends ClientBundle {

    public static final ResourceBundle RES = GWT.create(ResourceBundle.class);
    
    @Source("Style.css")
    @CssResource.NotStrict
    Style getStyle();
}
