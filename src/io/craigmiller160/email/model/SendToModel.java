package io.craigmiller160.email.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by craig on 3/6/17.
 */
public class SendToModel extends AbstractModel{

    public static final String TO_EMAIL_PROP = "ToEmail";
    public static final String CC_EMAIL_PROP = "CCEmail";
    public static final String BCC_EMAIL_PROP = "BCCEmail";

    private List<String> toEmails;
    private List<String> ccEmails;
    private List<String> bccEmails;

    public SendToModel(){
        this.toEmails = new ArrayList<>();
        this.ccEmails = new ArrayList<>();
        this.bccEmails = new ArrayList<>();
    }

    public void addToEmail(String email){
        this.toEmails.add(email);
        fireIndexedPropertyChange(TO_EMAIL_PROP, this.toEmails.size() - 1, null, email);
    }

    public void addCCEmail(String email){
        this.ccEmails.add(email);
        fireIndexedPropertyChange(CC_EMAIL_PROP, this.ccEmails.size() - 1, null, email);
    }

    public void addBCCEmail(String email){
        this.bccEmails.add(email);
        fireIndexedPropertyChange(BCC_EMAIL_PROP, this.bccEmails.size() - 1, null, email);
    }

    public void removeToEmail(String email){
        int index = toEmails.indexOf(email);
        if(index >= 0){
            this.toEmails.remove(email);
            fireIndexedPropertyChange(TO_EMAIL_PROP, index, email, null);
        }
    }

    public void removeCCEmail(String email){
        int index = ccEmails.indexOf(email);
        if(index >= 0){
            this.ccEmails.remove(email);
            fireIndexedPropertyChange(CC_EMAIL_PROP, index, email, null);
        }

    }

    public void removeBCCEmail(String email){
        int index = bccEmails.indexOf(email);
        if(index >= 0){
            this.bccEmails.remove(email);
            fireIndexedPropertyChange(BCC_EMAIL_PROP, index, email, null);
        }
    }

    public void removeToEmail(int index){
        String email = this.toEmails.remove(index);
        fireIndexedPropertyChange(TO_EMAIL_PROP, index, email, null);
    }

    public void removeCCEmail(int index){
        String email = this.ccEmails.remove(index);
        fireIndexedPropertyChange(CC_EMAIL_PROP, index, email, null);
    }

    public void removeBCCEmail(int index){
        String email = this.bccEmails.remove(index);
        fireIndexedPropertyChange(BCC_EMAIL_PROP, index, email, null);
    }

    public void updateToEmail(String email, int index){
        this.toEmails.set(index, email);
        fireIndexedPropertyChange(TO_EMAIL_PROP, index, null, email);
    }

    public void updateCCEmail(String email, int index){
        this.ccEmails.set(index, email);
        fireIndexedPropertyChange(CC_EMAIL_PROP, index, null, email);
    }

    public void updateBCCEmail(String email, int index){
        this.bccEmails.set(index, email);
        fireIndexedPropertyChange(BCC_EMAIL_PROP, index, null, email);
    }

    public int toEmailCount(){
        return toEmails.size();
    }

    public int ccEmailCount(){
        return ccEmails.size();
    }

    public int bccEmailCount(){
        return bccEmails.size();
    }

    public void setToEmails(List<String> toEmails){
        this.toEmails = toEmails;
        firePropertyChange(TO_EMAIL_PROP, null, toEmails);
    }

    public void setCCEmails(List<String> ccEmails){
        this.ccEmails = ccEmails;
        firePropertyChange(CC_EMAIL_PROP, null, ccEmails);
    }

    public void setBCCEmails(List<String> bccEmails){
        this.bccEmails = bccEmails;
        firePropertyChange(BCC_EMAIL_PROP, null, bccEmails);
    }

    public List<String> getToEmails(){
        return this.toEmails;
    }

    public List<String> getCCEmails(){
        return this.ccEmails;
    }

    public List<String> getBCCEmails(){
        return this.bccEmails;
    }

    @Override
    public void validate() throws Exception {
        if(toEmails.isEmpty() && ccEmails.isEmpty() && bccEmails.isEmpty()){
            throw new Exception("Email has no recipients sent");
        }
    }

    @Override
    public void clear() {
        setToEmails(new ArrayList<>());
        setCCEmails(new ArrayList<>());
        setBCCEmails(new ArrayList<>());
    }
}
