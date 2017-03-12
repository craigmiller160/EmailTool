package io.craigmiller160.email;

import io.craigmiller160.email.model.MessageModel;
import io.craigmiller160.email.model.SendFromModel;
import io.craigmiller160.email.model.SendToModel;
import org.apache.commons.lang3.StringUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

/**
 * Created by craig on 3/12/17.
 */
public class Email {

    private static final String MAIL_SMTP_PORT = "mail.smtp.port";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private static final String SMTP_PROTOCOL = "smtp";

    private final SendFromModel sendFromModel;
    private final SendToModel sendToModel;
    private final MessageModel messageModel;

    private Email(SendFromModel sendFromModel, SendToModel sendToModel, MessageModel messageModel){
        this.sendFromModel = sendFromModel;
        this.sendToModel = sendToModel;
        this.messageModel = messageModel;
    }

    public static Email newEmail(SendFromModel sendFromModel, SendToModel sendToModel, MessageModel messageModel) throws Exception{
        Email email = new Email(sendFromModel, sendToModel, messageModel);
        email.validate();
        return email;
    }

    public void validate() throws Exception{
        sendFromModel.validate();
        sendToModel.validate();
        messageModel.validate();
    }

    public void send() throws Exception{
        System.out.println("Sending email, please wait...");

        Properties props = getMailSessionProps();
        Session session = Session.getDefaultInstance(props);

        MimeMessage mimeMessage = new MimeMessage(session);
        addRecipients(mimeMessage);
        addContent(mimeMessage);

        Transport transport = session.getTransport(SMTP_PROTOCOL);
        if(sendFromModel.isAuth()){
            transport.connect(sendFromModel.getHost(), sendFromModel.getUsername(), sendFromModel.getPassword());
        }
        else{
            transport.connect(sendFromModel.getHost(), null, null);
        }

        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

        System.out.println("Email sent successfully");
    }

    private void addContent(MimeMessage mimeMessage) throws Exception{
        mimeMessage.setSubject(messageModel.getSubject());
        MimeMultipart multipart = new MimeMultipart();
        mimeMessage.setContent(multipart);

        if(!StringUtils.isEmpty(messageModel.getBody())){
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(messageModel.getBody());
            multipart.addBodyPart(bodyPart);
        }

        for(String attachment : messageModel.getAttachments()){
            File f = new File(attachment);
            if(!f.exists()){
                throw new Exception("Attachment file doesn't exist: " + f.getAbsolutePath());
            }

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.setDataHandler(new DataHandler(new FileDataSource(f.getAbsolutePath())));
            attachmentPart.setFileName(f.getName());
            multipart.addBodyPart(attachmentPart);
        }
    }

    private void addRecipients(MimeMessage mimeMessage) throws Exception{
        for(String email : sendToModel.getToEmails()){
            if(!email.trim().isEmpty()){
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            }
        }

        for(String email : sendToModel.getCCEmails()){
            if(!email.trim().isEmpty()){
                mimeMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(email));
            }
        }

        for(String email : sendToModel.getBCCEmails()){
            if(!email.trim().isEmpty()){
                mimeMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(email));
            }
        }
    }

    private Properties getMailSessionProps(){
        Properties props = System.getProperties();
        props.put(MAIL_SMTP_PORT, sendFromModel.getPort());
        props.put(MAIL_SMTP_AUTH, sendFromModel.isAuth());
        props.put(MAIL_STARTTLS_ENABLE, sendFromModel.isStartTLS());
        return props;
    }

}
