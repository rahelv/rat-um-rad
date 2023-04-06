package ch.progradler.rat_um_rad.client.services;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.IOException;

/**
 * Implementation of {@link IGameService}.
 * Uses {@link OutputPacketGateway} to send correct {@link Packet}s to server.
 */
public class GameService implements IGameService {
    private final OutputPacketGateway outputPacketGateway;

    public GameService(OutputPacketGateway outputPacketGateway) {
        this.outputPacketGateway = outputPacketGateway;
    }

    @Override
    public void createGame(int requiredPlayerCount) throws IOException {
        Packet packet = new Packet(Command.CREATE_GAME, requiredPlayerCount, ContentType.INTEGER);
        outputPacketGateway.sendPacket(packet);
    }
}
