package ch.progradler.rat_um_rad.client.gui.javafx.mainMenu.createGame;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CreateGameModel {
    private StringProperty groupNameInput;

    public CreateGameModel() {
        groupNameInput = new SimpleStringProperty("");
    }
    public StringProperty getGroupNameInputProperty() {
        return groupNameInput;
    }

    public String getGroupNameInput() {
        return groupNameInput.get();
    }
}
