package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.gameOverview;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby.LobbyController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby.LobbyModel;
import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GameOverviewController implements Initializable, ServerResponseListener<List<GameBase>> {
    @FXML
    private LobbyController lobbyController;
    @FXML
    private Button backToLobbyButton;
    @FXML
    private ListView onGoingListView;
    @FXML
    private ListView finishedGamesListView;

    private IGameService gameService;
    private GameOverviewModel gameOverviewModel;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(this);

        this.gameService = new GameService();
    }

    public void initData(GameOverviewModel model, LobbyModel lobbyModel) {
        this.lobbyController.initData(lobbyModel);
        this.gameOverviewModel = model;

        try {
            requestListsFromServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void requestListsFromServer() throws IOException {
        this.gameService.requestWaitingGames();
        this.gameService.requestStartedGames();
        this.gameService.requestFinishedGames();
    }

    @FXML
    private void returnButtonAction(ActionEvent event) {
        Platform.runLater(() -> {
            gameOverviewModel.getListener().controllerChanged("showStartupPage"); //returns to main Page...
        });
    }

    @Override
    public void serverResponseReceived(List<GameBase> content, ContentType contentType) {
        switch(contentType) {
            case GAME_INFO_LIST_STARTED -> {
                this.gameOverviewModel.setOngoingGameList(content);
                this.onGoingListView.setItems(this.gameOverviewModel.getOngoingGameList());
                onGoingListView.setCellFactory(param -> new GameOverviewController.Cell());
            }
            case GAME_INFO_LIST_FINISHED -> {
                this.gameOverviewModel.setFinishedGameList(content);
                this.finishedGamesListView.setItems(this.gameOverviewModel.getFinishedGameList());
                finishedGamesListView.setCellFactory(param -> new GameOverviewController.Cell());
            }
        }
    }

    static class OpenGameCell extends ListCell<GameBase> {
        Pane pane = new Pane();
        HBox hbox = new HBox();
        Label nameLabel = new Label();
        Button listPlayersButton = new Button("players");
        Button enterGameButton = new Button("join");
        public OpenGameCell() {
            super();
            hbox.getChildren().addAll(nameLabel,pane,listPlayersButton, enterGameButton);
            hbox.setHgrow(pane, Priority.ALWAYS);
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
    static class Cell extends ListCell<GameBase>{
        Pane pane = new Pane();
        HBox hbox = new HBox();
        Label nameLabel = new Label();
        Button listPlayersButton = new Button("players");
        public Cell() {
            super();
            hbox.getChildren().addAll(nameLabel,pane,listPlayersButton);
            hbox.setHgrow(pane, Priority.ALWAYS);
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
