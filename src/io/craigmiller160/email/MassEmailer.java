package io.craigmiller160.email;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by craig on 9/18/16.
 */
public class MassEmailer {

    private static final Scanner scanner = new Scanner(System.in);
    private static File emailListFile;
    private static File emailMessageFile;
    private static List<File> attachmentFiles;
    private static String sendTo;
    private static String message;
    private static String subject;

    private static Properties applicationProps;

    public static void main(String[] args) throws Exception{
        applicationProps = new Properties();
        applicationProps.load(MassEmailer.class.getClassLoader().getResourceAsStream("email.properties"));
        introPrompts();
        summarizeEmail();

        while(true){
            System.out.print("\nDo you want to send? (y/n): ");
            String response = scanner.nextLine();
            if("y".equalsIgnoreCase(response)){
                sendEmail();
                break;
            }
            else if("n".equalsIgnoreCase(response)){
                System.out.println("Email cancelled, please try again.");
                break;
            }
            else{
                System.out.println("Invalid input!");
            }
        }
    }

    private static void sendEmail() throws Exception{
        System.out.println("Sending email, please wait...");
        Properties props = System.getProperties();
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage mimeMessage = new MimeMessage(session);
        String[] sendToParts = sendTo.split(",");
        for(String s : sendToParts){
            if(!s.trim().isEmpty()){
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(s));
            }
        }

        mimeMessage.setSubject(subject);
        MimeMultipart multipart = new MimeMultipart();

        MimeBodyPart messagePart = new MimeBodyPart();
        messagePart.setText(message);
        multipart.addBodyPart(messagePart);

        mimeMessage.setContent(message, "text/plain");
        for(File f : attachmentFiles){
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setDataHandler(new DataHandler(new FileDataSource(f.getAbsolutePath())));
            bodyPart.setFileName(f.getName());
            multipart.addBodyPart(bodyPart);
        }

        mimeMessage.setContent(multipart);

        Transport transport = null;
        try{
            transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", applicationProps.getProperty("username"), applicationProps.getProperty("password"));
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            System.out.println("Email sent successfully");
        }
        finally{
            if(transport != null){
                transport.close();
            }
        }
    }

    private static void summarizeEmail() throws Exception{
        StringBuilder emailListBuilder = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader(emailListFile))){
            String line = null;
            while((line = reader.readLine()) != null){
                if(!line.trim().isEmpty()){
                    emailListBuilder.append(line.trim()).append(",");
                }
            }
        }
        sendTo = emailListBuilder.toString();

        StringBuilder emailBodyBuilder = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader(emailMessageFile))){
            String line = null;
            while((line = reader.readLine()) != null){
                emailBodyBuilder.append(line).append("\n");
            }
        }
        message = emailBodyBuilder.toString();

        System.out.println("\nYour Email:");
        System.out.println("  Send To: ");
        System.out.println("  " + emailListBuilder.toString());
        System.out.println("  Subject:");
        System.out.println("  " + subject);
        System.out.println("  Message:\n");
        System.out.println(emailBodyBuilder.toString());
        System.out.println("  Attachments:");
        if(attachmentFiles.size() > 0){
            attachmentFiles.forEach((f) -> System.out.println("  " + f.getAbsolutePath()));
        }
        else{
            System.out.println("  None");
        }
    }

    private static void introPrompts() throws Exception{
        System.out.println("Mass Email Tool\n\n");
        System.out.print("Email List File: ");
        String emailListFilePath = scanner.nextLine();
        emailListFile = new File(emailListFilePath);
        if(!emailListFile.exists() || !emailListFile.isFile()){
            throw new IOException("Email List File is invalid: " + emailListFilePath);
        }
        System.out.println("Emails File: " + emailListFile.getAbsolutePath() + "\n");

        System.out.print("Email Subject: ");
        subject = scanner.nextLine();
        System.out.println("Subject: " + subject + "\n");

        System.out.print("Email Message File: ");
        String emailMessageFilePath = scanner.nextLine();
        emailMessageFile = new File(emailMessageFilePath);
        if(!emailMessageFile.exists() || !emailMessageFile.isFile()){
            throw new IOException("Email List File is invalid: " + emailMessageFilePath);
        }
        System.out.println("Message File: " + emailMessageFile.getAbsolutePath() + "\n");

        attachmentFiles = new ArrayList<>();
        while(true){
            System.out.print("Do you want to add an attachment? (y/n): ");
            String addAttachment = scanner.nextLine();
            if("y".equalsIgnoreCase(addAttachment)){
                System.out.print("Attachment File: ");
                String attachmentFilePath = scanner.nextLine();
                File attachmentFile = new File(attachmentFilePath);
                if(!attachmentFile.exists() || !attachmentFile.isFile()){
                    throw new IOException("Attachment File is invalid: " + attachmentFilePath);
                }
                attachmentFiles.add(attachmentFile);
                System.out.println("Attachment File: " + attachmentFile.getAbsolutePath());
            }
            else if("n".equalsIgnoreCase(addAttachment)){
                break;
            }
            else{
                System.out.println("Invalid Input!");
            }
        }
    }

}
