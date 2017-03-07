package io.craigmiller160.email.gui;

import io.craigmiller160.email.model.AbstractModel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by craig on 3/6/17.
 */
public class SendToListModel extends AbstractTableModel {

    private List<String> values = new ArrayList<>();
    private String title;

    public SendToListModel(String title){
        this.title = title;
    }

    public void setValues(List<String> values){
        this.values = values;
    }

    @Override
    public int getRowCount() {
        return values.size() + 1;
    }

    @Override
    public String getColumnName(int column) {
        return title;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(rowIndex == values.size()){
            values.add((String) aValue);
            fireTableRowsInserted(rowIndex, rowIndex);
        }
        else{
            if(aValue == null || ((String) aValue).isEmpty()){
                values.remove(rowIndex);
                fireTableRowsDeleted(rowIndex, rowIndex);
            }
            else{
                values.set(rowIndex, (String) aValue);
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rowIndex == values.size() ? "" : values.get(rowIndex);
    }
}
