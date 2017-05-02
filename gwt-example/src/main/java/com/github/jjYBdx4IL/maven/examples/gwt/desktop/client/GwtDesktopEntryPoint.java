package com.github.jjYBdx4IL.maven.examples.gwt.desktop.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import static com.github.jjYBdx4IL.maven.examples.gwt.desktop.client.GwtResourceBundle.RES;
import java.util.logging.Logger;

public class GwtDesktopEntryPoint implements EntryPoint {

    private static final Logger logger = Logger.getLogger(GwtDesktopEntryPoint.class.getName());

    @Override
    public void onModuleLoad() {
        logger.info("onModuleLoad()");
        RES.css().ensureInjected();
        RootPanel rootPanel = RootPanel.get("frontend");

        final DialogBox dialogBox2 = new DialogBox();
        dialogBox2.setTitle("Box Title");
        dialogBox2.setText("Box Text");
        dialogBox2.setModal(true);
        dialogBox2.setGlassEnabled(false);
        dialogBox2.setAutoHideEnabled(true);
        dialogBox2.setWidget(new DatePicker());

        final DatePicker datePicker = new DatePicker();

        final Button button = new Button("click me");
        rootPanel.add(new TextBox());
        rootPanel.add(button);

        button.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
//                dialogBox2.center();
//                dialogBox2.show();
                dialogBox2.showRelativeTo(button);
            }
        });
    }
}
