package ch.progradler.rat_um_rad.client.gui.javafx.game;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.gui.javafx.game.activity.ActivityController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.activity.ActivityModel;
import ch.progradler.rat_um_rad.client.gui.javafx.game.gameMap.GameMapController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.gameMap.GameMapModel;
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
    private GameService gameService;
    Stage stage;
    GameModel gameModel;
    @FXML
    private ActivityController activityController = new ActivityController();
    @FXML
    private PlayerOverviewController playerOverviewController = new PlayerOverviewController();
    @FXML
    private GameMapController gameMapController = new GameMapController();
    /**
     * 1. Warten auf Spieler in Lobby
     * 2. Game Startet
     * 3. --> Karten wählen
     * 4. --> Farbe zugewiesen, Radkarten bekommen etc.
     * 5. Karte anzeigen
     * */
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
    }

    /**
     * initializes the game model and binds data to view.
     *
     * @param gameModel
     * @param stage
     */
    public void initData(GameModel gameModel, Stage stage) {
        this.stage = stage;
        this.gameModel = gameModel;
        this.activityController.initData(new ActivityModel());
        this.playerOverviewController.initData(new PlayerOverviewModel());
        this.playerOverviewController.updatePlayerOverview(gameModel.getClientGame().getOtherPlayers());
        this.gameModel.setClientGame(gameModel.getClientGame());
        this.gameMapController.initData(new GameMapModel(gameModel.getClientGame())); //TODO: maybe only call after game is started (in serverresponsehandler)
        this.gameMapController.updateGameMapModel(gameModel.getClientGame());
        this.activityController.updateActivities(gameModel.getClientGame().getActivities());
    }

    public void gameUpdated(ClientGame content) {
        System.out.println("built roads: " + content.getRoadsBuilt().keySet());
        Platform.runLater(() -> {
            this.gameModel.setClientGame(content);
            this.gameMapController.initData(new GameMapModel(content)); //TODO: maybe only call after game is started (in serverresponsehandler)
            this.gameMapController.updateGameMapModelWithMap(content);
            this.activityController.updateActivities(content.getActivities());
            this.playerOverviewController.updatePlayerOverview(content.getOtherPlayers());
            this.stage.show();
        });
        //TODO: if destinationcards received run chooseDestinationCards();
    }

    private void startGameChooseDestinationCards(ClientGame clientGame) {
        Platform.runLater(() -> {
            gameModel.getListener().selectDestinationCards(clientGame); //TODO: when game joined, this listener is never called
        });
    }

    //TODO: method for chooseDestinationCards during game
}
