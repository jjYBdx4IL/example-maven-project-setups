package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.gin;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.MainPanel;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.XSRF;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.rpcdemo.RpcDemo;
import com.google.gwt.event.shared.EventBus;
import dagger.Component;
//import com.google.gwt.inject.client.GinModules;
//import com.google.gwt.inject.client.Ginjector;

import javax.inject.Singleton;

/**
 *
 * @author jjYBdx4IL
 */
//@GinModules(InjectorModule.class)
@Singleton
@Component(modules = InjectorModule.class)
public interface Injector { //extends Ginjector {
//    public static final Injector INSTANCE = GWT.create(Injector.class);

    EventBus getEventBus();
    RpcDemo getRpcDemo();
    MainPanel getMainPanel();

    XSRF getXSRF();
}
