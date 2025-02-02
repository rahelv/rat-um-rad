package ch.progradler.rat_um_rad.client.gui.javafx;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeController;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeModel;
import ch.progradler.rat_um_rad.client.gui.javafx.game.GameController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.GameModel;
import ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom.ChatRoomModel;
import ch.progradler.rat_um_rad.client.gui.javafx.game.chooseCard.ChooseCardController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.chooseCard.ChooseCardModel;
import ch.progradler.rat_um_rad.client.gui.javafx.game.gameEndPhase.EndPhaseController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.gameEndPhase.EndPhaseModel;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.StartupPageController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.StartupPageModel;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.createGame.CreateGameController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.createGame.CreateGameModel;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.gameOverview.GameOverviewController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.gameOverview.GameOverviewModel;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.highScore.HighScoreController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.highScore.HighScoreModel;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby.LobbyModel;
import ch.progradler.rat_um_rad.client.models.User;
import ch.progradler.rat_um_rad.client.utils.listeners.ControllerChangeListener;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * This is the Rat um Rad JavaFX-Application.
 */
public class GUI extends Application implements ControllerChangeListener<UsernameChangeController> {
    public static final Logger LOGGER = LogManager.getLogger();
    Stage window;
    Scene mainScene;
    private UsernameChangeModel usernameChangeModel;
    private UsernameChangeController usernameChangeController;
    private StartupPageModel startupPageModel;
    private StartupPageController startupPageController;
    private GameOverviewModel gameOverviewModel;
    private GameOverviewController gameOverviewController;
    private CreateGameModel createGameModel;
    private CreateGameController createGameController;
    private LobbyModel lobbyModel;
    private GameModel gameModel;
    private GameController gameController;
    private ChooseCardModel chooseCardModel;
    private ChooseCardController chooseCardController;
    private EndPhaseController endPhaseController;
    private HighScoreModel highScoreModel;
    private HighScoreController highScoreController;
    private ChatRoomModel chatRoomModel;

    /**
     * Launching this method will not work on some platforms.
     * What you should do is to create a separate main class and launch the GUI class from there as is done in {@link Main}
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * starts the GUI Application, see {@link ch.progradler.rat_um_rad.client.Client}
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     */
    @Override
    public void start(Stage primaryStage) {
        /**
         * Listener for INVALID_ACTION_FATAL. creates a popup with the error message when an error is thrown.
         */
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<ClientGame>() {
            @Override
            public void serverResponseReceived(ClientGame game) {
                Platform.runLater(() -> {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Player Disconnected.");
                    errorAlert.setHeaderText("a player disconnected.");
                    errorAlert.setContentText("The Game ends now. You will be returned to the startup page.");
                    errorAlert.initModality(Modality.APPLICATION_MODAL);
                    errorAlert.showAndWait();
                    controllerChanged("showStartupPage");
                });
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.GAME_ENDED_BY_PLAYER_DISCONNECTION;
            }
        });

        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<String>() {
            @Override
            public void serverResponseReceived(String error) {
                //TODO: call popup and display error message
                Platform.runLater(() -> {
                    Alert errorAlert = new Alert(Alert.AlertType.WARNING);
                    errorAlert.setTitle("An Error Occured");
                    errorAlert.setHeaderText(error);
                    errorAlert.setContentText("Please try again"); //TODO: display different text according to error thrown
                    errorAlert.initModality(Modality.APPLICATION_MODAL);
                    errorAlert.showAndWait();
                });
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.INVALID_ACTION_FATAL;
            }
        });
        Parameters parameters = getParameters();
        List<String> paramlist = parameters.getRaw();

        /**
         * check if username was chosen through command line args.
         * if no param exists, throws an indexoutofbounds exception.
         */
        try {
            if (!paramlist.get(0).equals("")) {
                this.usernameChangeModel = new UsernameChangeModel(new User(), this, paramlist.get(0));
            } else {
                this.usernameChangeModel = new UsernameChangeModel(new User(), this);
            }
        } catch (IndexOutOfBoundsException e) {
            if (e instanceof IndexOutOfBoundsException) {
                LOGGER.error("can't start stage correctly");
            }
            this.usernameChangeModel = new UsernameChangeModel(new User(), this);
        }

        this.createGameModel = new CreateGameModel(this);
        this.gameOverviewModel = new GameOverviewModel(this);
        this.lobbyModel = new LobbyModel();
        this.chooseCardModel = new ChooseCardModel(this);

        this.window = primaryStage;

        this.startupPageModel = new StartupPageModel(this);
        this.highScoreModel = new HighScoreModel(this);
        loadControllers();

        this.loadFXMLView("/views/ChangeUsernameView.fxml", this.usernameChangeController);
        this.usernameChangeController.initData(this.usernameChangeModel, this.window);
        this.window.show();
    }

    private void loadControllers() {
        this.usernameChangeController = new UsernameChangeController();
        this.createGameController = new CreateGameController();
        this.gameOverviewController = new GameOverviewController();
        this.gameController = new GameController();
        this.startupPageController = new StartupPageController();
        this.chooseCardController = new ChooseCardController();
        this.endPhaseController = new EndPhaseController();
        this.highScoreController = new HighScoreController();
    }

    /**
     * Takes a path to the corresponding FXML file and loads it.
     *
     * @param path
     * @return
     */
    private FXMLLoader loadFXMLView(String path, Object controller) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(path));
        loader.setController(controller);
        try {
            Parent content = loader.load();
            this.mainScene = new Scene(content, 1000, 600);
            this.window.setScene(mainScene);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof IOException) {
                LOGGER.error("FXMLLoader can't load view correctly");
            }
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
                this.loadFXMLView("/views/StartupPage.fxml", this.startupPageController);
                this.startupPageController.initData(this.usernameChangeModel, this.startupPageModel, this.window, this.lobbyModel);
                this.window.show();
            }
            case "showUsernameChange" -> {
                this.loadFXMLView("/views/ChangeUsernameView.fxml", this.usernameChangeController);
                usernameChangeController.initData(this.usernameChangeModel, this.window);
                this.window.show();
            }
            case "showAllGamesView" -> {
                this.loadFXMLView("/views/GameOverview.fxml", this.gameOverviewController);
                gameOverviewController.initData(this.gameOverviewModel, this.lobbyModel);
                this.window.show();
            }
            case "createGame" -> {
                this.loadFXMLView("/views/CreateGameView.fxml", this.createGameController);
                this.createGameController.initData(this.createGameModel, this.window);
                this.window.show();
            }
            case "showHighScores" -> {
                this.loadFXMLView("/views/HighScoreView.fxml", this.highScoreController);
                this.highScoreController.initData(this.highScoreModel, this.window);
                this.window.show();
            }
        }

    }

    /**
     * Listener for createGameView --> creates the game model, instantiates the game View
     *
     * @param content
     */
    @Override
    public void gameCreated(ClientGame content) {
        this.loadFXMLView("/views/game/GameView.fxml", this.gameController);
        this.gameModel = new GameModel(this, content); //TODO: gameController which is given to fxmlview also updated ??
        this.chatRoomModel = new ChatRoomModel();
        this.gameController.initData(this.gameModel, this.window, this.chatRoomModel);

        this.window.show();
    }

    @Override
    public void selectDestinationCards(ClientGame clientGame) {
        this.chooseCardModel.setLongDestinationCard(clientGame.getOwnPlayer().getLongDestinationCard());
        this.chooseCardModel.updateDestinationCardList(clientGame.getOwnPlayer().getShortDestinationCardsToChooseFrom());
        this.loadFXMLView("/views/game/ChooseCardView.fxml", this.chooseCardController);
        this.chooseCardController.initData(this.chooseCardModel, this.window);
        this.window.show();
    }

    @Override
    public void returnToGame(ClientGame clientGame) {
        this.gameModel.setClientGame(clientGame);
        this.loadFXMLView("/views/game/GameView.fxml", this.gameController);
        gameController.initData(this.gameModel, this.window, this.chatRoomModel);
        this.window.show();
    }

    @Override
    public void showWinner(ClientGame game) {
        EndPhaseModel endPhaseModel = new EndPhaseModel(game, this);
        this.loadFXMLView("/views/EndPhaseView.fxml", this.endPhaseController);
        this.endPhaseController.initData(endPhaseModel, this.window);
        this.window.show();
    }
}
