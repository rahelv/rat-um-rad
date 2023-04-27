package ch.progradler.rat_um_rad.shared.protocol.coder.packet;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.models.game.BuildRoadInfo;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.protocol.ClientCommand;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;

/**
 * Responsible for en-/decoding a {@link Packet}
 */
public abstract class PacketCoder<Command> implements Coder<Packet<Command>> {

    protected final PacketContentCoder contentCoder;

    public PacketCoder(PacketContentCoder contentCoder) {
        this.contentCoder = contentCoder;
    }

    public PacketCoder(Coder<ChatMessage> messageCoder,
                       Coder<UsernameChange> usernameChangeCoder,
                       Coder<GameBase> gameBaseCoder,
                       Coder<ClientGame> clientGameCoder,
                       Coder<BuildRoadInfo> buildRoadInfoCoder) {
        contentCoder = new PacketContentCoder(messageCoder,
                usernameChangeCoder,
                gameBaseCoder,
                clientGameCoder,
                buildRoadInfoCoder);
    }

    public static PacketCoder<ClientCommand> defaultClientPacketCoder() {
        return new ClientPacketCoder(PacketContentCoder.defaultPacketContentCoder());
    }

    public static PacketCoder<ServerCommand> defaultServerPacketCoder() {
        return new ServerPacketCoder(PacketContentCoder.defaultPacketContentCoder());
    }
}
