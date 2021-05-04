package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.shared;


import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import static com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.shared.ResourceBundle.RES;
import java.util.logging.Logger;

public abstract class AbstractEventDemo extends FlowPanel implements ValueChangeHandler<String>, HasValueChangeHandlers<String> {

    private static final Logger LOG = Logger.getLogger(AbstractEventDemo.class.getName());
    private Label title;
    private TextBox textBox;
    private ManualEventHandling manualEventBusTextBoxes;

    public AbstractEventDemo() {
    }

    protected abstract void preInit();

    private void init() {
        RES.getStyle().ensureInjected();

        preInit();

        title = new Label(getClass().getName());
        textBox = new TextBox();
        manualEventBusTextBoxes = new ManualEventHandling();

        add(title);
        add(textBox);
        add(manualEventBusTextBoxes);

        textBox.addValueChangeHandler(this);
        addValueChangeHandler(manualEventBusTextBoxes);
    }

    @Override
    protected void onAttach() {
        LOG.fine("entering onAttach()");
        super.onAttach();
        LOG.fine("leaving onAttach()");
    }

    @Override
    protected void onLoad() {
        LOG.fine("entering onLoad()");
        init();
        super.onLoad();
        LOG.fine("leaving onLoad()");
    }
}
