package ch.progradler.rat_um_rad.shared.protocol;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;

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
     * Type of {@link UsernameChange}
     */
    USERNAME_CHANGE,
    /**
     * Type of {@code null}
     */
    NONE,
}
