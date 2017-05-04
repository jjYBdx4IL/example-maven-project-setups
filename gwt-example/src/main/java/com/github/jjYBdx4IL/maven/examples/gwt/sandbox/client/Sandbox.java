package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import static com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.ResourceBundle.RES;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.events.CompletionEvent;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.events.LoginEvent;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.shared.ManualEventHandling;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.XsrfToken;
import java.util.logging.Logger;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Sandbox implements EntryPoint, ResizeHandler, CompletionEvent.Handler<XsrfToken>, LoginEvent.Handler {

    private static final Logger LOG = Logger.getLogger(ManualEventHandling.class.getName());

    private static final String rootDivId = "testPane";

    private MainPanel mainPanel = new MainPanel();

    private static EventBus eventBus = new SimpleEventBus();

    private static String jSessionId = Cookies.getCookie("JSESSIONID");

    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {
        LoginEvent.register(eventBus, this);

        if (jSessionId != null) {
            // already authenticated -> send fake LoginEvent to trigger XSRF token fetch
            getEventBus().fireEvent(new LoginEvent());
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

    public static boolean isAuthenticated() {
        return jSessionId != null;
    }

    /**
     * @return the eventBus
     */
    public static EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public void onLogin(LoginEvent event) {
        CompletionEvent.register(getEventBus(), this);
        XSRF.init(getEventBus());
    }
}
