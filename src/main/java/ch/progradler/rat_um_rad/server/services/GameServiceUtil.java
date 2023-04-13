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

    static Player createNewPlayer(String ipAddress, IUserRepository userRepository, Set<WheelColor> takenColors) {
        String name = userRepository.getUsername(ipAddress);
        Set<WheelColor> allColors = new HashSet<>(Arrays.asList(WheelColor.values()));
        for (WheelColor color: takenColors) {
            allColors.remove(color);
        }
        WheelColor[] availableColors = allColors.toArray(new WheelColor[0]);
        WheelColor color = RandomGenerator.randomFromArray(availableColors);
        while (takenColors.contains(color)) {
            RandomGenerator.randomFromArray(WheelColor.values());
        }
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

    public static void notifyPlayersOfGameUpdate(String gameId, IGameRepository gameRepository,  OutputPacketGateway outputPacketGateway, Command command) {
        Game game = gameRepository.getGame(gameId);
        Set<String> playerIps = game.getPlayers().keySet();
        for (String ipAddress: playerIps) {
            ClientGame clientGame = GameServiceUtil.toClientGame(game, ipAddress);
            outputPacketGateway.sendPacket(ipAddress, new Packet(command, clientGame, ContentType.GAME));
        }
    }

    public static void startGame(String gameId, IGameRepository gameRepository, OutputPacketGateway outputPacketGateway) {
        Game game = gameRepository.getGame(gameId);
        game.setStatus(PREPARATION);
        for (String ipAddress: game.getPlayers().keySet()) {
            GameServiceUtil.handOutLongDestinationCard(gameId, ipAddress, gameRepository);
            GameServiceUtil.handOutShortDestinationCards(gameId, ipAddress, gameRepository);
            game.getPlayersHaveChosenShortDestinationCards().put(ipAddress, false);
        }
        gameRepository.updateGame(game);
        for (String ipAddress: game.getPlayers().keySet()) {
            ClientGame clientGame = GameServiceUtil.toClientGame(game, ipAddress);
            outputPacketGateway.sendPacket(ipAddress, new Packet(GAME_STARTED_SELECT_DESTINATION_CARDS, clientGame, GAME));
        }
    }

    public static void handOutLongDestinationCard(String gameId, String ipAddress, IGameRepository gameRepository) {
        Game game = gameRepository.getGame(gameId);
        Player player = game.getPlayers().get(ipAddress);
        DestinationCardDeck longDestinationCardDeck = game.getDecksOfGame().getLongDestinationCardDeck();
        DestinationCard longDestinationCard = RandomGenerator.randomFromArray(longDestinationCardDeck.getCardDeck().toArray( new DestinationCard[0]));
        longDestinationCardDeck.getCardDeck().remove(longDestinationCard);
        player.setLongDestinationCard(longDestinationCard);
        gameRepository.updateGame(game);
    }

    public static void handOutShortDestinationCards(String gameId, String ipAddress, IGameRepository gameRepository) {
        Game game = gameRepository.getGame(gameId);
        Player player = game.getPlayers().get(ipAddress);
        DestinationCardDeck shortDestinationCardDeck = game.getDecksOfGame().getShortDestinationCardDeck();

        List<DestinationCard> playerShortDestinationCards = player.getShortDestinationCards();
        for (int i = 0; i < 3; i++ ) {
            DestinationCard shortDestinationCard = RandomGenerator.randomFromArray(shortDestinationCardDeck.getCardDeck().toArray( new DestinationCard[0]));
            shortDestinationCardDeck.getCardDeck().remove(shortDestinationCard);
            playerShortDestinationCards.add(shortDestinationCard);
        }
        player.setShortDestinationCards(playerShortDestinationCards);
        gameRepository.updateGame(game);
    }
}
