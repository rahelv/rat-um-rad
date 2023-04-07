package ch.progradler.rat_um_rad.client.gui.javafx;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeController;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeDialogView;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeModel;
import ch.progradler.rat_um_rad.client.models.User;
import ch.progradler.rat_um_rad.client.services.IUserService;
import ch.progradler.rat_um_rad.client.services.UserService;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This is the Rat um Rad JavaFX-Application.
 */
public class GUI extends Application {
    private FXMLLoader loader;
    private IUserService userService;

    /**
     * Launching this method will not work on some platforms.
     * What you should do is to create a separate main class and launch the GUI class from there as is done in {@link Main}
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void initServices(){
        userService = new UserService();
        // userService =  ServiceLoader.load(UserService.class).findFirst(); TODO: use this?
    }

    @Override
    public void start(Stage stage) {
        initServices();

        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxmlView/mainPage.fxml"));
        try {
            Parent content = loader.load();
            stage.setScene(new Scene(content, 640, 480));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupUsernameController(stage);
    }

    @FXML
    public void openHandbook(ActionEvent event) {
        HandbookPopup handbookPopup = new HandbookPopup(); //TODO: only open once
    }

    private void setupUsernameController(Stage stage) {
        UsernameChangeDialogView usernameChangeDialogView = new UsernameChangeDialogView(stage);

        UsernameChangeModel usernameChangeModel = new UsernameChangeModel(new User());
        UsernameChangeController usernameChangeController = new UsernameChangeController(
                usernameChangeModel,
                usernameChangeDialogView,
                userService
        );
        InputPacketGatewaySingleton.getInputPacketGateway()
                .setUsernameChangeController(usernameChangeController);
        usernameChangeController.updateView();
    }
}
