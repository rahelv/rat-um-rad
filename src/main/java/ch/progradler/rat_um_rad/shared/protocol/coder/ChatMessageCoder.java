package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;

import static ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper.SEPARATOR;

/**
 * En-/decodes {@link ChatMessage}
 */
public class ChatMessageCoder implements Coder<ChatMessage> {
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
