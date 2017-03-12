package io.craigmiller160.email.model;

import org.apache.commons.lang3.StringUtils;

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

    public String getBody(){
        return this.body;
    }

    public void setAttachments(List<String> attachments){
        List<String> old = this.attachments;
        this.attachments = attachments;
        firePropertyChange(ATTACHMENTS_PROP, old, attachments);
    }

    public List<String> getAttachments(){
        return this.attachments;
    }

    public int getAttachmentCount(){
        return this.attachments.size();
    }

    public void addAttachment(String attachment){
        this.attachments.add(attachment);
        fireIndexedPropertyChange(ATTACHMENTS_PROP, this.attachments.size() - 1, null, attachment);
    }

    public void removeAttachment(String attachment){
        int index = this.attachments.indexOf(attachment);
        if(index >= 0){
            this.attachments.remove(attachment);
            fireIndexedPropertyChange(ATTACHMENTS_PROP, index, attachment, null);
        }
    }

    public void removeAttachment(int index){
        String attachment = this.attachments.remove(index);
        fireIndexedPropertyChange(ATTACHMENTS_PROP, index, attachment, null);
    }

    public void updateAttachment(String attachment, int index){
        this.attachments.set(index, attachment);
        fireIndexedPropertyChange(ATTACHMENTS_PROP, index, null, attachment);
    }

    @Override
    public void validate() throws Exception {
        if(StringUtils.isEmpty(subject)){
            throw new Exception("Email is missing subject line");
        }
    }

    @Override
    public void clear() {
        setSubject(null);
        setBody(null);
        setAttachments(new ArrayList<>());
    }
}
