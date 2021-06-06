package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.gin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
//import com.google.gwt.inject.client.AbstractGinModule;
//import com.google.inject.Singleton;
import dagger.Module;
import dagger.Provides;

/**
 *
 * @author jjYBdx4IL
 */

@Module
public class InjectorModule { // extends AbstractGinModule {
    
    @Provides
    EventBus provideSimpleEventBus() {
        return new SimpleEventBus();
    }
    
//    @Override
//    protected void configure() {
//        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
//    }
}