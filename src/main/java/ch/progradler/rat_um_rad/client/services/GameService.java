package ch.progradler.rat_um_rad.client.services;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.shared.models.game.BuildRoadInfo;
import ch.progradler.rat_um_rad.client.gateway.OutputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.IOException;
import java.util.List;

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
    public void buildRoad(String roadId) throws IOException {
        Packet packet = new Packet(Command.BUILD_ROAD, roadId, ContentType.STRING);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void buildGreyRoad(String roadId, WheelColor color) throws IOException {
        Packet packet = new Packet(Command.BUILD_ROAD, new BuildRoadInfo(roadId, color), ContentType.BUILD_ROAD_INFO);
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

    @Override
    public void selectCards(List<String> selectedItems) throws IOException {
        for(String card : selectedItems) {
            System.out.println(card);
        }
        Packet packet = new Packet(Command.SHORT_DESTINATION_CARDS_SELECTED_IN_PREPARATION, selectedItems, ContentType.STRING_LIST);
        outputPacketGateway.sendPacket(packet);
    }

}
