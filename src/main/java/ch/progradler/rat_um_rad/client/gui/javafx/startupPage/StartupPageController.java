package ch.progradler.rat_um_rad.client.gui.javafx.startupPage;

import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeController;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartupPageController implements Initializable {
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

    public void initData(UsernameChangeModel usernameChangeModel, Stage stage) {
        this.stage = stage;
        this.usernameChangeModel = usernameChangeModel;
        welcomeLabel.setText("Herzlich Willkommen " + usernameChangeModel.getCurrentUsername() + " !");
    }

    private void showUsernameChangeDialog() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/changeUsernameDialog.fxml"));

        try {
            AnchorPane root = loader.load();
            this.stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }

        UsernameChangeController controller = loader.getController();
        controller.initData(this.usernameChangeModel, this.stage);

        this.stage.show();
        //TODO: when username is set, go on to startupPage
    }
}
