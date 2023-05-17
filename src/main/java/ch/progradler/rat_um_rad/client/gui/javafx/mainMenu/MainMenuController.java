package ch.progradler.rat_um_rad.client.gui.javafx.mainMenu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void openHandbook(ActionEvent event) {
        try {
            URL handbookUrl = MainMenuController.class.getClassLoader().getResource("Spielkonzept.pdf");
            if (handbookUrl == null) return;
            File handbook = new File(handbookUrl.toURI());
            Desktop.getDesktop().open(handbook);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void openIconLinks(ActionEvent event){
        IconLinksPopup iconLinksPopup = new IconLinksPopup();
    }
}
