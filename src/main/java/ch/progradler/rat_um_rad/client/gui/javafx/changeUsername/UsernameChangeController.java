package ch.progradler.rat_um_rad.client.gui.javafx.changeUsername;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.StartupPageController;
import ch.progradler.rat_um_rad.client.services.IUserService;
import ch.progradler.rat_um_rad.client.services.UserService;
import ch.progradler.rat_um_rad.client.utils.listeners.IListener;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.util.UsernameValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UsernameChangeController implements Initializable, IListener<UsernameChange> {
    private Stage stage;
    @FXML
    private Label usernameRulesLabel;
    @FXML
    private Label invalidLabel;

    public TextField username;

    private UsernameChangeModel usernameChangeModel;
    private UsernameValidator usernameValidator = new UsernameValidator();
    private IUserService userService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(this);
        this.userService = new UserService();
    }

    public void initData(UsernameChangeModel usernameChangeModel, Stage window) {
        this.stage = window;
        this.usernameChangeModel = usernameChangeModel;

        usernameRulesLabel.setText(usernameChangeModel.getUsernameRules());
        username.setText(usernameChangeModel.getSystemUsername());
        username.textProperty().bindBidirectional(usernameChangeModel.chosenUsernameProperty());
    }

    @FXML
    private void confirmButtonAction(ActionEvent event) throws IOException {
        String error = checkUsernameAndSendToServerIfValid();
        this.invalidLabel.setText(error);
        if(error == null) {
            invalidLabel.setVisible(false);
        } else {
            invalidLabel.setVisible(true);
            event.consume();
        }
    }

    private String checkUsernameAndSendToServerIfValid() throws IOException {
        String username = usernameChangeModel.getChosenUsername();
        if(!validateUsername(username)) return "Invalid username. See: " + usernameChangeModel.getUsernameRules();
        if(username.equals(usernameChangeModel.getCurrentUsername())){
            return usernameChangeModel.getChosenUsername() + " is already your username";
            //TODO: tell user to choose another name or cancel the action
        }
        this.userService.sendUsername(usernameChangeModel.getChosenUsername());
        return null;
    }

    public boolean validateUsername(String username) {
        return usernameValidator.isUsernameValid(username);
    }

    @Override
    public void serverResponseReceived(UsernameChange content) {
        this.usernameChangeModel.setConfirmedUsername(content.getNewName());
        //TODO: Confirm UsernameChange for User And Next View...
        Platform.runLater(() -> {
            showStartupPage();
        });
    }

    private void showStartupPage() { //TODO: move this method to class GUI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/mainPage.fxml"));

        try {
            this.stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StartupPageController controller = loader.getController();
        controller.initData(this.usernameChangeModel, this.stage);

        this.stage.show();
    }
}
