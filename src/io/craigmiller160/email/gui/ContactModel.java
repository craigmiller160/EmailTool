package io.craigmiller160.email.gui;

import io.craigmiller160.email.Contact;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by craigmiller on 1/16/17.
 */
public class ContactModel extends AbstractTableModel{

    private static final String[] columnNames = {"Name" ,"Email"};

    private final List<Contact> contactList = new ArrayList<>();

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String valString = aValue != null ? aValue.toString() : null;

        //TODO need to avoid IndexOutOfBoundsExceptions here
        Contact contact = contactList.get(rowIndex);
        if(columnIndex == 0){
            contact.setName(valString);
        }
        else{
            contact.setEmail(valString);
        }
    }

    @Override
    public int getRowCount() {
        return contactList.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        //TODO need to avoid IndexOutOfBoundsExceptions here
        Contact contact = contactList.get(rowIndex);
        if(columnIndex == 0){
            return contact.getName();
        }
        else{
            return contact.getEmail();
        }
    }

    public void newContact(){
        contactList.add(new Contact());
    }

    public void deleteContact(int rowIndex){
        //TODO need to avoid IndexOutOfBoundsExceptions here
        contactList.remove(rowIndex);
    }
}
