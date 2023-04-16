package ch.progradler.rat_um_rad.client.gui.javafx;

import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeController;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeModel;
import ch.progradler.rat_um_rad.client.gui.javafx.game.GameController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.GameModel;
import ch.progradler.rat_um_rad.client.gui.javafx.game.chooseCard.ChooseCardController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.chooseCard.ChooseCardModel;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.StartupPageController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.StartupPageModel;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.createGame.CreateGameController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.createGame.CreateGameModel;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.gameOverview.GameOverviewController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.gameOverview.GameOverviewModel;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby.LobbyModel;
import ch.progradler.rat_um_rad.client.models.User;
import ch.progradler.rat_um_rad.client.utils.listeners.ControllerChangeListener;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
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
    private GameOverviewModel gameOverviewModel;
    private CreateGameModel createGameModel;
    private LobbyModel lobbyModel;
    private GameModel gameModel;
    private ChooseCardModel chooseCardModel;
    Stage window;
    Scene mainScene;

    /**
     * Launching this method will not work on some platforms.
     * What you should do is to create a separate main class and launch the GUI class from there as is done in {@link Main}
     */
    public static void main(String[] args) {
        launch(args);
    }

    /** starts the GUI Application, see {@link ch.progradler.rat_um_rad.client.Client}
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     */
    @Override
    public void start(Stage primaryStage) {
        Parameters parameters = getParameters();
        List<String> paramlist = parameters.getRaw();

        /**
         * check if username was chosen through command line args.
         * if no param exists, throws an indexoutofbounds exception.
         */
        try {
            if(!paramlist.get(0).equals("")) {
                this.usernameChangeModel = new UsernameChangeModel(new User(), this, paramlist.get(0));
            } else {
                this.usernameChangeModel = new UsernameChangeModel(new User(), this);
            }
        } catch (IndexOutOfBoundsException e) {
            this.usernameChangeModel = new UsernameChangeModel(new User(), this);
        }

        this.createGameModel = new CreateGameModel(this);
        this.gameOverviewModel = new GameOverviewModel(this);
        this.lobbyModel = new LobbyModel();
        this.chooseCardModel = new ChooseCardModel(this);

        this.window = primaryStage;

        this.startupPageModel = new StartupPageModel(this);

        UsernameChangeController usernameChangeController = this.loadFXMLView("/views/ChangeUsernameView.fxml").getController();
        usernameChangeController.initData(this.usernameChangeModel, this.window);
        this.window.show();
    }

    /** Takes a path to the corresponding FXML file and loads it.
     * @param path
     * @return
     */
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
                StartupPageController startupPageController = this.loadFXMLView("/views/StartupPage.fxml").getController();
                //TODO: handle initialization of included lobby controller https://stackoverflow.com/questions/47295128/javafx-include-fxml-with-an-event-in-it
                startupPageController.initData(this.usernameChangeModel, this.startupPageModel, this.window, this.lobbyModel);

                this.window.show();
            }
            case "showUsernameChange" -> {
                UsernameChangeController usernameChangeController = this.loadFXMLView("/views/ChangeUsernameView.fxml").getController();
                usernameChangeController.initData(this.usernameChangeModel, this.window);
                this.window.show();
            }
            case "showAllGamesView" -> {
                GameOverviewController gameOverviewController = this.loadFXMLView("/views/GameOverview.fxml").getController();
                gameOverviewController.initData(this.gameOverviewModel, this.lobbyModel);
                this.window.show();
            }
            case "createGame" -> {
                CreateGameController controller = this.loadFXMLView("/views/CreateGameView.fxml").getController();
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
        this.gameModel = new GameModel(this, content);

        GameController controller = this.loadFXMLView("/views/game/GameView.fxml").getController();
        controller.initData(this.gameModel, this.window);
        this.window.show();
    }

    @Override
    public void selectDestinationCards(List<DestinationCard> list) {
        ChooseCardController controller = this.loadFXMLView("/views/game/ChooseCardView.fxml").getController();
        this.chooseCardModel.updateDestinationCardList(list);
        controller.initData(this.chooseCardModel, this.window);
    }
}
