package io.craigmiller160.email.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by craigmiller on 1/16/17.
 */
public class EmailWindow extends JFrame {

    //TODO need cell renderer with tooltips
    //TODO add button to load emails from file
    //TODO need save/load for all configurations feature

    //TODO delete this
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
        new EmailWindow();
    }

    private static final int TABLE_ROW_HEIGHT = 20;

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

    private JTextField subject;
    private JTextArea body;
    private JList<String> attachments;
    private DefaultListModel<String> attachmentModel;

    private JButton sendEmail;

    public EmailWindow(){
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
        JPanel secondRowPanel = buildSecondRowPanel();
        JPanel bottomPanel = bottomPanel();

        getContentPane().add(titleLabel, "dock north");
        getContentPane().add(sendToPanel, "dock north");
        getContentPane().add(secondRowPanel, "dock center");
        getContentPane().add(bottomPanel, "dock south");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildSecondRowPanel(){
        JPanel secondRowPanel = new JPanel(new MigLayout());

        secondRowPanel.add(buildFromPanel(), "grow, pushx");
        secondRowPanel.add(buildMessagePanel(), "grow, pushx");

        return secondRowPanel;
    }

    private JPanel buildFromPanel(){
        JPanel fromPanel = new JPanel(new MigLayout());

        JLabel usernameLabel = new JLabel("Username: ");
        JLabel passwordLabel = new JLabel("Password: ");
        JLabel portLabel = new JLabel("Port: ");

        username = new JTextField();
        password = new JPasswordField();
        port = new JSpinner(new SpinnerNumberModel(0,0,Integer.MAX_VALUE,1));
        auth = new JCheckBox("Use Auth");
        startTLS = new JCheckBox("Start TLS");

        fromPanel.add(usernameLabel, "");
        fromPanel.add(username, "growx, pushx, wrap");
        fromPanel.add(passwordLabel, "");
        fromPanel.add(password, "growx, pushx, wrap");
        fromPanel.add(portLabel, "");
        fromPanel.add(port, "growx, pushx, wrap");
        fromPanel.add(auth, "skip 1, growx, pushx, wrap");
        fromPanel.add(startTLS, "skip 1, growx, pushx, wrap");

        fromPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Send From"));

        return fromPanel;
    }

    private JPanel bottomPanel(){
        JPanel sendPanel = new JPanel(new MigLayout());

        sendEmail = new JButton("Send Email");

        sendPanel.add(sendEmail, "growx, pushx");

        return sendPanel;
    }

    private JPanel buildMessagePanel(){
        JPanel messagePanel = new JPanel(new MigLayout());

        JLabel subjectLabel = new JLabel("Subject: ");
        JLabel bodyLabel = new JLabel("Body: ");
        JLabel attachmentsLabel = new JLabel("Attachments: ");

        subject = new JTextField();
        body = new JTextArea();
        attachmentModel = new DefaultListModel<>();
        attachments = new JList<>(attachmentModel);

        JScrollPane bodyScroll = new JScrollPane(body);
        JScrollPane attachmentScroll = new JScrollPane(attachments);

        messagePanel.add(subjectLabel, "");
        messagePanel.add(subject, "growx, pushx, wrap");
        messagePanel.add(bodyLabel, "");
        messagePanel.add(bodyScroll, "growx, pushx, wrap");
        messagePanel.add(attachmentsLabel, "");
        messagePanel.add(attachmentScroll, "growx, pushx");

        messagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Message"));

        return messagePanel;
    }

    private JPanel buildSendToPanel(){
        JPanel sendToPanel = new JPanel(new MigLayout());

        toEmailModel = new SendToListModel("To");
        ccEmailModel = new SendToListModel("CC");
        bccEmailModel = new SendToListModel("BCC");

        toEmailList = new JTable(toEmailModel);
        toEmailList.setRowHeight(TABLE_ROW_HEIGHT);
        toEmailList.setDefaultRenderer(String.class, new CellRenderer());

        ccEmailList = new JTable(ccEmailModel);
        ccEmailList.setRowHeight(TABLE_ROW_HEIGHT);
        ccEmailList.setDefaultRenderer(String.class, new CellRenderer());

        bccEmailList = new JTable(bccEmailModel);
        bccEmailList.setRowHeight(TABLE_ROW_HEIGHT);
        bccEmailList.setDefaultRenderer(String.class, new CellRenderer());

        JScrollPane toEmailScroll = new JScrollPane(toEmailList);
        JScrollPane ccEmailScroll = new JScrollPane(ccEmailList);
        JScrollPane bccEmailScroll = new JScrollPane(bccEmailList);

        sendToPanel.add(toEmailScroll, "growx, pushx");
        sendToPanel.add(ccEmailScroll, "growx, pushx");
        sendToPanel.add(bccEmailScroll, "growx, pushx");

        sendToPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Send To"));

        return sendToPanel;
    }

}
