package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.client.models.ClientGame;
import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCardDeck;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.ErrorResponse;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.WAITING_FOR_PLAYERS;
import static ch.progradler.rat_um_rad.shared.protocol.Command.*;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.*;
import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.JOINING_NOT_POSSIBLE;
import static ch.progradler.rat_um_rad.shared.util.RandomGenerator.generateRandomId;

/**
 * This is the implementation of {@link IGameService}.
 */
public class GameService implements IGameService {
    private final OutputPacketGateway outputPacketGateway;
    private final IGameRepository gameRepository;
    private final IUserRepository userRepository;

    public GameService(OutputPacketGateway outputPacketGateway, IGameRepository gameRepository, IUserRepository userRepository) {
        this.outputPacketGateway = outputPacketGateway;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createGame(String creatorIpAddress, int requiredPlayerCount) {
        boolean created = false;

        GameMap map = GameMap.defaultMap();
        Map<String, Player> players = new HashMap<>();
        Player creator = GameServiceUtil.createNewPlayer(creatorIpAddress, userRepository, new HashSet<WheelColor>());
        players.put(creatorIpAddress, creator);

        Game gameCreated = null;
        while (!created) {
            try {
                gameCreated = new Game(generateRandomId(),
                        WAITING_FOR_PLAYERS,
                        map,
                        creatorIpAddress,
                        requiredPlayerCount,
                        players);
                gameRepository.addGame(gameCreated);
                created = true;
            } catch (IGameRepository.DuplicateIdException e) {
                // do nothing -> will retry with different id
            }
        }

        ClientGame clientGame = GameServiceUtil.toClientGame(gameCreated, creatorIpAddress);
        Packet response = new Packet(Command.GAME_CREATED, clientGame, ContentType.GAME);
        outputPacketGateway.sendPacket(creatorIpAddress, response);
    }

    @Override
    public void joinGame(String ipAddress, String gameId) {
        Game game = gameRepository.getGame(gameId);

        /*
        Checks whether game has status {@link GameStatus#WAITING_FOR_PLAYERS}.
        If yes, player is added.
        If not, error message is sent back.
         */
        if (game.getStatus().equals(WAITING_FOR_PLAYERS)) {
            //collect all already taken colors
            Set<WheelColor> takenColors = new HashSet<>();
            for (Player player: game.getPlayers().values()) {
                takenColors.add(player.getColor());
            }

            //add player
            Player newPlayer = GameServiceUtil.createNewPlayer(ipAddress, userRepository, takenColors);
            game.getPlayers().put(ipAddress, newPlayer);

            //inform all players with the required information
            for (String playerIpAddress: game.getPlayers().keySet()) {
                if (! playerIpAddress.equals(ipAddress)) {
                    ClientGame clientGame = GameServiceUtil.toClientGame(game, playerIpAddress);
                    outputPacketGateway.sendPacket(playerIpAddress, new Packet(NEW_PLAYER, clientGame, GAME));
                }
            }
            ClientGame clientGame = GameServiceUtil.toClientGame(game, ipAddress);
            outputPacketGateway.sendPacket(ipAddress, new Packet(GAME_JOINED, clientGame, GAME));
        } else {
            outputPacketGateway.sendPacket(ipAddress, new Packet(INVALID_ACTION_FATAL, ErrorResponse.JOINING_NOT_POSSIBLE, STRING));
            return;
        }

        //check, whether there are enough players. If yes, start Game.
        if (game.getRequiredPlayerCount() == game.getPlayers().size()) {
            startGame();
        }
    }

    private void startGame() {
        //TODO: implement
    }

    @Override
    public void sendMessageTo(String ipAddressFrom, String ipAddressTo) {
        //TODO: implement
    }

    @Override
    public void sendMessageToAll(String ipAddressFrom) {
        //TODO: implement
    }

    @Override
    public void exitGame(String ipAddress) {
        //TODO: implement
    }

    @Override
    public void selectShortDestinationCards(String ipAddress, DestinationCardDeck destinationCardDeck) {
        //TODO: implement
    }

    @Override
    public void buildRoad(String ipAddress, String roadId) {
        //TODO: implement
    }

    @Override
    public void buildGreyRoad(String ipAddress, String roadId, WheelColor color) {
        //TODO: implement
    }

    @Override
    public void takeWheelCardFromDeck(String ipAddress) {
        //TODO: implement
    }

    @Override
    public void takeOpenWheelCard(String ipAdress, WheelCard wheelCard) {
        //TODO: implement
    }

    @Override
    public void takeDestinationCard(String ipAdress) {
        //TODO: implement
    }

    @Override
    public void handleConnectionLoss(String ipAddress) {
        //TODO: implement
    }

    @Override
    public void wantToFinishGame(String ipAddress) {
        //TODO: implement
    }

    @Override
    public void dontWantToFinishGame(String ipAddress) {
        //TODO: implement
    }

    @Override
    public void getWaitingGames(String ipAddress) {
        Packet packet = new Packet(SEND_GAMES, gameRepository.getWaitingGames(), GAME_INFO_LIST);
        outputPacketGateway.sendPacket(ipAddress, packet);
    }

    @Override
    public void getStartedGames(String ipAddress) {
        Packet packet = new Packet(SEND_GAMES, gameRepository.getStartedGames(), GAME_INFO_LIST);
        outputPacketGateway.sendPacket(ipAddress, packet);
    }

    @Override
    public void getFinishedGames(String ipAddress) {
        Packet packet = new Packet(SEND_GAMES, gameRepository.getFinishedGames(), GAME_INFO_LIST);
        outputPacketGateway.sendPacket(ipAddress, packet);
    }
}
