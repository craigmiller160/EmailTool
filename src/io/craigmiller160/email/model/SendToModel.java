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
    }

    public void addCCEmail(String email){
        this.ccEmails.add(email);
    }

    public void addBCCEmail(String email){
        this.bccEmails.add(email);
    }

    public void removeToEmail(String email){
        this.toEmails.remove(email);
    }

    public void removeCCEmail(String email){
        this.ccEmails.remove(email);
    }

    public void removeBCCEmail(String email){
        this.bccEmails.remove(email);
    }

    public void removeToEmail(int index){
        this.toEmails.remove(index);
    }

    public void removeCCEmail(int index){
        this.ccEmails.remove(index);
    }

    public void removeBCCEmail(int index){
        this.bccEmails.remove(index);
    }

    public void updateToEmail(String email, int index){
        this.toEmails.set(index, email);
    }

    public void updateCCEmail(String email, int index){
        this.ccEmails.set(index, email);
    }

    public void updateBCCEmail(String email, int index){
        this.bccEmails.set(index, email);
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
        List<String> old = this.toEmails;
        this.toEmails = toEmails;
        firePropertyChange(TO_EMAIL_PROP, old, toEmails);
    }

    public void setCCEmails(List<String> ccEmails){
        List<String> old = this.ccEmails;
        this.ccEmails = ccEmails;
        firePropertyChange(CC_EMAIL_PROP, old, ccEmails);
    }

    public void setBCCEmails(List<String> bccEmails){
        List<String> old = this.bccEmails;
        this.bccEmails = bccEmails;
        firePropertyChange(BCC_EMAIL_PROP, old, bccEmails);
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

}
