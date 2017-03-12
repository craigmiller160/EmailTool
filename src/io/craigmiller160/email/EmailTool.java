package io.craigmiller160.email;

import io.craigmiller160.email.gui.EmailWindow;
import io.craigmiller160.email.gui.SendToListModel;
import io.craigmiller160.email.model.MessageModel;
import io.craigmiller160.email.model.SendFromModel;
import io.craigmiller160.email.model.SendToModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
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
import java.util.List;
import java.util.Optional;

import static io.craigmiller160.email.gui.SendToListModel.BCC_TITLE;
import static io.craigmiller160.email.gui.SendToListModel.CC_TITLE;
import static io.craigmiller160.email.gui.SendToListModel.TO_TITLE;
import static io.craigmiller160.email.model.MessageModel.ATTACHMENTS_PROP;
import static io.craigmiller160.email.model.MessageModel.BODY_PROP;
import static io.craigmiller160.email.model.MessageModel.SUBJECT_PROP;
import static io.craigmiller160.email.model.SendFromModel.AUTH_PROP;
import static io.craigmiller160.email.model.SendFromModel.HOST_PROP;
import static io.craigmiller160.email.model.SendFromModel.PASSWORD_PROP;
import static io.craigmiller160.email.model.SendFromModel.PORT_PROP;
import static io.craigmiller160.email.model.SendFromModel.START_TLS_PROP;
import static io.craigmiller160.email.model.SendFromModel.USERNAME_PROP;
import static io.craigmiller160.email.model.SendToModel.BCC_EMAIL_PROP;
import static io.craigmiller160.email.model.SendToModel.CC_EMAIL_PROP;
import static io.craigmiller160.email.model.SendToModel.TO_EMAIL_PROP;

/**
 * Created by craig on 3/6/17.
 */
public class EmailTool implements ActionListener, DocumentListener, TableModelListener, ListDataListener, ItemListener, ChangeListener, PropertyChangeListener{

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
    public static final String SAVE_PROP = "Save";
    public static final String LOAD_PROP = "Load";
    public static final String SAVE_AS_PROP = "SaveAs";
    public static final String NEW_PROP = "New";

    private SendFromModel sendFromModel;
    private SendToModel sendToModel;
    private MessageModel messageModel;
    private EmailWindow view;
    private volatile boolean isPropertyChanging = false;

    public EmailTool(){
        init();
    }

    private void init(){
        this.sendFromModel = new SendFromModel();
        this.sendFromModel.addPropertyChangeListener(this);

        this.sendToModel = new SendToModel();
        this.sendToModel.addPropertyChangeListener(this);

        this.messageModel = new MessageModel();
        this.messageModel.addPropertyChangeListener(this);

        this.view = new EmailWindow(this);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if(EXECUTE_PROP.equals(event.getActionCommand())){
            try{
                Email email = Email.newEmail(sendFromModel, sendToModel, messageModel);
                email.send();
            }
            catch(Exception ex){
                JOptionPane.showMessageDialog(view, "Unable to send email: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
        else if(NEW_PROP.equals(event.getActionCommand())){
            this.sendToModel.clear();
            this.sendFromModel.clear();
            this.messageModel.clear();
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
        if(!isPropertyChanging){
            isPropertyChanging = true;
            try{
                String propName = (String) event.getDocument().getProperty(PROP_NAME_PROP);
                String text = event.getDocument().getText(0, event.getDocument().getLength());
                if(USERNAME_PROP.equals(propName)){
                    sendFromModel.setUsername(text);
                }
                else if(PASSWORD_PROP.equals(propName)){
                    sendFromModel.setPassword(text);
                }
                else if(HOST_PROP.equals(propName)){
                    sendFromModel.setHost(text);
                }
                else if(SUBJECT_PROP.equals(propName)){
                    messageModel.setSubject(text);
                }
                else if(BODY_PROP.equals(propName)){
                    messageModel.setBody(text);
                }
            }
            catch(BadLocationException ex){
                ex.printStackTrace();
            }
            isPropertyChanging = false;
        }
    }

    @Override
    public void intervalAdded(ListDataEvent event) {
        if(!isPropertyChanging){
            isPropertyChanging = true;
            int index = event.getIndex0();
            DefaultListModel<String> source = (DefaultListModel<String>) event.getSource();
            String value = source.getElementAt(index);
            messageModel.addAttachment(value);
            isPropertyChanging = false;
        }
    }

    @Override
    public void intervalRemoved(ListDataEvent event) {
        if(!isPropertyChanging){
            isPropertyChanging = true;
            int index = event.getIndex0();
            messageModel.removeAttachment(index);
            isPropertyChanging = false;
        }
    }

    @Override
    public void contentsChanged(ListDataEvent event) {
        if(!isPropertyChanging){
            isPropertyChanging = true;
            int index = event.getIndex0();
            DefaultListModel<String> source = (DefaultListModel<String>) event.getSource();
            String value = source.getElementAt(index);
            messageModel.updateAttachment(value, index);
            isPropertyChanging = false;
        }
    }

    @Override
    public void tableChanged(TableModelEvent event) {
        if(!isPropertyChanging){
            isPropertyChanging = true;
            Object source = event.getSource();
            if(source instanceof SendToListModel){
                SendToListModel tableModel = (SendToListModel) source;
                String title = tableModel.getTitle();
                if(TO_TITLE.equals(title)){
                    if(event.getType() == TableModelEvent.INSERT){
                        sendToModel.addToEmail((String)tableModel.getValueAt(event.getFirstRow(), 0));
                    }
                    else if(event.getType() == TableModelEvent.DELETE){
                        sendToModel.removeToEmail(event.getFirstRow());
                    }
                    else if(event.getType() == TableModelEvent.UPDATE){
                        sendToModel.updateToEmail((String) tableModel.getValueAt(event.getFirstRow(), 0), event.getFirstRow());
                    }
                }
                else if(CC_TITLE.equals(title)){
                    if(event.getType() == TableModelEvent.INSERT){
                        sendToModel.addCCEmail((String)tableModel.getValueAt(event.getFirstRow(), 0));
                    }
                    else if(event.getType() == TableModelEvent.DELETE){
                        sendToModel.removeCCEmail(event.getFirstRow());
                    }
                    else if(event.getType() == TableModelEvent.UPDATE){
                        sendToModel.updateCCEmail((String) tableModel.getValueAt(event.getFirstRow(), 0), event.getFirstRow());
                    }
                }
                else if(BCC_TITLE.equals(title)){
                    if(event.getType() == TableModelEvent.INSERT){
                        sendToModel.addBCCEmail((String)tableModel.getValueAt(event.getFirstRow(), 0));
                    }
                    else if(event.getType() == TableModelEvent.DELETE){
                        sendToModel.removeBCCEmail(event.getFirstRow());
                    }
                    else if(event.getType() == TableModelEvent.UPDATE){
                        sendToModel.updateBCCEmail((String) tableModel.getValueAt(event.getFirstRow(), 0), event.getFirstRow());
                    }
                }
            }
            isPropertyChanging = false;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(!isPropertyChanging){
            isPropertyChanging = true;
            if(TO_EMAIL_PROP.equals(evt.getPropertyName())){
                view.setToEmails((List<String>) evt.getNewValue());
            }
            else if(CC_EMAIL_PROP.equals(evt.getPropertyName())){
                view.setCCEmails((List<String>) evt.getNewValue());
            }
            else if(BCC_EMAIL_PROP.equals(evt.getPropertyName())){
                view.setBCCEmails((List<String>) evt.getNewValue());
            }
            else if(HOST_PROP.equals(evt.getPropertyName())){
                view.setHost((String) evt.getNewValue());
            }
            else if(PORT_PROP.equals(evt.getPropertyName())){
                view.setPort((Integer) evt.getNewValue());
            }
            else if(START_TLS_PROP.equals(evt.getPropertyName())){
                view.setStartTLS((Boolean) evt.getNewValue());
            }
            else if(AUTH_PROP.equals(evt.getPropertyName())){
                view.setAuth((Boolean) evt.getNewValue());
            }
            else if(USERNAME_PROP.equals(evt.getPropertyName())){
                view.setUsername((String) evt.getNewValue());
            }
            else if(PASSWORD_PROP.equals(evt.getPropertyName())){
                view.setPassword((String) evt.getNewValue());
            }
            else if(SUBJECT_PROP.equals(evt.getPropertyName())){
                view.setSubject((String) evt.getNewValue());
            }
            else if(BODY_PROP.equals(evt.getPropertyName())){
                view.setBody((String) evt.getNewValue());
            }
            else if(ATTACHMENTS_PROP.equals(evt.getPropertyName())){
                view.setAttachments((List<String>) evt.getNewValue());
            }
            isPropertyChanging = false;
        }
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
