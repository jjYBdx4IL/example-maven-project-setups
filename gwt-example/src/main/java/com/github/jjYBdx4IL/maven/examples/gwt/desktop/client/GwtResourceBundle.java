package com.github.jjYBdx4IL.maven.examples.gwt.desktop.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

interface GwtResourceBundle extends ClientBundle {

    static final GwtResourceBundle RES = GWT.create(GwtResourceBundle.class);

    @Source("GwtDesktopStyle.css")
    @CssResource.NotStrict
    GwtDesktopStyle css();
}
