package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCardDeck;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.util.GameConfig;
import ch.progradler.rat_um_rad.shared.util.RandomGenerator;

import java.util.*;
import java.util.stream.IntStream;

import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.PREPARATION;
import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.STARTED;
import static ch.progradler.rat_um_rad.shared.protocol.Command.GAME_STARTED_SELECT_DESTINATION_CARDS;
import static ch.progradler.rat_um_rad.shared.protocol.Command.INVALID_ACTION_FATAL;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.GAME;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.STRING;
import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.*;

/**
 * Util class for {@link GameService} with complex methods that are used multiple times.
 * No methods are <code>public</code>.
 */
public class GameServiceUtil {
    static ClientGame toClientGame(Game game, String forPlayerIpAddress) {
        List<VisiblePlayer> otherPlayers = new ArrayList<>();
        game.getPlayers().forEach((key, player) -> {
            if (key.equals(forPlayerIpAddress)) return;
            otherPlayers.add(toVisiblePlayer(player, key));
        });
        return new ClientGame(
                game.getId(),
                game.getStatus(),
                game.getMap(),
                game.getCreatedAt(),
                game.getCreatorPlayerIpAddress(),
                game.getRequiredPlayerCount(),
                otherPlayers,
                game.getPlayers().get(forPlayerIpAddress),
                game.getTurn(),
                game.getRoadsBuilt());
    }

    static Player createNewPlayer(String ipAddress, IUserRepository userRepository, List<WheelColor> takenColors) {
        String name = userRepository.getUsername(ipAddress);
        List<WheelColor> availableColors = new ArrayList<>(Arrays.stream(WheelColor.values()).toList());
        availableColors.removeAll(takenColors);

        WheelColor color = RandomGenerator.randomFromList(availableColors);
        return new Player(name, color, 0, GameConfig.STARTING_WHEELS_PER_PLAYER, 0);
    }

    static VisiblePlayer toVisiblePlayer(Player player, String ipAddress) {
        return new VisiblePlayer(player.getName(),
                player.getColor(),
                player.getScore(),
                player.getWheelsRemaining(),
                player.getPlayingOrder(),
                ipAddress,
                player.getWheelCards().size(),
                player.getShortDestinationCards().size()
        );
    }

    public static Game getCurrentGameOfPlayer(String playerIpAddress, IGameRepository mockGameRepository) {
        List<Game> allGames = mockGameRepository.getAllGames();
        for (Game game : allGames) {
            if (game.getPlayers().containsKey(playerIpAddress)) return game;
        }
        return null;
    }

    public static void notifyPlayersOfGameUpdate(Game game, OutputPacketGateway outputPacketGateway, Command command) {
        Set<String> playerIps = game.getPlayerIpAddresses();
        for (String ipAddress : playerIps) {
            ClientGame clientGame = GameServiceUtil.toClientGame(game, ipAddress);
            outputPacketGateway.sendPacket(ipAddress, new Packet(command, clientGame, ContentType.GAME));
        }
    }

    public static void startGame(Game game, IGameRepository gameRepository, OutputPacketGateway outputPacketGateway) {
        game.setStatus(PREPARATION);

        Map<String, Player> players = game.getPlayers();
        List<String> playerIpAddresses = game.getPlayerIpAddresses().stream().toList();
        int playerCount = playerIpAddresses.size();

        List<Integer> playingOrders = generateShuffledPlayingOrders(playerCount);

        for (int i = 0; i < playerCount; i++) {
            String ipAddress = playerIpAddresses.get(i);
            GameServiceUtil.handOutLongDestinationCard(game, ipAddress);
            GameServiceUtil.handOutShortDestinationCardsTooChoose(game, ipAddress);
            game.getPlayersHaveChosenShortDestinationCards().put(ipAddress, false);

            Player player = players.get(ipAddress);
            player.setPlayingOrder(playingOrders.get(i));
        }
        gameRepository.updateGame(game);
        for (String ipAddress : game.getPlayerIpAddresses()) {
            ClientGame clientGame = GameServiceUtil.toClientGame(game, ipAddress);
            outputPacketGateway.sendPacket(ipAddress, new Packet(GAME_STARTED_SELECT_DESTINATION_CARDS, clientGame, GAME));
        }
    }

    private static List<Integer> generateShuffledPlayingOrders(int playerCount) {
        List<Integer> playingOrders = new ArrayList<>(IntStream.range(0, playerCount).boxed().toList());
        Collections.shuffle(playingOrders);
        return playingOrders;
    }

    static void handOutLongDestinationCard(Game game, String ipAddress) {
        Player player = game.getPlayers().get(ipAddress);
        DestinationCardDeck longDestinationCardDeck = game.getDecksOfGame().getLongDestinationCardDeck();
        DestinationCard longDestinationCard = RandomGenerator.randomFromList(longDestinationCardDeck.getCardDeck());
        longDestinationCardDeck.getCardDeck().remove(longDestinationCard);
        player.setLongDestinationCard(longDestinationCard);
    }

    static void handOutShortDestinationCardsTooChoose(Game game, String ipAddress) {
        Player player = game.getPlayers().get(ipAddress);
        DestinationCardDeck shortDestinationCardDeck = game.getDecksOfGame().getShortDestinationCardDeck();

        List<DestinationCard> cardsToChooseFrom = player.getShortDestinationCards();
        for (int i = 0; i < 3; i++) {
            DestinationCard shortDestinationCard = RandomGenerator.randomFromArray(shortDestinationCardDeck.getCardDeck().toArray(new DestinationCard[0]));
            shortDestinationCardDeck.getCardDeck().remove(shortDestinationCard);
            cardsToChooseFrom.add(shortDestinationCard);
        }
        player.setShortDestinationCardsToChooseFrom(cardsToChooseFrom);
    }

    public static void startGameRounds(Game game, IGameRepository gameRepository, OutputPacketGateway outputPacketGateway) {
        // TODO: implement
        // set status to Started
        // shuffle card deck
        // save game
        // send update to all
    }

    /**
     * @return Whether or not it is the player's turn in the game.
     */
    public static boolean isPlayersTurn(Game game, String playerIp) {
        int turn = game.getTurn();
        Map<String, Player> players = game.getPlayers();
        int playerCount = players.size();
        int playerOrder = players.get(playerIp).getPlayingOrder();

        return turn % playerCount == playerOrder;
    }

    /**
     * @return whether or not action is generally valid.
     */
    public static boolean validateAndHandleActionPrecondition(String ipAddress, Game game, OutputPacketGateway outputPacketGateway) {
        if (game == null) {
            sendInvalidActionResponse(ipAddress, PLAYER_IN_NO_GAME, outputPacketGateway);
            return false;
        }

        if (game.getStatus() != STARTED) {
            sendInvalidActionResponse(ipAddress, GAME_NOT_STARTED, outputPacketGateway);
            return false;
        }

        if (!GameServiceUtil.isPlayersTurn(game, ipAddress)) {
            sendInvalidActionResponse(ipAddress, NOT_PLAYERS_TURN, outputPacketGateway);
            return false;
        }
        return true;
    }

    public static void sendInvalidActionResponse(String ipAddress, String errorMessage, OutputPacketGateway outputPacketGateway) {
        Packet errorResponse = new Packet(INVALID_ACTION_FATAL,
                errorMessage,
                STRING);
        outputPacketGateway.sendPacket(ipAddress, errorResponse);
    }
}
