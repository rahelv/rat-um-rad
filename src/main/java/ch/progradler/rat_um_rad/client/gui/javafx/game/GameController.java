package ch.progradler.rat_um_rad.client.gui.javafx.game;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.gui.javafx.game.activity.ActivityController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.activity.ActivityModel;
import ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom.ChatRoomController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom.ChatRoomModel;
import ch.progradler.rat_um_rad.client.gui.javafx.game.gameMap.GameMapController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.gameMap.GameMapModel;
import ch.progradler.rat_um_rad.client.gui.javafx.game.ownPlayerOverview.OwnPlayerOverviewController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.ownPlayerOverview.OwnPlayerOverviewModel;
import ch.progradler.rat_um_rad.client.gui.javafx.game.playerOverview.PlayerOverviewController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.playerOverview.PlayerOverviewModel;
import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class GameController {
    Stage stage;
    GameModel gameModel;
    private GameService gameService;
    @FXML
    private ActivityController activityController = new ActivityController();
    @FXML
    private PlayerOverviewController playerOverviewController = new PlayerOverviewController();
    @FXML
    private OwnPlayerOverviewController ownPlayerOverviewController = new OwnPlayerOverviewController();
    @FXML
    private GameMapController gameMapController = new GameMapController();
    @FXML
    private ChatRoomController chatRoomController = new ChatRoomController();

    /**
     * 1. Warten auf Spieler in Lobby
     * 2. Game Startet
     * 3. --> Karten wählen
     * 4. --> Farbe zugewiesen, Radkarten bekommen etc.
     * 5. Karte anzeigen
     */
    public GameController() {
        this.gameService = new GameService();
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<ClientGame>() {
            @Override
            public void serverResponseReceived(ClientGame content) {
                gameUpdated(content);
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.GAME_UPDATED;
            }
        });
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<ClientGame>() {
            @Override
            public void serverResponseReceived(ClientGame content) {
                startGameChooseDestinationCards(content);
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.GAME_STARTED_SELECT_DESTINATION_CARDS;
            }
        });
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<ClientGame>() {
            @Override
            public void serverResponseReceived(ClientGame content) {
                startGameChooseDestinationCards(content);
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.REQUEST_SHORT_DESTINATION_CARDS_RESULT;
            }
        });
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<ClientGame>() {
            @Override
            public void serverResponseReceived(ClientGame content) {
                showWinner(content);
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.GAME_ENDED;
            }
        });
    }

    /**
     * initializes the game model and binds data to view.
     *
     * @param gameModel
     * @param stage
     */
    public void initData(GameModel gameModel, Stage stage, ChatRoomModel chatRoomModel) {
        this.stage = stage;
        this.gameModel = gameModel;
        this.activityController.initData(new ActivityModel());
        this.chatRoomController.initData(chatRoomModel);
        this.playerOverviewController.initData(new PlayerOverviewModel());
        this.playerOverviewController.updatePlayerOverview(gameModel.getClientGame().getOwnPlayer(), gameModel.getClientGame().getOtherPlayers());
        this.ownPlayerOverviewController.initData(new OwnPlayerOverviewModel());
        this.ownPlayerOverviewController.updatePlayer(gameModel.getClientGame().getOwnPlayer());
        this.gameModel.setClientGame(gameModel.getClientGame());
        this.gameMapController.initData(new GameMapModel(gameModel.getClientGame())); //TODO: maybe only call after game is started (in serverresponsehandler)
        this.gameMapController.updateGameMapModel(gameModel.getClientGame());
        this.activityController.updateActivities(gameModel.getClientGame().getActivities());
    }

    public void gameUpdated(ClientGame content) {
        Platform.runLater(() -> {
            this.gameModel.setClientGame(content);
            this.gameMapController.initData(new GameMapModel(content)); //TODO: maybe only call after game is started (in serverresponsehandler)
            this.gameMapController.updateGameMapModelWithMap(content);
            this.activityController.updateActivities(content.getActivities());
            this.playerOverviewController.updatePlayerOverview(content.getOwnPlayer(), content.getOtherPlayers());
            this.ownPlayerOverviewController.updatePlayer(content.getOwnPlayer());
            this.stage.show();
        });
        //TODO: if destinationcards received run chooseDestinationCards();
    }

    private void startGameChooseDestinationCards(ClientGame clientGame) {
        Platform.runLater(() -> {
            gameModel.getListener().selectDestinationCards(clientGame); //TODO: when game joined, this listener is never called
        });
    }

    private void showWinner(ClientGame game) {
        Platform.runLater(() -> {
            gameModel.getListener().showWinner(game); //TODO: when game joined, this listener is never called
        });
    }
}
