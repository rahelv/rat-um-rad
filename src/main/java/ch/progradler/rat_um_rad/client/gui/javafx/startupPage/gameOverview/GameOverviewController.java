package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.gameOverview;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby.LobbyController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby.LobbyModel;
import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.io.IOException;
import java.util.List;

public class GameOverviewController {
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

    public GameOverviewController() {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<List<GameBase>>() {
            @Override
            public void serverResponseReceived(List<GameBase> content) {
                startedGameListReceived(content);
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.SEND_STARTED_GAMES;
            }
        });

        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<List<GameBase>>() {
            @Override
            public void serverResponseReceived(List<GameBase> content) {
                finishedGameListReceived(content);
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.SEND_FINISHED_GAMES;
            }
        });

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

    public void startedGameListReceived(List<GameBase> content) {
        this.gameOverviewModel.setOngoingGameList(content);
        this.onGoingListView.setItems(this.gameOverviewModel.getOngoingGameList());
        onGoingListView.setCellFactory(param -> new GameOverviewController.Cell());
    }

    public void finishedGameListReceived(List<GameBase> content) {
        this.gameOverviewModel.setFinishedGameList(content);
        this.finishedGamesListView.setItems(this.gameOverviewModel.getFinishedGameList());
        finishedGamesListView.setCellFactory(param -> new GameOverviewController.Cell());
    }

    static class Cell extends ListCell<GameBase> {
        Pane pane = new Pane();
        HBox hbox = new HBox();
        Label nameLabel = new Label();
        Button listPlayersButton = new Button("players");

        public Cell() {
            super();
            hbox.getChildren().addAll(nameLabel, pane, listPlayersButton);
            hbox.setHgrow(pane, Priority.ALWAYS);
            listPlayersButton.setOnAction(event -> {
                //TODO: listing all players in this game
            });
        }

        protected void updateItem(GameBase item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            if (item != null && !empty) {
                nameLabel.setText(item.getId());
                setGraphic(hbox);
            }
        }
    }
}
