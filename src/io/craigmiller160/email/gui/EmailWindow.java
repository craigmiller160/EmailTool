package io.craigmiller160.email.gui;

import io.craigmiller160.email.EmailTool;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import static io.craigmiller160.email.EmailTool.EXECUTE_PROP;
import static io.craigmiller160.email.EmailTool.PROP_NAME_PROP;
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

    private EmailTool controller;

    public EmailWindow(EmailTool controller){
        this.controller = controller;
        init();
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

        JScrollPane toEmailScroll = new JScrollPane(toEmailList);
        JScrollPane ccEmailScroll = new JScrollPane(ccEmailList);
        JScrollPane bccEmailScroll = new JScrollPane(bccEmailList);

        sendToPanel.add(toEmailScroll, "growx, pushx");
        sendToPanel.add(ccEmailScroll, "growx, pushx");
        sendToPanel.add(bccEmailScroll, "growx, pushx");

        sendToPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Send To"));

        return sendToPanel;
    }

    private void openFileChooser(ActionEvent event){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if(JFileChooser.APPROVE_OPTION == result){
            File file = fileChooser.getSelectedFile();
            if(ATTACHMENT_CMD.equals(event.getActionCommand())){
                attachmentModel.addElement(file.getAbsolutePath());
            }
        }
    }

    private void removeAttachment(ActionEvent event){
        //TODO remove an attachment here
        int selectedIndex = attachments.getSelectedIndex();
        attachmentModel.remove(selectedIndex);
    }

    private JTable createTable(TableModel model){
        JTable table = new JTable(model);
        table.setRowHeight(TABLE_ROW_HEIGHT);
        table.setDefaultRenderer(String.class, new CellRenderer());
        table.setPreferredScrollableViewportSize(SEND_TO_TABLE_DIMENSION);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return table;
    }

}
