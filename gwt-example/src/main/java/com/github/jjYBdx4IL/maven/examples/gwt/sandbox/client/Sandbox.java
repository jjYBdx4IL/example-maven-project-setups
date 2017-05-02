package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import static com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.ResourceBundle.RES;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.shared.ManualEventHandling;
import java.util.logging.Logger;

/**
 * Entry point classes define
 * <code>onModuleLoad()</code>.
 */
public class Sandbox implements EntryPoint, ResizeHandler {

    private static final Logger LOG = Logger.getLogger(ManualEventHandling.class.getName());
    
    private static final String rootDivId = "testPane";
    
    private MainPanel mainPanel = new MainPanel();

    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {
        RES.getStyle().ensureInjected();
        RootLayoutPanel.get().add(mainPanel);
        Window.addResizeHandler(this);
    }

    @Override
    public void onResize(ResizeEvent event) {
        LOG.fine("onResize() -> "+Window.getClientWidth()+" x "+Window.getClientHeight());
    }
}
