package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.gin;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.MainPanel;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.XSRF;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.rpcdemo.RpcDemo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 *
 * @author jjYBdx4IL
 */
@GinModules(InjectorModule.class)
public interface Injector extends Ginjector {
    public static final Injector INSTANCE = GWT.create(Injector.class);

    public EventBus getEventBus();
    public RpcDemo getRpcDemo();
    public MainPanel getMainPanel();

    public XSRF getXSRF();
}
