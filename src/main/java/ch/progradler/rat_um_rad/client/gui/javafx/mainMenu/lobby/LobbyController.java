package ch.progradler.rat_um_rad.client.gui.javafx.mainMenu.lobby;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {
    public ListView<String> openGamesListView;
    public Button showAllGamesButton;
    public Button leaveLobbyButton;
    public Button createGameButton;
    public TextArea currentPlayersTextArea;
    public TextField gameIdTextField;
    public Button joinButton;

    private LobbyModel lobbyModel;

    //private ListView<String> listView;

    public LobbyController() {
        this.lobbyModel = new LobbyModel();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        openGamesListView.getItems().addAll(this.lobbyModel.getGameNames());
    }

    @FXML
    public void showAllGamesAction(ActionEvent event){

    }

    @FXML
    public void leaveLobbyAction(ActionEvent actionEvent) {
    }

    @FXML
    public void createGameAction(ActionEvent actionEvent) {
    }

    @FXML
    public void joinAction(ActionEvent actionEvent) {
    }
}
