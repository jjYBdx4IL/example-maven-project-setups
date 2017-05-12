package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.shared;


import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import static com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.shared.ResourceBundle.RES;
import java.util.logging.Logger;

public class ManualEventHandling extends VerticalPanel implements ValueChangeHandler<String> {

    private static final Logger LOG = Logger.getLogger(ManualEventHandling.class.getName());
    Label title;
    TextBox textBox1;
    TextBox textBox2;

    public ManualEventHandling() {
    }

    private void init() {
        RES.getStyle().ensureInjected();
        addStyleName(RES.getStyle().manualEventHandling());
        
        title = new Label(getClass().getName());
        textBox1 = new TextBox();
        textBox2 = new TextBox();

        add(title);
        add(textBox1);
        add(textBox2);

        textBox1.addValueChangeHandler(this);
        textBox2.addValueChangeHandler(this);
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

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        LOG.fine(event.toDebugString() + ", source: " + event.getSource().toString());
        if (event.getSource() == textBox1) {
            textBox2.setValue(event.getValue());
        } else if (event.getSource() == textBox2) {
            textBox1.setValue(event.getValue());
        } else {
            textBox1.setValue(event.getValue());
            textBox2.setValue(event.getValue());
        }
    }
}
