package ch.progradler.rat_um_rad.server.services.action_handlers;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Action;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.server.services.GameServiceUtil;
import ch.progradler.rat_um_rad.server.services.HighscoreManager;
import ch.progradler.rat_um_rad.server.validation.Validator;
import ch.progradler.rat_um_rad.shared.models.Activity;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DecksOfGame;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.FINISHED;
import static ch.progradler.rat_um_rad.shared.protocol.ServerCommand.GAME_ENDED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionHandlerTest {
    private static final String IP_ADDRESS = "clientA";
    private final String actionData = "someAction";
    private final DecksOfGame decksOfGame = mock(DecksOfGame.class);
    @Mock
    IGameRepository mockGameRepository;
    @Mock
    IUserRepository mockUserRepository;
    @Mock
    OutputPacketGateway mockOutputPacketGateway;
    @Mock
    GameEndUtil mockGameEndUtil;
    @Mock
    HighscoreManager mockHighscoreManager;
    @Mock
    Validator<Action<String>> mockActionValidator;
    private String validateResult = null;
    private boolean canEndGame = false;
    private ServerCommand command = ServerCommand.ROAD_BUILT;
    private boolean updateGameStateCalled = false;
    private ActionHandler<String> actionHandler;

    @BeforeEach
    void setUp() {
        actionHandler = new ActionHandler<>(mockGameRepository, mockUserRepository, mockOutputPacketGateway, mockGameEndUtil, mockHighscoreManager, mockActionValidator) {
            @Override
            protected String validate(Game game, String ipAddress, String actionData) {
                return validateResult;
            }

            @Override
            protected void updateGameState(Game game, String ipAddress, String actionData) {
                updateGameStateCalled = true;
            }

            @Override
            protected boolean canEndGame() {
                return canEndGame;
            }

            @Override
            protected ServerCommand getActivityCommand() {
                return command;
            }
        };
    }

    @Test
    void handleSendsErrorMessageToClientAndDoesNotUpdateGameStateIfSpecificValidateErrorNotNull() {
        validateResult = "some error";
        Game game = mock(Game.class);
        when(mockActionValidator.validate(any())).thenReturn(null);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(IP_ADDRESS, mockGameRepository))
                    .thenReturn(game);

            actionHandler.handle(IP_ADDRESS, actionData);

            assertFalse(updateGameStateCalled);
            utilities.verify(() -> GameServiceUtil.sendInvalidActionResponse(IP_ADDRESS, validateResult, mockOutputPacketGateway));
        }
    }

    @Test
    void handleDoesNotSendErrorMessageToClientIfSpecificValidateErrorNull() {
        validateResult = null;
        Game game = mock(Game.class);
        when(mockActionValidator.validate(any())).thenReturn(null);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(IP_ADDRESS, mockGameRepository))
                    .thenReturn(game);

            actionHandler.handle(IP_ADDRESS, actionData);

            utilities.verify(() -> GameServiceUtil.notifyPlayersOfGameAction(any(), any(), any(), any()), never());
        }
    }

    @Test
    void DoesNotUpdateStateWhenGeneralValidationFails() {
        String ipAddress = "clientA";
        when(mockActionValidator.validate(any())).thenReturn("error");

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(IP_ADDRESS, mockGameRepository))
                    .thenReturn(null);

            actionHandler.handle(IP_ADDRESS, actionData);

            assertFalse(updateGameStateCalled);
            utilities.verify(() -> GameServiceUtil.sendInvalidActionResponse(ipAddress, "error", mockOutputPacketGateway));
        }
    }

    @Test
    void DoesNotUpdateStateWhenGeneralValidationSucceeds() {
        Game game = mock(Game.class);
        when(mockActionValidator.validate(any())).thenReturn(null); // no error

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(IP_ADDRESS, mockGameRepository))
                    .thenReturn(game);

            actionHandler.handle(IP_ADDRESS, actionData);

            assertTrue(updateGameStateCalled);
            utilities.verify(() -> GameServiceUtil.sendInvalidActionResponse(any(), any(), any()), never());
        }
    }

    @Test
    void handleSavesGameAfterUpdateStateWithCorrectTurnAndActivitiesAndSendsUpdateToPlayers() {

        int turn = 5;
        List<Activity> activities = new ArrayList<>();

        String actorName = "actor";
        when(mockUserRepository.getUsername(IP_ADDRESS)).thenReturn(actorName);

        GameMap mockGameMap = mock(GameMap.class);
        Game game = new Game("gameId", GameStatus.STARTED, mockGameMap, new Date(), "creator", 3, new HashMap<>(),
                turn, new HashMap<>(), activities, decksOfGame, new ArrayList<>());

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(IP_ADDRESS, mockGameRepository))
                    .thenReturn(game);
            utilities.when(() -> GameServiceUtil.isPlayersTurn(game, IP_ADDRESS))
                    .thenReturn(true);

            actionHandler.handle(IP_ADDRESS, actionData);

            assertEquals(turn + 1, game.getTurn());
            assertEquals(Collections.singletonList(new Activity(actorName, command)), activities);

            verify(mockGameRepository).updateGame(game);
            utilities.verify(() -> GameServiceUtil.notifyPlayersOfGameUpdate(game, mockOutputPacketGateway, command));
        }
    }

    @Test
    void handleSavesChecksIfGameEndedIfCanEndGameIsTrue() {
        GameMap mockGameMap = mock(GameMap.class);
        Game game = new Game("gameId", GameStatus.STARTED, mockGameMap, "creator", 3, new HashMap<>(), decksOfGame);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(IP_ADDRESS, mockGameRepository))
                    .thenReturn(game);
            utilities.when(() -> GameServiceUtil.isPlayersTurn(game, IP_ADDRESS))
                    .thenReturn(true);

            canEndGame = true;
            actionHandler.handle(IP_ADDRESS, actionData);
            verify(mockGameEndUtil).isGameEnded(game);
        }
    }

    @Test
    void handleSavesChecksIfGameEndedIfCanEndGameIsFalse() {
        GameMap mockGameMap = mock(GameMap.class);
        Game game = new Game("gameId", GameStatus.STARTED, mockGameMap, "creator", 3, new HashMap<>(), decksOfGame);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(IP_ADDRESS, mockGameRepository))
                    .thenReturn(game);
            utilities.when(() -> GameServiceUtil.isPlayersTurn(game, IP_ADDRESS))
                    .thenReturn(true);

            canEndGame = false;
            actionHandler.handle(IP_ADDRESS, actionData);
            verifyNoInteractions(mockGameEndUtil);
        }
    }

    @Test
    void handleSavesSetsStatusToFinishedAndUpdatesScoresAndSendsGameUpdateAndSavesHighscores() {
        GameMap mockGameMap = mock(GameMap.class);

        String playerName1 = "Player 1";
        String playerName2 = "Player 2";
        int finalScore1 = 50;
        int finalScore2 = 70;
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        when(player1.getName()).thenReturn(playerName1);
        when(player2.getName()).thenReturn(playerName2);
        when(player1.getScore()).thenReturn(finalScore1);
        when(player2.getScore()).thenReturn(finalScore2);

        Map<String, Player> players = Map.of("player1", player1, "player2", player2);

        Game game = new Game("gameId", GameStatus.STARTED, mockGameMap, "creator", 3, players, decksOfGame);

        when(mockGameEndUtil.isGameEnded(game)).thenReturn(true);
        doNothing().when(mockGameEndUtil).updateScoresAndEndResult(game);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(IP_ADDRESS, mockGameRepository))
                    .thenReturn(game);
            utilities.when(() -> GameServiceUtil.isPlayersTurn(game, IP_ADDRESS))
                    .thenReturn(true);

            canEndGame = true;
            actionHandler.handle(IP_ADDRESS, actionData);

            verify(mockGameEndUtil).updateScoresAndEndResult(game);

            verify(mockHighscoreManager).attemptAddHighscore(eq(playerName1), eq(finalScore1), any());
            verify(mockHighscoreManager).attemptAddHighscore(eq(playerName2), eq(finalScore2), any());

            assertEquals(FINISHED, game.getStatus());
            utilities.verify(() -> GameServiceUtil.notifyPlayersOfGameUpdate(game, mockOutputPacketGateway, GAME_ENDED));
        }
    }
}