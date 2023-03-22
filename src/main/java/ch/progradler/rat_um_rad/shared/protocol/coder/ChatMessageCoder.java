package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;

/**
 * En-/decodes {@link ChatMessage}
 */
public class ChatMessageCoder implements Coder<ChatMessage> {

    static final String SEPARATOR = "_/_"; // used uncommon text, so it doesn't appear in a message by accident.

    @Override
    public String encode(ChatMessage chatMessage) {
        return "{" +
                chatMessage.getUsername() + SEPARATOR +
                chatMessage.getMessage() +
                "}";
    }

    @Override
    public ChatMessage decode(String encoded) {
        String unwrapped = encoded.substring(1, encoded.length() - 1);
        String[] fields = unwrapped.split(SEPARATOR);
        String username = fields[0];
        String message = fields[1];
        return new ChatMessage(username, message);
    }
}
