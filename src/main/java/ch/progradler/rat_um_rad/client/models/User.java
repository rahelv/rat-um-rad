package ch.progradler.rat_um_rad.client.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class User {
    public static final String PROPERTY_NAME_USERNAME = "username";
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private String username;

    /**
     * Adds PropertyChangeListener for the username property
     * @param listener
     */
    public void addUsernameObserver(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(PROPERTY_NAME_USERNAME, listener);
    }

    public String getUsername() {
        return username;
    }

    /**
     * Sets the username which is received from server. Triggers PropertyChange so Observers (CommandLineHandler) are notified of username change.
     * @param username
     */
    public void setConfirmedUsername(String username) {
        String oldUsername = this.username;
        this.username = username;
        propertyChangeSupport.firePropertyChange(PROPERTY_NAME_USERNAME, oldUsername, username);
    }
}
