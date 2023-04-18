package ch.progradler.rat_um_rad.client.gui.javafx.game;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.gui.javafx.game.activity.ActivityController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.activity.ActivityModel;
import ch.progradler.rat_um_rad.client.gui.javafx.game.gameMap.GameMapController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.gameMap.GameMapModel;
import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    private GameService gameService;
    Stage stage;
    GameModel gameModel;
    @FXML
    private ActivityController activityController;
    @FXML
    private GameMapController gameMapController;
    /**
     * 1. Warten auf Spieler in Lobby
     * 2. Game Startet
     * 3. --> Karten wählen
     * 4. --> Farbe zugewiesen, Radkarten bekommen etc.
     * 5. Karte anzeigen
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.gameService = new GameService();
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<ClientGame>() {
            @Override
            public void serverResponseReceived(ClientGame content) {
                gameUpdated(content);
            }

            @Override
            public Command forCommand() {
                return Command.NEW_PLAYER;
            }
        });
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<ClientGame>() {
            @Override
            public void serverResponseReceived(ClientGame content) {
                startGameChooseDestinationCards(content);
            }

            @Override
            public Command forCommand() {
                return Command.GAME_STARTED_SELECT_DESTINATION_CARDS;
            }
        });
    }

    /** initializes the game model and binds data to view.
     * @param gameModel
     * @param stage
     */
    public void initData(GameModel gameModel, Stage stage) {
        this.stage = stage;
        this.gameModel = gameModel;
        this.activityController.initData(new ActivityModel());
        this.gameModel.setClientGame(gameModel.getClientGame());
        this.gameMapController.initData(new GameMapModel(gameModel.getClientGame())); //TODO: maybe only call after game is started (in serverresponsehandler)
        //TODO: this.activityController.updateActitivites();
        this.gameMapController.udpateGameMapModel(gameModel.getClientGame());
        //TODO: activities should be implemented on server - this.activityController.updateActitivies(this.gameModel.getClientGame().getActivities());
    }

    public void gameUpdated(ClientGame content) {
        this.gameModel.setClientGame(content);
        this.gameMapController.initData(new GameMapModel(content)); //TODO: maybe only call after game is started (in serverresponsehandler)
        //TODO: this.activityController.updateActitivites();
        this.gameMapController.udpateGameMapModel(content);

        //TODO: if destinationcards received run chooseDestinationCards();
    }

    private void startGameChooseDestinationCards(ClientGame clientGame) {
        Platform.runLater(() -> {
            gameModel.getListener().selectDestinationCards(clientGame); //TODO: when game joined, this listener is never called
        });
    }

    //TODO: method for chooseDestinationCards during game
}
