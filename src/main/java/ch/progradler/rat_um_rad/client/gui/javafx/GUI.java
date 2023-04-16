package ch.progradler.rat_um_rad.client.gui.javafx;

import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeController;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeModel;
import ch.progradler.rat_um_rad.client.gui.javafx.game.GameController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.GameModel;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.StartupPageController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.StartupPageModel;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.createGame.CreateGameController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.createGame.CreateGameModel;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.gameOverview.ShowAllGamesController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.gameOverview.ShowAllGamesModel;
import ch.progradler.rat_um_rad.client.models.User;
import ch.progradler.rat_um_rad.client.services.UserService;
import ch.progradler.rat_um_rad.client.utils.listeners.ControllerChangeListener;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * This is the Rat um Rad JavaFX-Application.
 */
public class GUI extends Application implements ControllerChangeListener<UsernameChangeController> {
    private UsernameChangeModel usernameChangeModel;
    private StartupPageModel startupPageModel;
    private ShowAllGamesModel showAllGamesModel;
    private CreateGameModel createGameModel;
    private GameModel gameModel;
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
        Parameters parameters = getParameters();
        List<String> paramlist = parameters.getRaw();

        if(!paramlist.get(0).equals("")) {
           this.usernameChangeModel = new UsernameChangeModel(new User(), this, paramlist.get(0));
        } else {
            this.usernameChangeModel = new UsernameChangeModel(new User(), this);
        }
        this.createGameModel = new CreateGameModel(this);
        this.showAllGamesModel = new ShowAllGamesModel(this);

        this.window = primaryStage;

        this.startupPageModel = new StartupPageModel(this);

        UsernameChangeController usernameChangeController = this.loadFXMLView("/views/changeUsernameDialog.fxml").getController();
        usernameChangeController.initData(this.usernameChangeModel, this.window);
        this.window.show();
    }

    private FXMLLoader loadFXMLView(String path) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(path));
        try {
            Parent content = loader.load();
            this.mainScene = new Scene(content, 640, 480);
            this.window.setScene(mainScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loader;
    }

    /**
     * listens for changes from controller and loads the page requested.
     */
    @Override
    public void controllerChanged(String command) {
        switch (command) {
            case "showStartupPage" -> {
                StartupPageController startupPageController = this.loadFXMLView("/views/mainPage.fxml").getController();
                //TODO: handle initialization of included lobby controller https://stackoverflow.com/questions/47295128/javafx-include-fxml-with-an-event-in-it
                startupPageController.initData(this.usernameChangeModel, this.startupPageModel, this.window);
                this.window.show();
            }
            case "showUsernameChange" -> {
                UsernameChangeController usernameChangeController = this.loadFXMLView("/views/changeUsernameDialog.fxml").getController();
                usernameChangeController.initData(this.usernameChangeModel, this.window);
                this.window.show();
            }
            case "showAllGamesView" -> {
                ShowAllGamesController showAllGamesController = this.loadFXMLView("/views/showAllGamesView.fxml").getController();
                showAllGamesController.initData(this.showAllGamesModel);
                this.window.show();
            }
            case "createGame" -> {
                CreateGameController controller = this.loadFXMLView("/views/createGameView.fxml").getController();
                controller.initData(this.createGameModel, this.window);
                this.window.show();
            }
        }
    }

    /** Listener for createGameView --> creates the game model, instantiates the game View
     * @param content
     */
    @Override
    public void gameCreated(ClientGame content) {
        this.gameModel = new GameModel(content);

        GameController controller = this.loadFXMLView("/views/game/gameView.fxml").getController();
        controller.initData(this.gameModel, this.window);
        this.window.show();
    }
}
