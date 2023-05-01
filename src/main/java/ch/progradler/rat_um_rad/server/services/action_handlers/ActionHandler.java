package ch.progradler.rat_um_rad.server.services.action_handlers;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Action;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.server.services.GameServiceUtil;
import ch.progradler.rat_um_rad.server.services.HighscoreManager;
import ch.progradler.rat_um_rad.server.validation.ActionValidator;
import ch.progradler.rat_um_rad.server.validation.Validator;
import ch.progradler.rat_um_rad.shared.models.Activity;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.protocol.ErrorResponse;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;

import java.util.Date;
import java.util.Map;

import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.FINISHED;
import static ch.progradler.rat_um_rad.shared.protocol.ServerCommand.GAME_ENDED;

/**
 * General abstraction of handling incoming action request from client.
 *
 * @param <T> Type of action data required to process action.
 */
public abstract class ActionHandler<T> {
    private final Validator<Action<T>> actionValidator;

    private final IGameRepository gameRepository;
    private final IUserRepository userRepository;
    private final OutputPacketGateway outputPacketGateway;
    private final GameEndUtil gameEndUtil;
    private final HighscoreManager highscoreManager;

    protected ActionHandler(IGameRepository gameRepository,
                            IUserRepository userRepository,
                            OutputPacketGateway outputPacketGateway,
                            GameEndUtil gameEndUtil,
                            HighscoreManager highscoreManager,
                            Validator<Action<T>> actionValidator) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.outputPacketGateway = outputPacketGateway;
        this.gameEndUtil = gameEndUtil;
        this.highscoreManager = highscoreManager;
        this.actionValidator = actionValidator;
    }

    protected ActionHandler(IGameRepository gameRepository,
                            IUserRepository userRepository,
                            OutputPacketGateway outputPacketGateway,
                            GameEndUtil gameEndUtil,
                            HighscoreManager highscoreManager) {
        this(gameRepository, userRepository, outputPacketGateway, gameEndUtil, highscoreManager, new ActionValidator<>());
    }


    protected abstract String validate(Game game, String ipAddress, T actionData);

    /**
     * Should manipulate the game instance to represent the action.
     */
    protected abstract void updateGameState(Game game, String ipAddress, T actionData);

    /**
     * @return Whether or not this action can end a game.
     */
    protected abstract boolean canEndGame();

    protected abstract ServerCommand getActivityCommand();

    public final void handle(String ipAddress, T actionData) {
        Game game = GameServiceUtil.getCurrentGameOfPlayer(ipAddress, gameRepository);

        String error = validateGeneralAndSpecific(game, ipAddress, actionData);
        if (error != null) {
            GameServiceUtil.sendInvalidActionResponse(ipAddress, error, outputPacketGateway);
            return;
        }
        assert game != null;

        updateGameStateGeneral(ipAddress, actionData, game);

        gameRepository.updateGame(game);
        GameServiceUtil.notifyPlayersOfGameUpdate(game, outputPacketGateway, getActivityCommand());

        if (canEndGame() && gameEndUtil.isGameEnded(game)) {
            game.setStatus(FINISHED);
            gameEndUtil.updateScoresAndEndResult(game);
            attemptAddHighscores(game.getPlayers());
            gameRepository.updateGame(game);
            GameServiceUtil.notifyPlayersOfGameUpdate(game, outputPacketGateway, GAME_ENDED);
        }
    }

    private void attemptAddHighscores(Map<String, Player> players) {
        for (Player player : players.values()) {
            highscoreManager.attemptAddHighscore(player.getName(), player.getScore(), new Date());
        }
    }

    private void updateGameStateGeneral(String ipAddress, T actionData, Game game) {
        updateGameState(game, ipAddress, actionData);
        game.nextTurn();

        String actorName = userRepository.getUsername(ipAddress);
        game.getActivities().add(new Activity(actorName, getActivityCommand()));
    }

    /**
     * @return error message ({@link ErrorResponse} if invalid or null if valid.
     */
    private String validateGeneralAndSpecific(Game game, String ipAddress, T actionData) {
        String actionError = actionValidator.validate(new Action<>(game, ipAddress, actionData));
        if (actionError != null) return actionError;
        return validate(game, ipAddress, actionData);
    }
}
