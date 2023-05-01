package ch.progradler.rat_um_rad.server.validation;

import ch.progradler.rat_um_rad.server.models.Action;
import ch.progradler.rat_um_rad.server.services.GameServiceUtil;

import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.STARTED;
import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.*;

/**
 * Validates an {@link Action<T>}
 */
public class ActionValidator<T> implements Validator<Action<T>> {
    @Override
    public String validate(Action<T> action) {
        if (action.game == null) return PLAYER_IN_NO_GAME;
        if (action.game.getStatus() != STARTED) return GAME_NOT_STARTED;

        if (!GameServiceUtil.isPlayersTurn(action.game, action.ipAddress)) return NOT_PLAYERS_TURN;
        return null;
    }
}
