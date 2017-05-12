package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.datagrid;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.view.client.ProvidesKey;
import java.util.Arrays;
import java.util.List;

public class CellTableFieldUpdaterExample extends SimpleLayoutPanel {

    /**
     * A simple data type that represents a contact with a unique ID.
     */
    private static class Contact {

        private static int nextId = 0;
        private final int id;
        private String name;

        public Contact(String name) {
            nextId++;
            this.id = nextId;
            this.name = name;
        }
    }
    /**
     * The list of data to display.
     */
    private static final List<Contact> CONTACTS = Arrays.asList(new Contact("John"), new Contact(
            "Joe"), new Contact("George"));
    /**
     * The key provider that allows us to identify Contacts even if a field changes. We identify contacts by their
     * unique ID.
     */
    private static final ProvidesKey<Contact> KEY_PROVIDER =
            new ProvidesKey<CellTableFieldUpdaterExample.Contact>() {
        @Override
        public Object getKey(Contact item) {
            return item.id;
        }
    };

    public CellTableFieldUpdaterExample() {
        // Create a CellTable with a key provider.
        final CellTable<Contact> table = new CellTable<Contact>(KEY_PROVIDER);

        // Add a text input column to edit the name.
        final TextInputCell nameCell = new TextInputCell();
        Column<Contact, String> nameColumn = new Column<Contact, String>(nameCell) {
            @Override
            public String getValue(Contact object) {
                // Return the name as the value of this column.
                return object.name;
            }
        };
        table.addColumn(nameColumn, "Name");

        // Add a field updater to be notified when the user enters a new name.
        nameColumn.setFieldUpdater(new FieldUpdater<Contact, String>() {
            @Override
            public void update(int index, Contact object, String value) {
                // Inform the user of the change.
                Window.alert("You changed the name of " + object.name + " to " + value);

                // Push the changes into the Contact. At this point, you could send an
                // asynchronous request to the server to update the database.
                object.name = value;

                // Redraw the table with the new data.
                table.redraw();
            }
        });

        // Push the data into the widget.
        table.setRowData(CONTACTS);

        // Add it to the root panel.
        table.setSize("100%", "auto");
        add(table);
    }
}