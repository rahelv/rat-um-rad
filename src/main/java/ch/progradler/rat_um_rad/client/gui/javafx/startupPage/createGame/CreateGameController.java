package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.createGame;

import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.client.services.UserService;
import ch.progradler.rat_um_rad.server.models.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

public class CreateGameController implements Initializable {
   //public TextField groupNameTextField;
    public Spinner<Integer> playerNumSpinner;
    public Button createGameButton;
    private CreateGameModel createGameModel;
    private IGameService gameService;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.gameService = new GameService();
        this.createGameModel = new CreateGameModel();

        SpinnerValueFactory.IntegerSpinnerValueFactory spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5, 1);
        playerNumSpinner.setValueFactory(spinnerValueFactory);
        playerNumSpinner.setEditable(false);

       //groupNameTextField.textProperty().bindBidirectional(createGameModel.getGroupNameInputProperty());
    }

    @FXML
    public void createGameButtonAction(ActionEvent actionEvent) {
        Integer playerCount = playerNumSpinner.getValue();
        try {
            gameService.createGame(playerCount.intValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
