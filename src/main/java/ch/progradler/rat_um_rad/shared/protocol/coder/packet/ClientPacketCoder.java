package ch.progradler.rat_um_rad.shared.protocol.coder.packet;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.Highscore;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.models.game.BuildRoadInfo;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.protocol.ClientCommand;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper;

import java.util.List;

/**
 * Responsible for en-/decoding a {@link Packet<ClientCommand>}
 */
public class ClientPacketCoder extends PacketCoder<ClientCommand> {
    public ClientPacketCoder(PacketContentCoder contentCoder) {
        super(contentCoder);
    }

    public ClientPacketCoder(Coder<ChatMessage> messageCoder,
                             Coder<UsernameChange> usernameChangeCoder,
                             Coder<GameBase> gameBaseCoder,
                             Coder<ClientGame> clientGameCoder,
                             Coder<BuildRoadInfo> buildRoadInfoCoder, Coder<Highscore> highscoreCoder) {
        super(messageCoder, usernameChangeCoder, gameBaseCoder, clientGameCoder, buildRoadInfoCoder, highscoreCoder);
    }

    /**
     * @param packet
     * @param level
     * @return String encoded in our network protocol.
     * Will be in format "command-/-{encodedContent}-/-contentType"
     */
    @Override
    public String encode(Packet<ClientCommand> packet, int level) {
        ContentType contentType = packet.getContentType();

        return CoderHelper.encodeFields(level, packet.getCommand().name(),
                contentCoder.encodeContent(packet.getContent(), contentType, level + 1),
                contentType.name());
    }

    @Override
    public Packet<ClientCommand> decode(String encoded, int level) {
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        if (fields.size() < 3) {
            // should never happen
            throw new IllegalArgumentException("Cannot decode Packet String because it hat missing fields: " + encoded);
        }

        ContentType contentType = ContentType.valueOf(fields.get(2));
        ClientCommand command = ClientCommand.valueOf(fields.get(0));
        Object content = contentCoder.decodeContent(fields.get(1), contentType, level + 1);
        return new Packet.Client(command, content, contentType);
    }
}
