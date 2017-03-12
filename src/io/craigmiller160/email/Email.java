package io.craigmiller160.email;

import io.craigmiller160.email.model.MessageModel;
import io.craigmiller160.email.model.SendFromModel;
import io.craigmiller160.email.model.SendToModel;

import java.util.Properties;

/**
 * Created by craig on 3/12/17.
 */
public class Email {

    private static final String MAIL_SMTP_PORT = "mail.smtp.port";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_STARTTLS_ENABLE = "mail.smtp.starttls.enable";

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

    }

    private Properties getMailSessionProps(){
        Properties props = System.getProperties();
        return props;
    }

}
