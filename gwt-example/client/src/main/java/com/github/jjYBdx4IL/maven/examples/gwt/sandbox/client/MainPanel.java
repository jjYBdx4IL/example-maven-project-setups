package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client;


import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.DebugId;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.chat.ChatDemo;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.datagrid.CellTableExample;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.datagrid.CellTableFieldUpdaterComplexExample;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.datagrid.CellTableFieldUpdaterExample;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.datagrid.JpaCellTableExample;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.datagrid.StackOverflow15161741;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.eventbus.SimpleEventBusDemo;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.handlermanager.HandlerManagerDemo;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.keyevents.KeyEventDemo;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.rpcdemo.RpcDemo;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

@SuppressWarnings("LoggerStringConcat")
@Singleton
public class MainPanel extends Composite implements ValueChangeHandler<String> {

    private static final Logger LOG = Logger.getLogger(MainPanel.class.getName());
    private final StackLayoutPanel layoutPanel = new StackLayoutPanel(Unit.EM);
    private final double headerSize = 2.3d;

    @Inject
    public MainPanel(final RpcDemo rpcDemo) {
        layoutPanel.add(rpcDemo, "RpcDemo", headerSize);
        layoutPanel.add(new StackOverflow15161741(), "StackOverflow15161741", headerSize);
        layoutPanel.add(new SimpleEventBusDemo(), "SimpleEventBusDemo", headerSize);
        layoutPanel.add(new HandlerManagerDemo(), "HandlerManagerDemo", headerSize);
        layoutPanel.add(new KeyEventDemo(), "KeyEventDemo", headerSize);
        layoutPanel.add(new CellTableExample(), "CellTableExample", headerSize);
        layoutPanel.add(new CellTableFieldUpdaterExample(), "CellTableFieldUpdaterExample", headerSize);
        layoutPanel.add(new CellTableFieldUpdaterComplexExample(), "CellTableFieldUpdaterComplexExample", headerSize);
        layoutPanel.add(new ChatDemo(), "ChatDemo", headerSize);
        layoutPanel.add(new JpaCellTableExample(), "JpaCellTableExample", headerSize);
        layoutPanel.getHeaderWidget(layoutPanel.getWidgetCount()-1).ensureDebugId(DebugId.JpaCellTableExample.name());
        this.initWidget(layoutPanel);

        String initToken = History.getToken();
        LOG.info("initToken = " + initToken);
        int tabIdx;
        try {
            tabIdx = Integer.parseInt(initToken);
        } catch (NumberFormatException ex) {
            tabIdx = 0;
        }
        LOG.info("tabIdx = "+tabIdx);
        layoutPanel.showWidget(tabIdx);

        layoutPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                History.newItem(Integer.toString(event.getSelectedItem()));
            }
        });

        History.addValueChangeHandler(this);
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
        super.onLoad();
        LOG.fine("leaving onLoad()");
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        int tabIdx;

        try {
            tabIdx = Integer.parseInt(event.getValue());
        } catch (NumberFormatException ex) {
            tabIdx = 0;
        }

        LOG.info("tabIdx = "+tabIdx);
        layoutPanel.showWidget(tabIdx);
    }
}
