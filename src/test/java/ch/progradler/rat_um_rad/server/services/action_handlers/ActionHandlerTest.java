package ch.progradler.rat_um_rad.server.services.action_handlers;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.server.services.GameServiceUtil;
import ch.progradler.rat_um_rad.shared.models.Activity;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
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

import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.*;
import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.*;
import static ch.progradler.rat_um_rad.shared.protocol.ServerCommand.GAME_ENDED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionHandlerTest {
    private final String actionData = "someAction";
    private static final String IP_ADDRESS = "clientA";

    private String validateResult = null;
    private boolean canEndGame = false;
    private ServerCommand command = ServerCommand.ROAD_BUILT;
    private boolean updateGameStateCalled = false;

    @Mock
    IGameRepository mockGameRepository;
    @Mock
    IUserRepository mockUserRepository;
    @Mock
    OutputPacketGateway mockOutputPacketGateway;
    @Mock
    GameEndUtil mockGameEndUtil;

    private ActionHandler<String> actionHandler;

    private final DecksOfGame decksOfGame = mock(DecksOfGame.class);

    @BeforeEach
    void setUp() {
        actionHandler = new ActionHandler<>(mockGameRepository, mockUserRepository, mockOutputPacketGateway, mockGameEndUtil) {
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
        when(game.getStatus()).thenReturn(STARTED);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(IP_ADDRESS, mockGameRepository))
                    .thenReturn(game);
            utilities.when(() -> GameServiceUtil.isPlayersTurn(game, IP_ADDRESS))
                    .thenReturn(true);

            actionHandler.handle(IP_ADDRESS, actionData);

            assertFalse(updateGameStateCalled);
            utilities.verify(() -> GameServiceUtil.sendInvalidActionResponse(IP_ADDRESS, validateResult, mockOutputPacketGateway));
        }
    }

    @Test
    void handleDoesNotSendErrorMessageToClientIfSpecificValidateErrorNull() {
        validateResult = null;
        Game game = mock(Game.class);
        when(game.getStatus()).thenReturn(STARTED);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(IP_ADDRESS, mockGameRepository))
                    .thenReturn(game);

            actionHandler.handle(IP_ADDRESS, actionData);

            utilities.verify(() -> GameServiceUtil.notifyPlayersOfGameAction(any(), any(), any(), any()), never());
        }
    }

    @Test
    void validateGeneralReturnsCorrectErrorIfGameNull() {
        String ipAddress = "clientA";

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(IP_ADDRESS, mockGameRepository))
                    .thenReturn(null);

            actionHandler.handle(IP_ADDRESS, actionData);

            assertFalse(updateGameStateCalled);
            utilities.verify(() -> GameServiceUtil.sendInvalidActionResponse(ipAddress, PLAYER_IN_NO_GAME, mockOutputPacketGateway));
        }
    }

    @Test
    void validateGeneralReturnsCorrectErrorIfStatusIsNotStarted() {
        String ipAddress = "clientA";
        Game game = mock(Game.class);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            for (GameStatus status : Arrays.asList(WAITING_FOR_PLAYERS, PREPARATION, FINISHED)) {
                when(game.getStatus()).thenReturn(status);
                utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(IP_ADDRESS, mockGameRepository))
                        .thenReturn(game);

                actionHandler.handle(IP_ADDRESS, actionData);
            }
            assertFalse(updateGameStateCalled);
            utilities.verify(() -> GameServiceUtil.sendInvalidActionResponse(ipAddress, GAME_NOT_STARTED, mockOutputPacketGateway),
                    times(3));
        }
    }

    @Test
    void validateGeneralReturnsCorrectErrorIfIsNotPlayersTurn() {
        Game game = mock(Game.class);
        when(game.getStatus()).thenReturn(STARTED);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(IP_ADDRESS, mockGameRepository))
                    .thenReturn(game);
            utilities.when(() -> GameServiceUtil.isPlayersTurn(game, IP_ADDRESS))
                    .thenReturn(false);

            actionHandler.handle(IP_ADDRESS, actionData);
            assertFalse(updateGameStateCalled);

            utilities.verify(() -> GameServiceUtil.sendInvalidActionResponse(IP_ADDRESS, NOT_PLAYERS_TURN, mockOutputPacketGateway));
        }
    }

    @Test
    void validateGeneralReturnsNoErrorAndCallsUpdateGameStateIfAllOk() {
        Game game = mock(Game.class);
        when(game.getStatus()).thenReturn(STARTED);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(IP_ADDRESS, mockGameRepository))
                    .thenReturn(game);
            utilities.when(() -> GameServiceUtil.isPlayersTurn(game, IP_ADDRESS))
                    .thenReturn(true);

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
                turn, new HashMap<>(), activities, decksOfGame);

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
    void handleSavesSetsStatusToFinishedAndUpdatesScoresAndSendsGameUpdate() {
        GameMap mockGameMap = mock(GameMap.class);
        Game game = new Game("gameId", GameStatus.STARTED, mockGameMap, "creator", 3, new HashMap<>(), decksOfGame);

        when(mockGameEndUtil.isGameEnded(game)).thenReturn(true);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(IP_ADDRESS, mockGameRepository))
                    .thenReturn(game);
            utilities.when(() -> GameServiceUtil.isPlayersTurn(game, IP_ADDRESS))
                    .thenReturn(true);

            canEndGame = true;
            actionHandler.handle(IP_ADDRESS, actionData);

            verify(mockGameEndUtil).updateScoresAndEndResult(game);

            assertEquals(FINISHED, game.getStatus());
            utilities.verify(() -> GameServiceUtil.notifyPlayersOfGameUpdate(game, mockOutputPacketGateway, GAME_ENDED));
        }
    }
}