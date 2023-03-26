package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;

import static ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper.SEPARATOR;

/**
 * En-/decodes {@link ChatMessage}
 */
public class ChatMessageCoder implements Coder<ChatMessage> {
    /** receives a chatMessage and encodes it to the defined serialized format.
     * @param chatMessage
     * @return
     */
    @Override
    public String encode(ChatMessage chatMessage) {
        return "{" +
                chatMessage.getUsername() + SEPARATOR +
                chatMessage.getMessage() +
                "}";
    }

    /** receives an encoded String and decodes it to class ChatMessage
     * @param encoded
     * @return
     */
    @Override
    public ChatMessage decode(String encoded) {
        String unwrapped = encoded.substring(1, encoded.length() - 1);
        String[] fields = unwrapped.split(SEPARATOR);
        String username = fields[0];
        String message = fields[1];
        return new ChatMessage(username, message);
    }
}
