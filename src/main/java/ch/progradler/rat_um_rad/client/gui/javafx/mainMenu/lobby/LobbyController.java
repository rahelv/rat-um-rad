package ch.progradler.rat_um_rad.client.gui.javafx.mainMenu.lobby;

import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {
    private IGameService gameService;
    private LobbyModel lobbyModel;
    public ListView<GameBase> openGamesListView;
    public TextArea currentPlayersTextArea;
    public TextField gameIdTextField;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.gameService = new GameService();
        this.lobbyModel = new LobbyModel();
        try {
            this.gameService.requestWaitingGames();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.openGamesListView.setItems(this.lobbyModel.getGameInfoList());
        openGamesListView.setCellFactory(param -> new Cell());
        //each item of listView should have 2 buttons:list players and join game
    }

    @FXML
    public void showAllGamesAction(ActionEvent event){

    }

    @FXML
    public void leaveLobbyAction(ActionEvent actionEvent) {
        System.out.println("leave lobby");
    }

    @FXML
    public void createGameAction(ActionEvent actionEvent) {
        //TODO: create Game dialog
        System.out.println("create game");
        try {
            this.gameService.createGame(5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void joinGameAction(ActionEvent actionEvent) {
        //TODO: get gameId (bind to textfield wo id eingegeben wurde)
        System.out.println("joined game ");
        //        //TODO: Anfrage an Server um Game zu jonen
    }

    static class Cell extends ListCell<GameBase> {
        HBox hbox = new HBox();
        Label nameLabel = new Label();
        Button listPlayersButton = new Button("players");
        Button enterGameButton = new Button("join");
        public Cell() {
            super();
            hbox.getChildren().addAll(nameLabel, listPlayersButton, enterGameButton);
            enterGameButton.setOnAction(event -> {
                System.out.println("wanting to join game " + getItem().getId());
                //TODO: send anfrage to server: OutputPacketGatewaySingleton.;
            });
            listPlayersButton.setOnAction(event -> {
                System.out.println("listing all players in this game");
            });
        }
        protected void updateItem(GameBase item, boolean empty){
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            if(item != null && !empty){
                nameLabel.setText(item.getId());
                setGraphic(hbox);
            }
        }

    }
}
