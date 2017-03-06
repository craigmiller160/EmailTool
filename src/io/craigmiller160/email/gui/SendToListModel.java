package io.craigmiller160.email.gui;

import io.craigmiller160.email.model.AbstractModel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by craig on 3/6/17.
 */
public class SendToListModel extends AbstractListModel<String> {

    private List<String> values = new ArrayList<>();

    public void setValues(List<String> values){
        this.values = values;
    }

    @Override
    public int getSize() {
        return values.size() + 1;
    }

    @Override
    public String getElementAt(int index) {
        return index == values.size() ? "" : values.get(index);
    }
}
