package io.craigmiller160.email.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * Created by craigmiller on 1/16/17.
 */
public class EmailWindow extends JFrame {

    //TODO need cell renderer with tooltips

    //TODO delete this
    public static void main(String[] args){
        new EmailWindow();
    }

    /*
     * Options:
     *
     * 1) List of contacts
     * 2) BCC?
     * 3) Subject
     * 4) Body
     * 5) List of Attachments
     */

    private JList<String> toEmailList;
    private JList<String> ccEmailList;
    private JList<String> bccEmailList;
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

        JPanel contactsPanel = buildSendToPanel();

        getContentPane().add(titleLabel, "dock north");
        getContentPane().add(contactsPanel, "dock center");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildFromPanel(){
        JPanel fromPanel = new JPanel(new MigLayout());

        username = new JTextField();
        password = new JPasswordField();
        port = new JSpinner(new SpinnerNumberModel(0,0,Integer.MAX_VALUE,1));
        auth = new JCheckBox("Use Auth");
        startTLS = new JCheckBox("Start TLS");

        //TODO add items to panel

        return fromPanel;
    }

    private JPanel buildMessagePanel(){
        JPanel messagePanel = new JPanel(new MigLayout());

        subject = new JTextField();
        body = new JTextArea();
        attachmentModel = new DefaultListModel<>();
        attachments = new JList<>(attachmentModel);

        //TODO add items to panel

        return messagePanel;
    }

    private JPanel buildSendToPanel(){
        JPanel sendToPanel = new JPanel(new MigLayout());

        toEmailModel = new SendToListModel();
        ccEmailModel = new SendToListModel();
        bccEmailModel = new SendToListModel();

        toEmailList = new JList<>(toEmailModel);
        ccEmailList = new JList<>(ccEmailModel);
        bccEmailList = new JList<>(bccEmailModel);

        JScrollPane toEmailScroll = new JScrollPane(toEmailList);
        JScrollPane ccEmailScroll = new JScrollPane(ccEmailList);
        JScrollPane bccEmailScroll = new JScrollPane(bccEmailList);

        toEmailScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "To"));
        ccEmailScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "CC"));
        bccEmailScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "BCC"));

        sendToPanel.add(toEmailScroll, "growx, pushx");
        sendToPanel.add(ccEmailScroll, "growx, pushx");
        sendToPanel.add(bccEmailScroll, "growx, pushx");

        return sendToPanel;
    }

}
