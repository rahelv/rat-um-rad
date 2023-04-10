package ch.progradler.rat_um_rad.shared.protocol;

import ch.progradler.rat_um_rad.client.models.ClientGame;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;

import java.util.List;

import java.util.List;

/**
 * Possible ContentType of packet content
 */
public enum ContentType {
    /**
     * Type of {@link ChatMessage}
     */
    CHAT_MESSAGE,
    /**
     * Type of {@link String}
     */
    STRING,
    /**
     * Type of {@link Integer} or <code>int</code>
     */
    INTEGER,
    /**
     * Type of {@link UsernameChange}
     */
    USERNAME_CHANGE,
    /**
     * Type of {@link ClientGame}
     */
    GAME,
    /**
     * Type of {@link List<String>}
     */
    STRING_LIST,
    /**
     * Type of {@link List<GameBase>}
     */
    GAME_INFO_LIST,
    /**
     * Type of {@link GameStatus}
     */
    GAME_STATUS,
    /**
     * Type of {@code null}
     */
    NONE,
}
