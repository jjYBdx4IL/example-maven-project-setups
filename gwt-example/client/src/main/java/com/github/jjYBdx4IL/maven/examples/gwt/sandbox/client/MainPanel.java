package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client;


import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.datagrid.CellTableExample;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.datagrid.CellTableFieldUpdaterComplexExample;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.datagrid.CellTableFieldUpdaterExample;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.datagrid.StackOverflow15161741;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.eventbus.SimpleEventBusDemo;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.handlermanager.HandlerManagerDemo;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.keyevents.KeyEventDemo;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.rpcdemo.RpcDemo;
import com.google.inject.Inject;
import java.util.logging.Logger;

public class MainPanel extends Composite implements ValueChangeHandler<String> {

    private static final Logger logger = Logger.getLogger(MainPanel.class.getName());
    TabLayoutPanel tabLayoutPanel;

    @Inject
    public MainPanel(final RpcDemo rpcDemo) {
        tabLayoutPanel = new TabLayoutPanel(1.5, Unit.EM);
        tabLayoutPanel.add(rpcDemo, "RpcDemo");
        tabLayoutPanel.add(new StackOverflow15161741(), "StackOverflow15161741");
        tabLayoutPanel.add(new SimpleEventBusDemo(), "SimpleEventBusDemo");
        tabLayoutPanel.add(new HandlerManagerDemo(), "HandlerManagerDemo");
        tabLayoutPanel.add(new KeyEventDemo(), "KeyEventDemo");
        tabLayoutPanel.add(new CellTableExample(), "CellTableExample");
        tabLayoutPanel.add(new CellTableFieldUpdaterExample(), "CellTableFieldUpdaterExample");
        tabLayoutPanel.add(new CellTableFieldUpdaterComplexExample(), "CellTableFieldUpdaterComplexExample");
        this.initWidget(tabLayoutPanel);

        String initToken = History.getToken();
        logger.info("initToken = " + initToken);
        int tabIdx;
        try {
            tabIdx = Integer.parseInt(initToken);
        } catch (NumberFormatException ex) {
            tabIdx = 0;
        }
        logger.info("tabIdx = "+tabIdx);
        tabLayoutPanel.selectTab(tabIdx);

        tabLayoutPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                History.newItem(Integer.toString(event.getSelectedItem()));
            }
        });

        History.addValueChangeHandler(this);
    }

    @Override
    protected void onAttach() {
        logger.fine("entering onAttach()");
        super.onAttach();
        logger.fine("leaving onAttach()");
    }

    @Override
    protected void onLoad() {
        logger.fine("entering onLoad()");
        super.onLoad();
        logger.fine("leaving onLoad()");
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        int tabIdx;

        try {
            tabIdx = Integer.parseInt(event.getValue());
        } catch (NumberFormatException ex) {
            tabIdx = 0;
        }

        logger.info("tabIdx = "+tabIdx);
        tabLayoutPanel.selectTab(tabIdx);
    }
}
