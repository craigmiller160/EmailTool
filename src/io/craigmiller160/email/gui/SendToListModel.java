package io.craigmiller160.email.gui;

import io.craigmiller160.email.model.AbstractModel;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by craig on 3/6/17.
 */
public class SendToListModel extends AbstractTableModel {

    public static final String TO_TITLE = "To";
    public static final String CC_TITLE = "CC";
    public static final String BCC_TITLE = "BCC";

    private List<String> values = new ArrayList<>();
    private String title;

    public SendToListModel(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setValues(List<String> values){
        this.values = new ArrayList<>(values);
        fireTableDataChanged();
    }

    public void clear(){
        this.values = new ArrayList<>();
        fireTableDataChanged();
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
        if(rowIndex == values.size() && aValue != null && !((String) aValue).trim().isEmpty()){
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

    public static class ExtendedTableModelEvent extends TableModelEvent{

        public static final int DELETE_ALL = 2;

        public ExtendedTableModelEvent(TableModel source) {
            super(source);
        }

        public ExtendedTableModelEvent(TableModel source, int row) {
            super(source, row);
        }

        public ExtendedTableModelEvent(TableModel source, int firstRow, int lastRow) {
            super(source, firstRow, lastRow);
        }

        public ExtendedTableModelEvent(TableModel source, int firstRow, int lastRow, int column) {
            super(source, firstRow, lastRow, column);
        }

        public ExtendedTableModelEvent(TableModel source, int firstRow, int lastRow, int column, int type) {
            super(source, firstRow, lastRow, column, type);
        }
    }
}
