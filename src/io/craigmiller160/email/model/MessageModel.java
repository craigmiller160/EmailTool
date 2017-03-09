package io.craigmiller160.email.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by craig on 3/7/17.
 */
public class MessageModel extends AbstractModel {

    public static final String SUBJECT_PROP = "Subject";
    public static final String BODY_PROP = "Body";
    public static final String ATTACHMENTS_PROP = "Attachments";

    private String subject;
    private String body;
    private List<String> attachments;

    public MessageModel(){
        this.attachments = new ArrayList<>();
    }

    public void setSubject(String subject){
        String old = this.subject;
        this.subject = subject;
        firePropertyChange(SUBJECT_PROP, old, subject);
    }

    public String getSubject(){
        return this.subject;
    }

    public void setBody(String body){
        String old = this.body;
        this.body = body;
        firePropertyChange(BODY_PROP, old, body);
    }

    public void setAttachments(List<String> attachments){
        List<String> old = this.attachments;
        this.attachments = attachments;
        firePropertyChange(ATTACHMENTS_PROP, old, attachments);
    }

    public List<String> getAttachments(){
        return this.attachments;
    }

    public void addAttachment(String attachment){
        this.attachments.add(attachment);
    }

    public void removeAttachment(int index){
        this.attachments.remove(index);
    }

    public void updateAttachment(String attachment, int index){
        this.attachments.set(index, attachment);
    }

}
