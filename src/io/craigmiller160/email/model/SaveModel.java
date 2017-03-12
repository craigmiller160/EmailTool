package io.craigmiller160.email.model;

import java.io.File;

/**
 * Created by craig on 3/12/17.
 */
public class SaveModel extends AbstractModel {

    public static final String SAVE_NAME_PROP = "SaveName";
    public static final String SAVE_FILE_PROP = "SaveFile";
    public static final String LAST_LOCATION_PROP = "LastLocation";

    public static final String EXTENSION = "emailconfig";

    private String saveName;
    private File saveFile;
    private File lastLocation;

    public String getSaveName() {
        return saveName;
    }

    public void setSaveName(String saveName) {
        String old = this.saveName;
        this.saveName = saveName;
        firePropertyChange(SAVE_NAME_PROP, old, saveName);
    }

    public File getSaveFile() {
        return saveFile;
    }

    public void setSaveFile(File saveFile) {
        File old = this.saveFile;
        this.saveFile = saveFile;
        firePropertyChange(SAVE_FILE_PROP, old, saveFile);
    }

    public File getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(File lastLocation) {
        File old = this.lastLocation;
        this.lastLocation = lastLocation;
        firePropertyChange(LAST_LOCATION_PROP, old, lastLocation);
    }

    @Override
    public void validate() throws Exception {
        //Do nothing
    }

    @Override
    public void clear() {
        setSaveName(null);
        setSaveFile(null);
        setLastLocation(null);
    }
}
