package ch.progradler.rat_um_rad.client.services;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.client.gateway.OutputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
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

    public GameService() {
        this.outputPacketGateway = OutputPacketGatewaySingleton.getOutputPacketGateway();
    }

    public GameService(OutputPacketGateway outputPacketGateway) {
        this.outputPacketGateway = outputPacketGateway;
    }

    @Override
    public void createGame(int requiredPlayerCount) throws IOException {
        Packet packet = new Packet(Command.CREATE_GAME, requiredPlayerCount, ContentType.INTEGER);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void joinGame(String gameId) throws IOException {
        Packet packet = new Packet(Command.WANT_JOIN_GAME, gameId, ContentType.STRING);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void requestWaitingGames() throws IOException {
        Packet packet = new Packet(Command.REQUEST_GAMES, GameStatus.WAITING_FOR_PLAYERS, ContentType.GAME_STATUS);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void requestStartedGames() throws IOException {
        Packet packet = new Packet(Command.REQUEST_GAMES, GameStatus.STARTED, ContentType.GAME_STATUS);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void requestFinishedGames() throws IOException {
        Packet packet = new Packet(Command.REQUEST_GAMES, GameStatus.FINISHED, ContentType.GAME_STATUS);
        outputPacketGateway.sendPacket(packet);
    }

}
