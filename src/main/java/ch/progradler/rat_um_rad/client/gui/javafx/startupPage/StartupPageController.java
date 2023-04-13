package ch.progradler.rat_um_rad.client.gui.javafx.startupPage;

import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeModel;
import ch.progradler.rat_um_rad.client.utils.listeners.ControllerChangeListener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class StartupPageController implements Initializable {
    private StartupPageModel startupPageModel;
    Stage stage;
    UsernameChangeModel usernameChangeModel;

    @FXML
    private Label welcomeLabel;

    @FXML
    private void changeUsernameButtonTriggered(ActionEvent event) {
        this.showUsernameChangeDialog();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO: add username to label
    }

    public void initData(UsernameChangeModel usernameChangeModel, StartupPageModel startupPageModel, Stage stage) {
        this.usernameChangeModel = usernameChangeModel;
        this.startupPageModel = startupPageModel;
        this.stage = stage;
        welcomeLabel.setText("Herzlich Willkommen " + usernameChangeModel.getCurrentUsername() + " !");
    }

    private void showUsernameChangeDialog() {
        System.out.println("show usernamechangedialog");
        Platform.runLater(() -> {
            startupPageModel.getListener().controllerChanged("showUsernameChange");
        });
    }

    @FXML
    public void showAllGamesAction(ActionEvent event){
        Platform.runLater(() -> {
            startupPageModel.getListener().controllerChanged("showAllGamesView");
        });
    }

    @FXML
    public void createGameAction(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            startupPageModel.getListener().controllerChanged("createGame");
        });
    }
}
