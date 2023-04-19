package ch.progradler.rat_um_rad.client.gui.javafx.changeUsername;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.services.IUserService;
import ch.progradler.rat_um_rad.client.services.UserService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.util.UsernameValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for ChangeUsernameView.fxml (in resources/views)
 */
public class UsernameChangeController {
    private Stage stage;
    @FXML
    private Label usernameRulesLabel;
    @FXML
    private Label invalidLabel;

    public TextField username;
    @FXML
    private Button confirmUsernameButton;
    @FXML
    private Button cancelButton;

    private UsernameChangeModel usernameChangeModel;
    private UsernameValidator usernameValidator = new UsernameValidator();
    private IUserService userService;

    public UsernameChangeController() {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<UsernameChange>() {
            @Override
            public void serverResponseReceived(UsernameChange content) {
                usernameChangeReceived(content);
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.USERNAME_CONFIRMED;
            }
        });
        this.userService = new UserService();
    }

    /**
     * used to initialize the Model and set Data (instead of a constructor, because class is loaded through FXML loader)
     *
     * @param usernameChangeModel
     * @param window
     */
    public void initData(UsernameChangeModel usernameChangeModel, Stage window) {
        this.stage = window;
        this.usernameChangeModel = usernameChangeModel;

        usernameRulesLabel.setText(usernameChangeModel.getUsernameRules());
        username.setText(usernameChangeModel.getSystemUsername()); //System Username is shown as the default value
        username.textProperty().bindBidirectional(usernameChangeModel.chosenUsernameProperty()); //text input bound to property in model

        if (usernameChangeModel.getCurrentUsername().equals("")) { //differentiate between new user (no username set) and username change
            this.cancelButton.setVisible(false);
        }

        if (this.usernameChangeModel.getChosenUsernameCommandLine() != null) {
            this.usernameChangeModel.setChosenUsername(this.usernameChangeModel.getChosenUsernameCommandLine());
            try {
                confirmButtonAction(new ActionEvent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * triggered when the ok button is clicked. checks the chosen username and sets an error if username is not valid.
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void confirmButtonAction(ActionEvent event) throws IOException {
        String error = checkUsernameAndSendToServerIfValid();
        this.invalidLabel.setText(error);
        if (error == null) {
            invalidLabel.setVisible(false);
        } else {
            invalidLabel.setVisible(true);
            event.consume();
        }
    }

    /**
     * notifies the GUI to change the shown scene to startupPage.
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void returnToStartupPage(ActionEvent event) throws IOException {
        Platform.runLater(() -> {
            usernameChangeModel.getListener().controllerChanged("showStartupPage");
        });
    }

    /**
     * checks if the chosenusername is valid and if it's valid sends it to server, otherwise returns error message.
     *
     * @return
     * @throws IOException
     */
    private String checkUsernameAndSendToServerIfValid() throws IOException {
        String username = usernameChangeModel.getChosenUsername();
        if (!validateUsername(username)) return "Invalid username. See: " + usernameChangeModel.getUsernameRules();
        if (username.equals(usernameChangeModel.getCurrentUsername())) {
            return usernameChangeModel.getChosenUsername() + " is already your username";
            //TODO: tell user to choose another name or cancel the action
        }
        try {
            sendUsernameToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendUsernameToServer() throws IOException {
        if (this.usernameChangeModel.getCurrentUsername().equals("")) {
            this.userService.sendUsername(usernameChangeModel.getChosenUsername());
        } else {
            this.userService.changeUsername(usernameChangeModel.getChosenUsername());
        }
    }

    /**
     * @param username
     * @return whether the username is valid or not
     */
    public boolean validateUsername(String username) {
        return usernameValidator.isUsernameValid(username);
    }

    /**
     * listens to changes from the ServerResponseHandler and reacts accordingly. (When username confirmation is received from the server, goes to next page)
     *
     * @param content
     */
    public void usernameChangeReceived(UsernameChange content) {
        this.usernameChangeModel.setConfirmedUsername(content.getNewName());
        //TODO: Confirm UsernameChange for User And Next View...
        Platform.runLater(() -> {
            usernameChangeModel.getListener().controllerChanged("showStartupPage");
        });
    }
}
