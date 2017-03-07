package io.craigmiller160.email;

import io.craigmiller160.email.gui.EmailWindow;
import io.craigmiller160.email.gui.SendToListModel;
import io.craigmiller160.email.model.SendFromModel;
import io.craigmiller160.email.model.SendToModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Optional;

import static io.craigmiller160.email.gui.SendToListModel.BCC_TITLE;
import static io.craigmiller160.email.gui.SendToListModel.CC_TITLE;
import static io.craigmiller160.email.gui.SendToListModel.TO_TITLE;
import static io.craigmiller160.email.model.SendFromModel.AUTH_PROP;
import static io.craigmiller160.email.model.SendFromModel.PASSWORD_PROP;
import static io.craigmiller160.email.model.SendFromModel.PORT_PROP;
import static io.craigmiller160.email.model.SendFromModel.START_TLS_PROP;
import static io.craigmiller160.email.model.SendFromModel.USERNAME_PROP;

/**
 * Created by craig on 3/6/17.
 */
public class EmailTool implements ActionListener, DocumentListener, TableModelListener, ItemListener, ChangeListener, PropertyChangeListener{

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            setLAF();
            new EmailTool();
        });
    }

    private static void setLAF(){
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
    }

    public static final String PROP_NAME_PROP = "PropertyName";
    public static final String EXECUTE_PROP = "Execute";

    private SendFromModel sendFromModel;
    private SendToModel sendToModel;
    private EmailWindow view;
    private volatile boolean viewIsChangingProperty = false;

    public EmailTool(){
        init();
    }

    private void init(){
        this.sendFromModel = new SendFromModel();
        this.sendFromModel.addPropertyChangeListener(this);

        this.sendToModel = new SendToModel();
        this.sendFromModel.addPropertyChangeListener(this);

        this.view = new EmailWindow(this);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if(EXECUTE_PROP.equals(event.getActionCommand())){
            //TODO this is where the email sending execution logic goes
        }
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        documentChanged(event);
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        documentChanged(event);
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
        documentChanged(event);
    }

    private void documentChanged(DocumentEvent event){
        try{
            String propName = (String) event.getDocument().getProperty(PROP_NAME_PROP);
            String text = event.getDocument().getText(0, event.getDocument().getLength());
            if(USERNAME_PROP.equals(propName)){
                sendFromModel.setUsername(text);
            }
            else if(PASSWORD_PROP.equals(propName)){
                sendFromModel.setPassword(text);
            }
        }
        catch(BadLocationException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void tableChanged(TableModelEvent event) {
        Object source = event.getSource();
        if(source instanceof SendToListModel){
            SendToListModel model = (SendToListModel) source;
            String title = model.getTitle();
            if(TO_TITLE.equals(title)){
                if(event.getType() == TableModelEvent.INSERT){

                }
                else if(event.getType() == TableModelEvent.DELETE){

                }
                else if(event.getType() == TableModelEvent.UPDATE){

                }
            }
            else if(CC_TITLE.equals(title)){
                if(event.getType() == TableModelEvent.INSERT){

                }
                else if(event.getType() == TableModelEvent.DELETE){

                }
                else if(event.getType() == TableModelEvent.UPDATE){

                }
            }
            else if(BCC_TITLE.equals(title)){
                if(event.getType() == TableModelEvent.INSERT){

                }
                else if(event.getType() == TableModelEvent.DELETE){

                }
                else if(event.getType() == TableModelEvent.UPDATE){

                }
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //TODO this is where property changes that should update the view go
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        JComponent comp = (JComponent) e.getSource();
        boolean state = e.getStateChange() == ItemEvent.SELECTED;
        if(AUTH_PROP.equals(comp.getName())){
            sendFromModel.setAuth(state);
        }
        else if(START_TLS_PROP.equals(comp.getName())){
            sendFromModel.setStartTLS(state);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JComponent comp = (JComponent) e.getSource();
        if(PORT_PROP.equals(comp.getName())){
            JSpinner spinner = (JSpinner) comp;
            sendFromModel.setPort((int) spinner.getValue());
        }
    }
}
