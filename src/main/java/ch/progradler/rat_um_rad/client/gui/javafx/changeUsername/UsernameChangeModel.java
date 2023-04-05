package ch.progradler.rat_um_rad.client.gui.javafx.changeUsername;

import ch.progradler.rat_um_rad.client.models.User;
import ch.progradler.rat_um_rad.client.utils.ComputerInfo;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UsernameChangeModel {
    ComputerInfo computerInfo;
    private String systemUsername;
    private String currentUsername; 
    private StringProperty chosenUsername; //TODO: bind to textfield
    private String usernameRules = "Username Rules: 5-30 characters. only letters, digits and underscores allowed. first char must be a letter!\n";

    UsernameChangeModel() {
        computerInfo = new ComputerInfo();
        this.systemUsername = computerInfo.getSystemUsername();
        this.currentUsername = User.getInstance().getUsername();
        this.chosenUsername = new SimpleStringProperty(this.systemUsername);
    }
    public String getChosenUsername() {
         return chosenUsername.get();
    }

    public String getCurrentUsername() {
        return currentUsername;
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
}
