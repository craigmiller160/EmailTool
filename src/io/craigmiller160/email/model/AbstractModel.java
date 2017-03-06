package io.craigmiller160.email.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by craig on 3/6/17.
 */
public abstract class AbstractModel{

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener){
        this.support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener){
        this.support.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldVal, Object newVal){
        this.support.firePropertyChange(propertyName, oldVal, newVal);
    }

    protected void fireIndexedPropertyChange(String propertyName, int index, Object oldVal, Object newVal){
        this.support.fireIndexedPropertyChange(propertyName, index, oldVal, newVal);
    }

}
