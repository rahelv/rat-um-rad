package ch.progradler.rat_um_rad.client.gui.javafx;

import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeController;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeModel;
import ch.progradler.rat_um_rad.client.models.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This is the Rat um Rad JavaFX-Application.
 */
public class GUI extends Application {
    private UsernameChangeModel usernameChangeModel;
    Stage window;
    Scene mainScene;
    /**
     * Launching this method will not work on some platforms.
     * What you should do is to create a separate main class and launch the GUI class from there as is done in {@link Main}
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.usernameChangeModel = new UsernameChangeModel(new User());
        this.window = primaryStage;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxmlView/mainPage.fxml"));
        try {
            Parent content = loader.load();
            this.mainScene = new Scene(content, 640, 480);
            primaryStage.setScene(mainScene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        showUsernameChangeDialog();
    }

    private void showUsernameChangeDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlView/changeusernameDialog.fxml"));

        try {
            this.window.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        UsernameChangeController controller = loader.getController();
        controller.initData(this.usernameChangeModel);

        this.window.show();
    }
}
