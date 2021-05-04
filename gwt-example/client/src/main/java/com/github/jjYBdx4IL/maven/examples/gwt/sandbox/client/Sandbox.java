package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import dagger.Component;

import static com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.ResourceBundle.RES;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.events.CompletionEvent;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.events.LoginEvent;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.gin.DaggerInjector;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.gin.Injector;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.gin.InjectorModule_ProvideSimpleEventBusFactory;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.shared.ManualEventHandling;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.XsrfToken;
import java.util.logging.Logger;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Sandbox implements EntryPoint, ResizeHandler, CompletionEvent.Handler<XsrfToken>, LoginEvent.Handler {

    private static final Logger LOG = Logger.getLogger(ManualEventHandling.class.getName());

    private final String rootDivId = "testPane";

    private MainPanel mainPanel;// = Injector.INSTANCE.getMainPanel();
    private EventBus eventBus;// = Injector.INSTANCE.getEventBus();
    private XSRF xsrf;// = Injector.INSTANCE.getXSRF();

    private String jSessionId = Cookies.getCookie("JSESSIONID");

    public Sandbox() {
    }

    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {
        mainPanel = DaggerInjector.create().getMainPanel();
        eventBus = DaggerInjector.create().getEventBus();
        xsrf = DaggerInjector.create().getXSRF();
        
        LoginEvent.register(eventBus, this);
        CompletionEvent.register(eventBus, this);

        if (jSessionId != null) {
            // already authenticated -> send fake LoginEvent to trigger XSRF token fetch
            eventBus.fireEvent(new LoginEvent());
        }
        
        RES.getStyle().ensureInjected();
        RootLayoutPanel.get().add(mainPanel);
        Window.addResizeHandler(this);
    }

    @Override
    public void onResize(ResizeEvent event) {
        LOG.fine("onResize() -> " + Window.getClientWidth() + " x " + Window.getClientHeight());
    }

    @Override
    public void onCompletion(CompletionEvent<XsrfToken> event) {

    }

    @Override
    public void onLogin(LoginEvent event) {
        xsrf.init();
    }
}
