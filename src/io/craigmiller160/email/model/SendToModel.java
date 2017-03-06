package io.craigmiller160.email.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by craig on 3/6/17.
 */
public class SendToModel {

    private final List<String> toEmails;
    private final List<String> ccEmails;
    private final List<String> bccEmails;

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

    public int toEmailCount(){
        return toEmails.size();
    }

    public int ccEmailCount(){
        return ccEmails.size();
    }

    public int bccEmailCount(){
        return bccEmails.size();
    }

}
