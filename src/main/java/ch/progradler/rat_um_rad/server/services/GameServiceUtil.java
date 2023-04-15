package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.client.models.ClientGame;
import ch.progradler.rat_um_rad.client.models.VisiblePlayer;
import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
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
import static ch.progradler.rat_um_rad.shared.protocol.Command.GAME_STARTED_SELECT_DESTINATION_CARDS;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.GAME;

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
                game.getTurn()
        );
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
                ipAddress,
                player.getWheelCards().size(),
                player.getShortDestinationCards().size(),
                player.getPlayingOrder()
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
        for (String ipAddress: playerIps) {
            ClientGame clientGame = GameServiceUtil.toClientGame(game, ipAddress);
            outputPacketGateway.sendPacket(ipAddress, new Packet(command, clientGame, ContentType.GAME));
        }
    }

    public static void startGame(Game game, IGameRepository gameRepository, OutputPacketGateway outputPacketGateway) {
        game.setStatus(PREPARATION);

        Map<String, Player> players = game.getPlayers();
        List<String> playerIpAddresses = game.getPlayerIpAddresses().stream().toList();
        int playerCount = playerIpAddresses.size();

        List<Integer> playingOrders = generateSuffledPlayingOrders(playerCount);

        for (int i = 0; i < playerCount; i++) {
            String ipAddress = playerIpAddresses.get(i);
            GameServiceUtil.handOutLongDestinationCard(game, ipAddress);
            GameServiceUtil.handOutShortDestinationCards(game, ipAddress);
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

    private static List<Integer> generateSuffledPlayingOrders(int playerCount) {
        List<Integer> playingOrders = new ArrayList<>(IntStream.range(0, playerCount).boxed().toList());
        Collections.shuffle(playingOrders);
        return playingOrders;
    }

    public static void handOutLongDestinationCard(Game game, String ipAddress) {
        Player player = game.getPlayers().get(ipAddress);
        DestinationCardDeck longDestinationCardDeck = game.getDecksOfGame().getLongDestinationCardDeck();
        DestinationCard longDestinationCard = RandomGenerator.randomFromList(longDestinationCardDeck.getCardDeck());
        longDestinationCardDeck.getCardDeck().remove(longDestinationCard);
        player.setLongDestinationCard(longDestinationCard);
    }

    public static void handOutShortDestinationCards(Game game, String ipAddress) {
        Player player = game.getPlayers().get(ipAddress);
        DestinationCardDeck shortDestinationCardDeck = game.getDecksOfGame().getShortDestinationCardDeck();

        List<DestinationCard> playerShortDestinationCards = player.getShortDestinationCards();
        for (int i = 0; i < 3; i++) {
            DestinationCard shortDestinationCard = RandomGenerator.randomFromArray(shortDestinationCardDeck.getCardDeck().toArray(new DestinationCard[0]));
            shortDestinationCardDeck.getCardDeck().remove(shortDestinationCard);
            playerShortDestinationCards.add(shortDestinationCard);
        }
        player.setShortDestinationCards(playerShortDestinationCards);
    }
}
