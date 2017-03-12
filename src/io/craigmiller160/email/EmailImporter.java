package io.craigmiller160.email;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by craig on 3/12/17.
 */
public class EmailImporter {

    public static List<String> importEmails(File file) throws Exception{
        List<String> emails = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line = null;
            while((line = reader.readLine()) != null){
                if(!line.trim().isEmpty()){
                    emails.add(line.trim());
                }
            }
        }

        return emails;
    }

}
