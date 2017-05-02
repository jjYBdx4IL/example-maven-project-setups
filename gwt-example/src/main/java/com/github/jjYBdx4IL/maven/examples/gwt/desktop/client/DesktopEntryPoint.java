package com.github.jjYBdx4IL.maven.examples.gwt.desktop.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.ResizableDialogBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import java.util.logging.Logger;

public class DesktopEntryPoint implements EntryPoint {

    private static final Logger logger = Logger.getLogger(DesktopEntryPoint.class.getName());

    @Override
    public void onModuleLoad() {
        logger.info("onModuleLoad()");

        ResizableDialogBox dialogBox = new ResizableDialogBox();
        dialogBox.setText("Box Title");
        SimpleLayoutPanel dialogBoxContentPanel = new SimpleLayoutPanel();
        dialogBoxContentPanel.setTitle("title");
        dialogBoxContentPanel.setSize("200px", "200px");
        dialogBox.add(dialogBoxContentPanel);
        logger.info(dialogBox.getHTML());

        RootLayoutPanel layoutPanel = RootLayoutPanel.get();
        layoutPanel.add(dialogBox);
        //rootPanel.getWidgetContainerElement(dialogBox).getStyle().setOverflow(Style.Overflow.VISIBLE);
    }
}
