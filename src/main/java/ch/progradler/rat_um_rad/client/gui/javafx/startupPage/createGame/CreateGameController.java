package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.createGame;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateGameController implements Initializable {
   //public TextField groupNameTextField;
    private Stage stage;
    public Spinner<Integer> playerNumSpinner;
    public Button createGameButton;
    private CreateGameModel createGameModel;
    private IGameService gameService;

    /** initializes the controller.
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<ClientGame>() {
            @Override
            public void serverResponseReceived(ClientGame content) {
                gameCreated(content);
            }

            @Override
            public Command forCommand() {
                return Command.GAME_CREATED;
            }
        });

        this.gameService = new GameService();

        SpinnerValueFactory.IntegerSpinnerValueFactory spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5, 1);
        playerNumSpinner.setValueFactory(spinnerValueFactory);
        playerNumSpinner.setEditable(false);

       //groupNameTextField.textProperty().bindBidirectional(createGameModel.getGroupNameInputProperty());
    }

    /** initializes the model which comes from the GUI class.
     * @param createGameModel
     * @param window
     */
    public void initData(CreateGameModel createGameModel, Stage window) {
        this.createGameModel = createGameModel;
        this.stage = window;
    }

    /** bound to createGameButton in View. sends request to server to create game through gameService.
     * @param actionEvent
     */
    @FXML
    public void createGameButtonAction(ActionEvent actionEvent) {
        Integer playerCount = playerNumSpinner.getValue();
        try {
            gameService.createGame(playerCount.intValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** listener for @ServerResponseHandler. When game is created, sends notification to listener (here GUI class) so it can set the according scene.
     * @param clientGame
     */
    public void gameCreated(ClientGame clientGame) {
        //TODO: open game view as soon as game is received.
        Platform.runLater(() -> {
            createGameModel.getListener().gameCreated(clientGame);
        });
    }
}
