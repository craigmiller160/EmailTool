package io.craigmiller160.email;

import io.craigmiller160.email.gui.EmailWindow;
import io.craigmiller160.email.gui.SendToListModel;
import io.craigmiller160.email.model.MessageModel;
import io.craigmiller160.email.model.SaveModel;
import io.craigmiller160.email.model.SendFromModel;
import io.craigmiller160.email.model.SendToModel;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

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
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.craigmiller160.email.gui.SendToListModel.BCC_TITLE;
import static io.craigmiller160.email.gui.SendToListModel.CC_TITLE;
import static io.craigmiller160.email.gui.SendToListModel.TO_TITLE;
import static io.craigmiller160.email.model.MessageModel.ATTACHMENTS_PROP;
import static io.craigmiller160.email.model.MessageModel.BODY_PROP;
import static io.craigmiller160.email.model.MessageModel.SUBJECT_PROP;
import static io.craigmiller160.email.model.SaveModel.SAVE_NAME_PROP;
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
    public static final String IMPORT_TO_PROP = "ImportTo";
    public static final String IMPORT_CC_PROP = "ImportCC";
    public static final String IMPORT_BCC_PROP = "ImportBCC";

    private SendFromModel sendFromModel;
    private SendToModel sendToModel;
    private MessageModel messageModel;
    private SaveModel saveModel;
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

        this.saveModel = new SaveModel();
        this.saveModel.addPropertyChangeListener(this);

        this.view = new EmailWindow(this);

        try{
            CryptoUtil.getInstance();
        }
        catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Unable to configure encryption util, cannot save/load configurations", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if(EXECUTE_PROP.equals(event.getActionCommand())){
            try{
                Email email = Email.newEmail(sendFromModel, sendToModel, messageModel);
                email.send();
                JOptionPane.showMessageDialog(view, "Email was sent successfully to all recipients", "Email Sent", JOptionPane.INFORMATION_MESSAGE);
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
            this.saveModel.clear();
            System.out.println("Created new configuration");
        }
    }

    public void importToEmails(File file){
        try{
            List<String> emails = EmailImporter.importEmails(file);
            this.sendToModel.setToEmails(emails);
        }
        catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Unable to import emails: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void importCCEmails(File file){
        try{
            List<String> emails = EmailImporter.importEmails(file);
            this.sendToModel.setCCEmails(emails);
        }
        catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Unable to import emails: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void importBCCEmails(File file){
        try{
            List<String> emails = EmailImporter.importEmails(file);
            this.sendToModel.setBCCEmails(emails);
        }
        catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Unable to import emails: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadConfig(File file){
        if(!file.exists() && !file.isFile()){
            JOptionPane.showMessageDialog(view, "Cannot load configuration, not valid file", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.sendToModel.clear();
        this.sendFromModel.clear();
        this.messageModel.clear();

        try{
            PersistConfiguration.loadConfig(file, sendToModel, sendFromModel, messageModel);
            String saveName = FilenameUtils.getBaseName(file.getAbsolutePath());
            this.saveModel.setSaveName(saveName);
            this.saveModel.setSaveFile(file);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Failed to load configuration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveConfig(File file){
        if(file == null){
            file = saveModel.getSaveFile();
            if(file == null){
                System.err.println("Cannot find file to save configuration to");
                JOptionPane.showMessageDialog(view, "Cannot find file to save configuration to", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
        if(StringUtils.isEmpty(extension) || !SaveModel.EXTENSION.equals(extension)){
            String fileWithExt = FilenameUtils.getFullPath(file.getAbsolutePath()) + File.separator + FilenameUtils.getBaseName(file.getAbsolutePath()) + "." + SaveModel.EXTENSION;
            file = new File(fileWithExt);
        }

        try{
            PersistConfiguration.saveConfig(file, sendToModel, sendFromModel, messageModel);
            String saveName = FilenameUtils.getBaseName(file.getAbsolutePath());
            this.saveModel.setSaveName(saveName);
            this.saveModel.setSaveFile(file);
        }
        catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Failed to save configuration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                //TODO the events don't clearly specify the proper operations... can't seem to do a remove-all
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
                if(evt instanceof IndexedPropertyChangeEvent){
                    int index = ((IndexedPropertyChangeEvent) evt).getIndex();
                    view.updateToEmail((String) evt.getNewValue(), index);
                }
                else{
                    view.setToEmails((List<String>) evt.getNewValue());
                }
            }
            else if(CC_EMAIL_PROP.equals(evt.getPropertyName())){
                if(evt instanceof IndexedPropertyChangeEvent){
                    int index = ((IndexedPropertyChangeEvent) evt).getIndex();
                    view.updateCCEmail((String) evt.getNewValue(), index);
                }
                else{
                    view.setCCEmails((List<String>) evt.getNewValue());
                }
            }
            else if(BCC_EMAIL_PROP.equals(evt.getPropertyName())){
                if(evt instanceof IndexedPropertyChangeEvent){
                    int index = ((IndexedPropertyChangeEvent) evt).getIndex();
                    view.updateBCCEmail((String) evt.getNewValue(), index);
                }
                else{
                    view.setBCCEmails((List<String>) evt.getNewValue());
                }
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
                if(evt instanceof IndexedPropertyChangeEvent){
                    int index = ((IndexedPropertyChangeEvent) evt).getIndex();
                    view.updateAttachment((String) evt.getNewValue(), index);
                }
                else{
                    view.setAttachments((List<String>) evt.getNewValue());
                }
            }
            else if(SAVE_NAME_PROP.equals(evt.getPropertyName())){
                view.setSaveName((String) evt.getNewValue());
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
