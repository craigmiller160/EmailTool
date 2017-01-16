package io.craigmiller160.email.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * Created by craigmiller on 1/16/17.
 */
public class EmailWindow extends JFrame {

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

    private ContactModel contactTableModel;
    private JTable contactTable;
    private JList attachmentList;
    private JButton addContactBtn;
    private JButton removeContactBtn;
    private JButton addAttachmentBtn;
    private JButton removeAttachmentBtn;
    private JScrollPane contactsScrollPane;

    public EmailWindow(){
        init();
    }

    private void init(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new JPanel(new MigLayout()));

        JPanel contactsPanel = buildContactsPanel();

        getContentPane().add(contactsPanel, "dock north");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildContactsPanel(){
        JPanel contactsPanel = new JPanel(new MigLayout());

        contactTableModel = new ContactModel();
        contactTable = new JTable(contactTableModel);
        contactTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        contactsScrollPane = new JScrollPane(contactTable);

        addContactBtn = new JButton("+");
        removeContactBtn = new JButton("-");

        JPanel buttonPanel = new JPanel(new MigLayout());
        buttonPanel.add(addContactBtn, "dock west");
        buttonPanel.add(removeContactBtn, "dock west");

        contactsPanel.add(contactsScrollPane, "dock center");
        contactsPanel.add(buttonPanel, "dock south");

        return contactsPanel;
    }

}
