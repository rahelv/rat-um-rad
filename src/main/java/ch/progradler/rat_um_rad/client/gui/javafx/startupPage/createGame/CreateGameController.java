package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.createGame;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class CreateGameController {
    public static final Logger LOGGER = LogManager.getLogger();
    public Button createGameButton;
    private Stage stage;
    @FXML
    private Spinner<Integer> playerNumSpinner;
    private CreateGameModel createGameModel;
    private IGameService gameService;

    public CreateGameController() {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<ClientGame>() {
            @Override
            public void serverResponseReceived(ClientGame content) {
                gameCreated(content);
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.GAME_CREATED;
            }
        });

        this.gameService = new GameService();
    }

    /**
     * initializes the model which comes from the GUI class.
     *
     * @param createGameModel
     * @param window
     */
    public void initData(CreateGameModel createGameModel, Stage window) {
        this.createGameModel = createGameModel;
        this.stage = window;


        SpinnerValueFactory.IntegerSpinnerValueFactory spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5, 1);
        playerNumSpinner.setValueFactory(spinnerValueFactory);
        playerNumSpinner.setEditable(false);
    }

    /**
     * bound to createGameButton in View. sends request to server to create game through gameService.
     *
     * @param actionEvent
     */
    @FXML
    public void createGameButtonAction(ActionEvent actionEvent) {
        Integer playerCount = playerNumSpinner.getValue();
        try {
            gameService.createGame(playerCount.intValue());
        } catch (IOException e) {
            if (e instanceof IOException) {
                LOGGER.error("create game button action has error");
            }
            e.printStackTrace();
        }
    }

    /**
     * listener for @ServerResponseHandler. When game is created, sends notification to listener (here GUI class) so it can set the according scene.
     *
     * @param clientGame
     */
    public void gameCreated(ClientGame clientGame) {
        //TODO: open game view as soon as game is received.
        Platform.runLater(() -> {
            createGameModel.getListener().gameCreated(clientGame);
        });
    }
}
