package ch.progradler.rat_um_rad.client.gui.javafx;

import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeDialogView;
import ch.progradler.rat_um_rad.client.services.UserService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ServiceLoader;

/**
 * This is the Rat um Rad JavaFX-Application.
 */
public class GUI extends Application {
    private UserService userService =  ServiceLoader.load(UserService.class).findFirst();
    private MenuBar menuBar;
    private VBox vBox;

    /**
     * Launching this method will not work on some platforms.
     * What you should do is to create a separate main class and launch the GUI class from there as is done in {@link Main}
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.menuBar = new RatUmRadMenuBar();

        vBox = new VBox(menuBar);

        Scene scene = new Scene(vBox, 640, 480);
        stage.setScene(scene);
        stage.show();

        UsernameChangeDialogView usernameChangeDialogView = new UsernameChangeDialogView(stage, userService);
        usernameChangeDialogView.getView();
    }
}
