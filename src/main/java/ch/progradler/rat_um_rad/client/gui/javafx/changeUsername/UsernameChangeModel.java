package ch.progradler.rat_um_rad.client.gui.javafx.changeUsername;

import ch.progradler.rat_um_rad.client.models.User;
import ch.progradler.rat_um_rad.client.utils.ComputerInfo;
import ch.progradler.rat_um_rad.client.utils.listeners.ControllerChangeListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for UsernameChange (stores the User)
 */
public class UsernameChangeModel {
    private String chosenUsernameCommandLine = null;
    private final ControllerChangeListener<?> listener;
    ComputerInfo computerInfo;
    private String systemUsername;
    private final User user;
    private StringProperty chosenUsername;
    private static final String usernameRules = "Username Rules: 5-30 characters. only letters, digits and underscores allowed. first char must be a letter!\n";

    public UsernameChangeModel(User user, ControllerChangeListener<?> listener) {
        this.listener = listener;
        computerInfo = new ComputerInfo();
        this.systemUsername = computerInfo.getSystemUsername();
        this.user = user;
        this.chosenUsername = new SimpleStringProperty(this.systemUsername);
    }

    public UsernameChangeModel(User user, ControllerChangeListener<?> listener, String chosenUsernameCommandLine) {
        this.listener = listener;
        computerInfo = new ComputerInfo();
        this.systemUsername = computerInfo.getSystemUsername();
        this.user = user;
        this.chosenUsername = new SimpleStringProperty(this.systemUsername);
        this.chosenUsernameCommandLine = chosenUsernameCommandLine;
    }

    public String getChosenUsername() {
         return chosenUsername.get();
    }
    public void setChosenUsername(String chosenUsername) {
        this.chosenUsername.setValue(chosenUsername);
    }

    public String getCurrentUsername() {
        return user.getUsername();
    }

    public String getUsernameRules() {
        return usernameRules;
    }

    public String getSystemUsername() {
        return systemUsername;
    }

    public StringProperty chosenUsernameProperty() {
        return chosenUsername;
    }

    public void setConfirmedUsername(String username) {
        this.user.setUsername(username);
    }

    public ControllerChangeListener<?> getListener() {
        return listener;
    }

    public String getChosenUsernameCommandLine() {
        return chosenUsernameCommandLine;
    }
}
