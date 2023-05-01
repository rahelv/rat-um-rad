package ch.progradler.rat_um_rad.server.validation;

import ch.progradler.rat_um_rad.server.models.Action;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.services.GameServiceUtil;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;

import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.*;
import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ActionValidatorTest {
    private final String actionData = "someAction";
    private final String ipAddress = "clientA";
    private final ActionValidator<String> validator = new ActionValidator<>();

    @Test
    void validateReturnsCorrectErrorIfGameNull() {
        Action<String> action = new Action<>(null, ipAddress, actionData);
        assertEquals(PLAYER_IN_NO_GAME, validator.validate(action));
    }

    @Test
    void validateReturnsCorrectErrorIfStatusIsNotStarted() {
        String ipAddress = "clientA";
        Game game = mock(Game.class);
        for (GameStatus status : Arrays.asList(WAITING_FOR_PLAYERS, PREPARATION, FINISHED)) {
            when(game.getStatus()).thenReturn(status);

            Action<String> action = new Action<>(game, ipAddress, actionData);
            assertEquals(GAME_NOT_STARTED, validator.validate(action));
        }
    }

    @Test
    void validateReturnsCorrectErrorIfIsNotPlayersTurn() {
        Game game = mock(Game.class);
        when(game.getStatus()).thenReturn(STARTED);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.isPlayersTurn(game, ipAddress))
                    .thenReturn(false);
            Action<String> action = new Action<>(game, ipAddress, actionData);
            assertEquals(NOT_PLAYERS_TURN, validator.validate(action));
        }
    }

    @Test
    void validateReturnsNoErrorAndCallsUpdateGameStateIfAllOk() {
        Game game = mock(Game.class);
        when(game.getStatus()).thenReturn(STARTED);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.isPlayersTurn(game, ipAddress))
                    .thenReturn(true);
            Action<String> action = new Action<>(game, ipAddress, actionData);
            assertNull(validator.validate(action));
        }
    }
}