package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.datagrid;

import com.google.gwt.cell.client.DatePickerCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class StackOverflow15161741 extends SimpleLayoutPanel {

    private static final Logger LOG = Logger.getLogger(StackOverflow15161741.class.getName());

    /**
     * A simple data type that represents a contact.
     */
    private static class Contact {

        private final String address;
        private Date birthday;
        private final String name;

        public Contact(String name, Date birthday, String address) {
            this.name = name;
            this.birthday = birthday;
            this.address = address;
        }
    }
    /**
     * The list of data to display.
     */
    private static final List<Contact> CONTACTS = Arrays.asList(
            new Contact("John",   new Date(100, 0, 1), "123 Fourth Avenue"),
            new Contact("Joe",    new Date(101, 0, 1), "22 Lance Ln"),
            new Contact("George", new Date(102, 0, 1), "1600 Pennsylvania Avenue"));
    CellTable<Contact> table;

    public StackOverflow15161741() {
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        table = new CellTable<Contact>(100);

        table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

        // Add a date column to show the birthday.
        DatePickerCell dateCell = new DatePickerCell();
        Column<Contact, Date> dateColumn = new Column<Contact, Date>(dateCell) {
            @Override
            public Date getValue(Contact object) {
                return object.birthday;
            }
        };

        // Add a field updater to be notified when the user enters a new name.
        dateColumn.setFieldUpdater(new FieldUpdater<Contact, Date>() {
            @Override
            public void update(int index, Contact object, Date value) {
                object.birthday = value;
            }
        });

        table.addColumn(dateColumn, new TextHeader("Birthday"));

        TextColumn<Contact> addressColumn = new TextColumn<Contact>() {
            @Override
            public String getValue(Contact object) {
                return object.address;
            }
        };

        table.addColumn(addressColumn, new TextHeader("Address"));

        // Add a selection model to handle user selection.
        final SingleSelectionModel<Contact> selectionModel = new SingleSelectionModel<Contact>();
        table.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                Contact selected = selectionModel.getSelectedObject();
                if (selected != null) {
                    //Window.alert("You selected: " + selected.name);
                    LOG.info("You selected: " + selected.name);
                }
            }
        });

        table.setRowData(CONTACTS);
        table.setSize("100%", "auto");
        add(table);
    }
}