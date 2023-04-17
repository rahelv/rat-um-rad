package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.createGame;

import ch.progradler.rat_um_rad.client.utils.listeners.ControllerChangeListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class CreateGameModel {
    private final ControllerChangeListener<?> listener;

    private StringProperty groupNameInput;

    public CreateGameModel(ControllerChangeListener<?> listener) {
        this.listener = listener;
        groupNameInput = new SimpleStringProperty("");
    }
    public StringProperty getGroupNameInputProperty() {
        return groupNameInput;
    }

    public String getGroupNameInput() {
        return groupNameInput.get();
    }

    public ControllerChangeListener<?> getListener() {
        return listener;
    }
}
