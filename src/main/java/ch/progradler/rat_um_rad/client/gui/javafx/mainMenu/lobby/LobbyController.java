package ch.progradler.rat_um_rad.client.gui.javafx.mainMenu.lobby;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {
    private LobbyModel lobbyModel;
    @FXML
    private ListView<String> listView;
    public LobbyController() {
        this.lobbyModel = new LobbyModel();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listView.getItems().addAll(this.lobbyModel.getGameNames());
    }
}
