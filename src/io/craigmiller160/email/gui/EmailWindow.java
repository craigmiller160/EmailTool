package io.craigmiller160.email.gui;

import io.craigmiller160.email.EmailTool;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static io.craigmiller160.email.EmailTool.EXECUTE_PROP;
import static io.craigmiller160.email.EmailTool.IMPORT_BCC_PROP;
import static io.craigmiller160.email.EmailTool.IMPORT_CC_PROP;
import static io.craigmiller160.email.EmailTool.IMPORT_TO_PROP;
import static io.craigmiller160.email.EmailTool.LOAD_PROP;
import static io.craigmiller160.email.EmailTool.NEW_PROP;
import static io.craigmiller160.email.EmailTool.PROP_NAME_PROP;
import static io.craigmiller160.email.EmailTool.SAVE_AS_PROP;
import static io.craigmiller160.email.EmailTool.SAVE_PROP;
import static io.craigmiller160.email.model.MessageModel.BODY_PROP;
import static io.craigmiller160.email.model.MessageModel.SUBJECT_PROP;
import static io.craigmiller160.email.model.SendFromModel.AUTH_PROP;
import static io.craigmiller160.email.model.SendFromModel.HOST_PROP;
import static io.craigmiller160.email.model.SendFromModel.PASSWORD_PROP;
import static io.craigmiller160.email.model.SendFromModel.PORT_PROP;
import static io.craigmiller160.email.model.SendFromModel.START_TLS_PROP;
import static io.craigmiller160.email.model.SendFromModel.USERNAME_PROP;

/**
 * Created by craigmiller on 1/16/17.
 */
public class EmailWindow extends JFrame {

    //TODO need cell renderer with tooltips
    //TODO add button to load emails from file
    //TODO need save/load for all configurations feature

    private static final int TABLE_ROW_HEIGHT = 20;
    private static final Dimension SEND_TO_TABLE_DIMENSION = new Dimension(200, 200);
    private static final String ATTACHMENT_CMD = "AttachmentCmd";
    private static final String REMOVE_ATTACHMENT_CMD = "RemoveAttachmentCmd";
    private static final String REMOVE_TO_CMD = "RemoveTo";
    private static final String REMOVE_ALL_TO_CMD = "RemoveAllTo";
    private static final String REMOVE_CC_CMD = "RemoveCC";
    private static final String REMOVE_ALL_CC_CMD = "RemoveAllCC";
    private static final String REMOVE_BCC_CMD = "RemoveBCC";
    private static final String REMOVE_ALL_BCC_CMD = "RemoveAllBCC";

    /*
     * Options:
     *
     * 1) List of contacts
     * 2) BCC?
     * 3) Subject
     * 4) Body
     * 5) List of Attachments
     */

    private JTable toEmailList;
    private JTable ccEmailList;
    private JTable bccEmailList;
    private SendToListModel toEmailModel;
    private SendToListModel ccEmailModel;
    private SendToListModel bccEmailModel;
    private JButton importTo;
    private JButton importCC;
    private JButton importBCC;
    private JButton removeTo;
    private JButton removeCC;
    private JButton removeBCC;
    private JButton removeAllTo;
    private JButton removeAllCC;
    private JButton removeAllBCC;

    private JTextField username;
    private JPasswordField password;
    private JSpinner port;
    private JCheckBox auth;
    private JCheckBox startTLS;
    private JTextField host;

    private JTextField subject;
    private JTextArea body;
    private JList<String> attachments;
    private DefaultListModel<String> attachmentModel;
    private JButton attachFile;
    private JButton removeAttachment;

    private JButton sendEmail;

    private JButton saveConfig;
    private JButton saveAsConfig;
    private JButton loadConfig;
    private JButton newConfig;
    private JLabel saveNameLabel;

    private File lastLocation;

    private EmailTool controller;

    public EmailWindow(EmailTool controller){
        this.controller = controller;
        init();
    }

    public void clearAllValues(){
        //TODO finish this
    }

    private void init(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new JPanel(new MigLayout("fillx")));

        JLabel titleLabel = new JLabel("Mass Email Tool");
        titleLabel.setFont(Fonts.APP_TITLE_FONT);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5,3, 5,3));

        JPanel sendToPanel = buildSendToPanel();
        JPanel sendFromPanel = buildFromPanel();
        JPanel messagePanel = buildMessagePanel();
        JPanel bottomPanel = bottomPanel();
        JToolBar toolBar = createToolbar();

        getContentPane().add(toolBar, "dock north");
        getContentPane().add(titleLabel, "dock north");
        getContentPane().add(sendToPanel, "span 3, growx, pushx, wrap");
        getContentPane().add(sendFromPanel, "grow, push");
        getContentPane().add(messagePanel, "span 2, growx, pushx, wrap");
        getContentPane().add(bottomPanel, "span 3, growx, pushx");

        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildFromPanel(){
        JPanel fromPanel = new JPanel(new MigLayout());

        JLabel usernameLabel = new JLabel("Username: ");
        JLabel passwordLabel = new JLabel("Password: ");
        JLabel portLabel = new JLabel("SMTP Port: ");
        JLabel hostLabel = new JLabel("SMTP Host: ");

        username = new JTextField();
        username.getDocument().putProperty(PROP_NAME_PROP, USERNAME_PROP);
        username.getDocument().addDocumentListener(controller);
        username.setEnabled(false);

        password = new JPasswordField();
        password.getDocument().putProperty(PROP_NAME_PROP, PASSWORD_PROP);
        password.getDocument().addDocumentListener(controller);
        password.setEnabled(false);

        port = new JSpinner(new SpinnerNumberModel(0,0,Integer.MAX_VALUE,1));
        port.setName(PORT_PROP);
        port.addChangeListener(controller);

        auth = new JCheckBox("Use Auth");
        auth.setName(AUTH_PROP);
        auth.addItemListener(controller);
        auth.addItemListener(this::enableCredentials);

        startTLS = new JCheckBox("Start TLS");
        startTLS.setName(START_TLS_PROP);
        startTLS.addItemListener(controller);

        host = new JTextField();
        host.getDocument().putProperty(PROP_NAME_PROP, HOST_PROP);
        host.getDocument().addDocumentListener(controller);

        fromPanel.add(hostLabel, "");
        fromPanel.add(host, "growx, pushx, wrap");
        fromPanel.add(portLabel, "");
        fromPanel.add(port, "growx, pushx, wrap");
        fromPanel.add(auth, "skip 1, growx, pushx, wrap");
        fromPanel.add(startTLS, "skip 1, growx, pushx, wrap");
        fromPanel.add(usernameLabel, "");
        fromPanel.add(username, "growx, pushx, wrap");
        fromPanel.add(passwordLabel, "");
        fromPanel.add(password, "growx, pushx, wrap");

        fromPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Send From"));

        return fromPanel;
    }

    private void enableCredentials(ItemEvent event){
        if(event.getStateChange() == ItemEvent.SELECTED){
            username.setEnabled(true);
            password.setEnabled(true);
        }
        else{
            username.setText("");
            password.setText("");
            username.setEnabled(false);
            password.setEnabled(false);
        }
    }

    private JPanel bottomPanel(){
        JPanel sendPanel = new JPanel(new MigLayout());

        sendEmail = new JButton("Send Email");
        sendEmail.setActionCommand(EXECUTE_PROP);
        sendEmail.addActionListener(controller);

        sendPanel.add(sendEmail, "growx, pushx");

        return sendPanel;
    }

    private JPanel buildMessagePanel(){
        JPanel messagePanel = new JPanel(new MigLayout());

        JLabel subjectLabel = new JLabel("Subject: ");
        JLabel bodyLabel = new JLabel("Body: ");
        JLabel attachmentsLabel = new JLabel("Attachments: ");

        subject = new JTextField();
        subject.getDocument().putProperty(PROP_NAME_PROP, SUBJECT_PROP);
        subject.getDocument().addDocumentListener(controller);

        body = new JTextArea(10, 50);
        body.getDocument().putProperty(PROP_NAME_PROP, BODY_PROP);
        body.getDocument().addDocumentListener(controller);

        attachmentModel = new DefaultListModel<>();
        attachments = new JList<>(attachmentModel);
        attachments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        attachmentModel.addListDataListener(controller);

        attachFile = new JButton("Attach File");
        attachFile.setActionCommand(ATTACHMENT_CMD);
        attachFile.addActionListener(this::openFileChooser);

        removeAttachment = new JButton("Remove Attachment");
        removeAttachment.setActionCommand(REMOVE_ATTACHMENT_CMD);
        removeAttachment.addActionListener(this::removeAttachment);

        JScrollPane bodyScroll = new JScrollPane(body);
        JScrollPane attachmentScroll = new JScrollPane(attachments);

        messagePanel.add(subjectLabel, "");
        messagePanel.add(subject, "growx, pushx, wrap");
        messagePanel.add(bodyLabel, "");
        messagePanel.add(bodyScroll, "growx, pushx, wrap");
        messagePanel.add(attachmentsLabel, "");
        messagePanel.add(attachmentScroll, "growx, pushx, wrap");
        messagePanel.add(attachFile, "skip 1, split 2");
        messagePanel.add(removeAttachment, "");

        messagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Message"));

        return messagePanel;
    }

    private JPanel buildSendToPanel(){
        JPanel sendToPanel = new JPanel(new MigLayout());

        toEmailModel = new SendToListModel(SendToListModel.TO_TITLE);
        toEmailModel.addTableModelListener(controller);
        ccEmailModel = new SendToListModel(SendToListModel.CC_TITLE);
        ccEmailModel.addTableModelListener(controller);
        bccEmailModel = new SendToListModel(SendToListModel.BCC_TITLE);
        bccEmailModel.addTableModelListener(controller);

        toEmailList = createTable(toEmailModel);
        ccEmailList = createTable(ccEmailModel);
        bccEmailList = createTable(bccEmailModel);

        importTo = new JButton("Import");
        importTo.setActionCommand(IMPORT_TO_PROP);
        importTo.addActionListener(this::openFileChooser);

        importCC = new JButton("Import");
        importCC.setActionCommand(IMPORT_CC_PROP);
        importCC.addActionListener(this::openFileChooser);

        importBCC = new JButton("Import");
        importBCC.setActionCommand(IMPORT_BCC_PROP);
        importBCC.addActionListener(this::openFileChooser);

        removeTo = new JButton("Remove");
        removeTo.setActionCommand(REMOVE_TO_CMD);
        removeTo.addActionListener(this::removeRecipients);

        removeCC = new JButton("Remove");
        removeCC.setActionCommand(REMOVE_CC_CMD);
        removeCC.addActionListener(this::removeRecipients);

        removeBCC = new JButton("Remove");
        removeBCC.setActionCommand(REMOVE_BCC_CMD);
        removeBCC.addActionListener(this::removeRecipients);

        removeAllTo = new JButton("Clear");
        removeAllTo.setActionCommand(REMOVE_ALL_TO_CMD);
        removeAllTo.addActionListener(this::removeRecipients);

        removeAllCC = new JButton("Clear");
        removeAllCC.setActionCommand(REMOVE_ALL_CC_CMD);
        removeAllCC.addActionListener(this::removeRecipients);

        removeAllBCC = new JButton("Clear");
        removeAllBCC.setActionCommand(REMOVE_ALL_BCC_CMD);
        removeAllBCC.addActionListener(this::removeRecipients);


        JScrollPane toEmailScroll = new JScrollPane(toEmailList);
        JScrollPane ccEmailScroll = new JScrollPane(ccEmailList);
        JScrollPane bccEmailScroll = new JScrollPane(bccEmailList);

        JPanel toPanel = new JPanel(new MigLayout());
        JPanel ccPanel = new JPanel(new MigLayout());
        JPanel bccPanel = new JPanel(new MigLayout());

        toPanel.add(toEmailScroll, "growx, pushx, wrap");
        toPanel.add(importTo, "split 3, growx, pushx");
        toPanel.add(removeTo, "growx, pushx");
        toPanel.add(removeAllTo, "growx, pushx");
        toPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        ccPanel.add(ccEmailScroll, "growx, pushx, wrap");
        ccPanel.add(importCC, "split 3, growx, pushx");
        ccPanel.add(removeCC, "growx, pushx");
        ccPanel.add(removeAllCC, "growx, pushx");
        ccPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        bccPanel.add(bccEmailScroll, "growx, pushx, wrap");
        bccPanel.add(importBCC, "split 3, growx, pushx");
        bccPanel.add(removeBCC, "growx, pushx");
        bccPanel.add(removeAllBCC, "growx, pushx");
        bccPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        sendToPanel.add(toPanel, "growx, pushx");
        sendToPanel.add(ccPanel, "growx, pushx");
        sendToPanel.add(bccPanel, "growx, pushx");

        sendToPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Send To"));

        return sendToPanel;
    }

    private void removeRecipients(ActionEvent event){
        if(REMOVE_TO_CMD.equals(event.getActionCommand())){
            int selected = toEmailList.getSelectedRow();
            if(selected >= 0 && selected < toEmailModel.getRowCount() - 1){
                toEmailModel.setValueAt(null, selected, 0);
            }
        }
        else if(REMOVE_CC_CMD.equals(event.getActionCommand())){
            int selected = ccEmailList.getSelectedRow();
            if(selected >= 0 && selected < ccEmailModel.getRowCount() - 1){
                ccEmailModel.setValueAt(null, selected, 0);
            }
        }
        else if(REMOVE_BCC_CMD.equals(event.getActionCommand())){
            int selected = bccEmailList.getSelectedRow();
            if(selected >= 0 && selected < bccEmailModel.getRowCount() - 1){
                bccEmailModel.setValueAt(null, selected, 0);
            }
        }
        else if(REMOVE_ALL_TO_CMD.equals(event.getActionCommand())){
            toEmailModel.clear();
        }
        else if(REMOVE_ALL_CC_CMD.equals(event.getActionCommand())){
            ccEmailModel.clear();
        }
        else if(REMOVE_ALL_BCC_CMD.equals(event.getActionCommand())){
            bccEmailModel.clear();
        }
    }

    private void openFileChooser(ActionEvent event){
        JFileChooser fileChooser = new JFileChooser();
        if(lastLocation != null){
            fileChooser.setCurrentDirectory(lastLocation);
        }
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if(!IMPORT_TO_PROP.equals(event.getActionCommand()) && !IMPORT_CC_PROP.equals(event.getActionCommand()) &&
                !IMPORT_BCC_PROP.equals(event.getActionCommand()) && !ATTACHMENT_CMD.equals(event.getActionCommand())){
            fileChooser.setFileFilter(new FileNameExtensionFilter("Email Configurations", "emailconfig"));
        }

        int result = -1;
        if(ATTACHMENT_CMD.equals(event.getActionCommand()) || LOAD_PROP.equals(event.getActionCommand()) ||
                IMPORT_TO_PROP.equals(event.getActionCommand()) || IMPORT_CC_PROP.equals(event.getActionCommand()) ||
                IMPORT_BCC_PROP.equals(event.getActionCommand())){
            result = fileChooser.showOpenDialog(this);
        }
        else{
            result = fileChooser.showSaveDialog(this);
        }

        if(JFileChooser.APPROVE_OPTION == result){
            lastLocation = fileChooser.getCurrentDirectory();
            File file = fileChooser.getSelectedFile();
            if(ATTACHMENT_CMD.equals(event.getActionCommand())){
                attachmentModel.addElement(file.getAbsolutePath());
            }
            else if(LOAD_PROP.equals(event.getActionCommand())){
                controller.loadConfig(file);
            }
            else if(SAVE_PROP.equals(event.getActionCommand()) || SAVE_AS_PROP.equals(event.getActionCommand())){
                controller.saveConfig(file);
            }
            else if(IMPORT_TO_PROP.equals(event.getActionCommand())){
                controller.importToEmails(file);
            }
            else if(IMPORT_CC_PROP.equals(event.getActionCommand())){
                controller.importCCEmails(file);
            }
            else if(IMPORT_BCC_PROP.equals(event.getActionCommand())){
                controller.importBCCEmails(file);
            }
        }
    }

    private void removeAttachment(ActionEvent event){
        int selectedIndex = attachments.getSelectedIndex();
        attachmentModel.remove(selectedIndex);
    }

    private JTable createTable(TableModel model){
        JTable table = new JTable(model);
        table.setRowHeight(TABLE_ROW_HEIGHT);
        table.setDefaultRenderer(String.class, new CellRenderer());
        table.setPreferredScrollableViewportSize(SEND_TO_TABLE_DIMENSION);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        return table;
    }

    private JToolBar createToolbar(){
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        newConfig = new JButton("New");
        newConfig.setActionCommand(NEW_PROP);
        newConfig.addActionListener(controller);
        newConfig.setPreferredSize(new Dimension(50,50));

        saveConfig = new JButton("Save");
        saveConfig.setActionCommand(SAVE_PROP);
        saveConfig.addActionListener((e) -> {
            if(!StringUtils.isEmpty(saveNameLabel.getText())){
                controller.saveConfig(null);
            }
            else{
                openFileChooser(e);
            }
        });
        saveConfig.setPreferredSize(new Dimension(50,50));

        saveAsConfig = new JButton("Save As");
        saveAsConfig.setActionCommand(SAVE_AS_PROP);
        saveAsConfig.addActionListener(this::openFileChooser);
        saveAsConfig.setPreferredSize(new Dimension(70,50));

        loadConfig = new JButton("Load");
        loadConfig.setActionCommand(LOAD_PROP);
        loadConfig.addActionListener(this::openFileChooser);
        loadConfig.setPreferredSize(new Dimension(50,50));

        saveNameLabel = new JLabel();
        saveNameLabel.setFont(Fonts.SAVE_NAME_FONT);
        saveNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        toolBar.add(newConfig);
        toolBar.add(loadConfig);
        toolBar.add(saveConfig);
        toolBar.add(saveAsConfig); //TODO need save as behavior
        toolBar.addSeparator();
        toolBar.add(saveNameLabel);

        return toolBar;
    }

    public void setToEmails(java.util.List<String> emails){
        this.toEmailModel.setValues(emails != null ? emails : new ArrayList<>());
    }

    public void setCCEmails(java.util.List<String> emails){
        this.ccEmailModel.setValues(emails != null ? emails : new ArrayList<>());
    }

    public void setBCCEmails(java.util.List<String> emails){
        this.bccEmailModel.setValues(emails != null ? emails : new ArrayList<>());
    }

    public void updateToEmail(String email, int index){
        this.toEmailModel.setValueAt(email, index, 0);
    }

    public void updateCCEmail(String email, int index){
        this.ccEmailModel.setValueAt(email, index, 0);
    }

    public void updateBCCEmail(String email, int index){
        this.bccEmailModel.setValueAt(email, index, 0);
    }

    public void setHost(String host){
        this.host.setText(host != null ? host : "");
    }

    public void setPort(int port){
        this.port.setValue(port);
    }

    public void setAuth(boolean auth){
        this.auth.setSelected(auth);
    }

    public void setStartTLS(boolean startTLS){
        this.startTLS.setSelected(startTLS);
    }

    public void setUsername(String username){
        if(this.auth.isSelected()){
            this.username.setText(username != null ? username : "");
        }
    }

    public void setPassword(String password){
        if(this.auth.isSelected()){
            this.password.setText(password != null ? password : "");
        }
    }

    public void setSubject(String subject){
        this.subject.setText(subject != null ? subject : "");
    }

    public void setBody(String body){
        this.body.setText(body != null ? body : "");
    }

    public void setAttachments(java.util.List<String> attachments){
        this.attachmentModel.clear();
        if(attachments != null){
            for(String s : attachments){
                this.attachmentModel.addElement(s);
            }
        }
    }

    public void updateAttachment(String attachment, int index){
        if(!StringUtils.isEmpty(attachment) && index == this.attachmentModel.size()){
            this.attachmentModel.addElement(attachment);
        }
        else if(!StringUtils.isEmpty(attachment)){
            this.attachmentModel.setElementAt(attachment, index);
        }
        else{
            this.attachmentModel.remove(index);
        }
    }

    public void setSaveName(String saveName){
        this.saveNameLabel.setText(saveName);
    }

}
