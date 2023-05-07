package ch.progradler.rat_um_rad.client.gui.javafx.mainMenu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void openHandbook(ActionEvent event) {
        HandbookPopup handbookPopup = new HandbookPopup(); //TODO: only open once
    }
}
