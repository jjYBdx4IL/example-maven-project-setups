package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.datagrid;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.DebugId;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.jpa.ExampleItem;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.ResourceBundle;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.List;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class JpaCellTableExample extends FlowPanel {

    private final CellTable<ExampleItem> table = new CellTable<>();
    private final Button addItemButton = new Button("add");
    private final TextBox data1TextBox = new TextBox();
    private final TextBox data2TextBox = new TextBox();

    public JpaCellTableExample() {
    }

    @Override
    protected void onLoad() {
        TextColumn<ExampleItem> idColumn = new TextColumn<ExampleItem>() {
            @Override
            public String getValue(ExampleItem object) {
                return Integer.toString(object.getId());
            }
            
        };
        table.addColumn(idColumn, "ID");
        
        TextColumn<ExampleItem> data1Column = new TextColumn<ExampleItem>() {
            @Override
            public String getValue(ExampleItem object) {
                return object.getData1();
            }
        };
        table.addColumn(data1Column, "Data1");

        TextColumn<ExampleItem> data2Column = new TextColumn<ExampleItem>() {
            @Override
            public String getValue(ExampleItem object) {
                return object.getData2();
            }
        };
        table.addColumn(data2Column, "Data2");

        TextColumn<ExampleItem> versionColumn = new TextColumn<ExampleItem>() {
            @Override
            public String getValue(ExampleItem object) {
                return Long.toString(object.getVersion());
            }
        };
        table.addColumn(versionColumn, "Version");

        // Add a selection model to handle user selection.
        final SingleSelectionModel<ExampleItem> selectionModel = new SingleSelectionModel<>();
        table.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                ExampleItem selected = selectionModel.getSelectedObject();
                if (selected != null) {
                    Window.alert("You selected: " + selected);
                }
            }
        });

        data1TextBox.ensureDebugId(DebugId.JpaCellTableExampleData1TextBox.name());
        data2TextBox.ensureDebugId(DebugId.JpaCellTableExampleData2TextBox.name());
        addItemButton.ensureDebugId(DebugId.JpaCellTableExampleAddButton.name());
        table.ensureDebugId(DebugId.JpaCellTableExampleTable.name());
        
        ScrollPanel tableScrollPanel = new ScrollPanel(table);
        tableScrollPanel.setAlwaysShowScrollBars(true);
        tableScrollPanel.setHeight("100%");
        
        add(data1TextBox);
        add(data2TextBox);
        add(addItemButton);
        add(tableScrollPanel);
        
        addItemButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ExampleItem item = new ExampleItem();
                item.setData1(data1TextBox.getText());
                item.setData2(data2TextBox.getText());
                ResourceBundle.asyncService.addExampleItem(item, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(Void result) {
                        updateTable();
                    }
                });
            }
        });
        
        updateTable();
    }
    
    private void updateTable() {
        ResourceBundle.asyncService.getExampleItems(new AsyncCallback<List<ExampleItem>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(List<ExampleItem> result) {
                table.setPageSize(result.size());
                table.setRowCount(result.size(), true);
                table.setRowData(0, result);
            }
        });
    }
}
