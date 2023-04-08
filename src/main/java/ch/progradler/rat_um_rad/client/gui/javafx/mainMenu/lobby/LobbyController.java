package ch.progradler.rat_um_rad.client.gui.javafx.mainMenu.lobby;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

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
        //this.openGamesListView.getItems().addAll(this.lobbyModel.getGameNamesList());
        this.openGamesListView.setItems(this.lobbyModel.getGameNamesList());
        openGamesListView.setCellFactory(param -> new Cell());
        //each item of listView should have 2 buttons:list players and join game
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
    static class Cell extends ListCell<String> { //to be continued,nicht so sicher
        HBox hbox = new HBox();
        Label nameLabel = new Label();
        Button listPlayers = new Button("list");
        Button enterGame = new Button("join");
        public Cell() {
            super();
            hbox.getChildren().addAll(listPlayers,enterGame);
            //add button actions
        }
        @Override
        protected void updateItem(String name,boolean empty){
            super.updateItem(name,empty);
            setText(null);
            setGraphic(null);
            if(name != null && !empty){
                nameLabel.setText(name);
                setGraphic(hbox);
            }
        }
    }
    public void listPlayersInGame(){

    }
}
