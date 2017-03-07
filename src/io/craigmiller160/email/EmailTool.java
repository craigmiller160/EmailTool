package io.craigmiller160.email;

import io.craigmiller160.email.gui.EmailWindow;
import io.craigmiller160.email.model.SendFromModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by craig on 3/6/17.
 */
public class EmailTool implements ActionListener, DocumentListener, TableModelListener, ItemListener, ChangeListener, PropertyChangeListener{

    public static void main(String[] args){
        try {
            Optional<UIManager.LookAndFeelInfo> info = Arrays.stream(UIManager.getInstalledLookAndFeels())
                    .filter((laf) -> "Nimbus".equals(laf.getName()))
                    .findFirst();
            if(info.isPresent()){
                UIManager.setLookAndFeel(info.get().getClassName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new EmailTool();
    }

    public static final String PROP_NAME_PROP = "PropertyName";

    private SendFromModel sendFromModel;
    private EmailWindow view;
    private volatile boolean viewIsChangingProperty = false;

    public EmailTool(){
        init();
    }

    private void init(){
        this.sendFromModel = new SendFromModel();
        this.sendFromModel.addPropertyChangeListener(this);

        this.view = new EmailWindow(this);
    }

    @Override
    public void actionPerformed(ActionEvent event) {

    }

    @Override
    public void insertUpdate(DocumentEvent event) {

    }

    @Override
    public void removeUpdate(DocumentEvent event) {

    }

    @Override
    public void changedUpdate(DocumentEvent event) {

    }

    @Override
    public void tableChanged(TableModelEvent event) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    @Override
    public void itemStateChanged(ItemEvent e) {

    }

    @Override
    public void stateChanged(ChangeEvent e) {

    }
}
