package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.datagrid;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.DebugId;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.dto.ExampleItemDTO;
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

public class JpaCellTableExample extends FlowPanel {

    private final CellTable<ExampleItemDTO> table = new CellTable<>();
    private final Button addItemButton = new Button("add");
    private final TextBox data1TextBox = new TextBox();
    private final TextBox data2TextBox = new TextBox();

    public JpaCellTableExample() {
    }

    @Override
    protected void onLoad() {
        // Add a text column to show the name.
        TextColumn<ExampleItemDTO> data1Column = new TextColumn<ExampleItemDTO>() {
            @Override
            public String getValue(ExampleItemDTO object) {
                return object.getData1();
            }
        };
        table.addColumn(data1Column, "Data1");

        // Add a date column to show the birthday.
        TextColumn<ExampleItemDTO> data2Column = new TextColumn<ExampleItemDTO>() {
            @Override
            public String getValue(ExampleItemDTO object) {
                return object.getData2();
            }
        };
        table.addColumn(data2Column, "Data2");

        // Add a selection model to handle user selection.
        final SingleSelectionModel<ExampleItemDTO> selectionModel = new SingleSelectionModel<>();
        table.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                ExampleItemDTO selected = selectionModel.getSelectedObject();
                if (selected != null) {
                    Window.alert("You selected: " + selected);
                }
            }
        });

        // Add it to the root panel.
        table.setSize("100%", "auto");
        
        data1TextBox.ensureDebugId(DebugId.JpaCellTableExampleData1TextBox.name());
        data2TextBox.ensureDebugId(DebugId.JpaCellTableExampleData2TextBox.name());
        addItemButton.ensureDebugId(DebugId.JpaCellTableExampleAddButton.name());
        table.ensureDebugId(DebugId.JpaCellTableExampleTable.name());
        
        add(data1TextBox);
        add(data2TextBox);
        add(addItemButton);
        add(table);
        
        addItemButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ExampleItemDTO item = new ExampleItemDTO(data1TextBox.getText(), data2TextBox.getText());
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
        ResourceBundle.asyncService.getExampleItems(new AsyncCallback<List<ExampleItemDTO>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(List<ExampleItemDTO> result) {
                table.setRowCount(result.size(), true);
                table.setRowData(0, result);
            }
        });
    }
}
