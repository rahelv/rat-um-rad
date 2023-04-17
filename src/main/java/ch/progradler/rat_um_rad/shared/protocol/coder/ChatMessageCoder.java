package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;

import java.util.List;

/**
 * En-/decodes {@link ChatMessage}
 */
public class ChatMessageCoder implements Coder<ChatMessage> {
    /**
     * receives a chatMessage and encodes it to the defined serialized format.
     *
     * @param chatMessage
     * @param level
     * @return
     */
    @Override
    public String encode(ChatMessage chatMessage, int level) {
        return CoderHelper.encodeFields(level, chatMessage.getUsername(), chatMessage.getMessage());
    }

    /**
     * receives an encoded String and decodes it to class ChatMessage
     *
     * @param encoded
     * @param level
     * @return
     */
    @Override
    public ChatMessage decode(String encoded, int level) {
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        String username = fields.get(0);
        String message = (fields.size() < 2) ? "" : fields.get(1);

        return new ChatMessage(username, message);
    }
}
