package ch.progradler.rat_um_rad.client.services;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.client.gateway.OutputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.shared.models.game.BuildRoadInfo;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.protocol.ClientCommand;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.Packet.Client;

import java.io.IOException;
import java.util.List;

/**
 * Implementation of {@link IGameService}.
 * Uses {@link OutputPacketGateway} to send correct {@link Client}s to server.
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
        Client packet = new Client(ClientCommand.CREATE_GAME, requiredPlayerCount, ContentType.INTEGER);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void joinGame(String gameId) throws IOException {
        Client packet = new Client(ClientCommand.WANT_JOIN_GAME, gameId, ContentType.STRING);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void buildRoad(String roadId) throws IOException {
        Client packet = new Client(ClientCommand.BUILD_ROAD, roadId, ContentType.STRING);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void buildGreyRoad(String roadId, WheelColor color) throws IOException {
        Client packet = new Client(ClientCommand.BUILD_ROAD, new BuildRoadInfo(roadId, color), ContentType.BUILD_ROAD_INFO);
        outputPacketGateway.sendPacket(packet);
    }

    public void takeWheelCards() throws IOException {
        Client packet = new Client(ClientCommand.REQUEST_WHEEL_CARDS, null, ContentType.NONE);
    }

    @Override
    public void requestWaitingGames() throws IOException {
        Client packet = new Client(ClientCommand.REQUEST_GAMES, GameStatus.WAITING_FOR_PLAYERS, ContentType.GAME_STATUS);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void requestStartedGames() throws IOException {
        Client packet = new Client(ClientCommand.REQUEST_GAMES, GameStatus.STARTED, ContentType.GAME_STATUS);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void requestFinishedGames() throws IOException {
        Client packet = new Client(ClientCommand.REQUEST_GAMES, GameStatus.FINISHED, ContentType.GAME_STATUS);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void selectCards(List<String> selectedItems) throws IOException {
        Client packet = new Client(ClientCommand.SHORT_DESTINATION_CARDS_SELECTED, selectedItems, ContentType.STRING_LIST);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void requestWheelCards() throws IOException {
        Client packet = new Client(ClientCommand.REQUEST_WHEEL_CARDS, null, ContentType.NONE);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void requestShortDestinationCards() throws IOException {
        Client packet = new Client(ClientCommand.REQUEST_SHORT_DESTINATION_CARDS, null, ContentType.NONE);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void requestHighscores() throws IOException {
        Packet.Client packet = new Packet.Client(ClientCommand.REQUEST_HIGHSCORES, null, ContentType.NONE);
        outputPacketGateway.sendPacket(packet);
    }
}
