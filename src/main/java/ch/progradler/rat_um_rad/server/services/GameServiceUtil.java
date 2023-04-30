package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.PlayerColor;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.*;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.util.GameConfig;
import ch.progradler.rat_um_rad.shared.util.RandomGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.IntStream;

import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.PREPARATION;
import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.STARTED;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.STRING;
import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.*;
import static ch.progradler.rat_um_rad.shared.protocol.ServerCommand.*;
import static ch.progradler.rat_um_rad.shared.util.GameConfig.SHORT_DEST_CARDS_AT_START_COUNT;

/**
 * Util class for {@link GameService} with complex methods that are used multiple times.
 * No methods are <code>public</code>.
 */
public class GameServiceUtil {
    public static final Logger LOGGER = LogManager.getLogger();

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
                game.getRoadsBuilt(),
                game.getActivities());
    }

    static Player createNewPlayer(String ipAddress, IUserRepository userRepository, List<PlayerColor> takenColors) {
        String name = userRepository.getUsername(ipAddress);
        List<PlayerColor> availableColors = new ArrayList<>(Arrays.stream(PlayerColor.values()).toList());
        availableColors.removeAll(takenColors);

        PlayerColor color = RandomGenerator.randomFromList(availableColors);
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
                player.getShortDestinationCards().size(),
                player.getEndResult()
        );
    }

    public static Game getCurrentGameOfPlayer(String playerIpAddress, IGameRepository mockGameRepository) {
        List<Game> allGames = mockGameRepository.getAllGames();
        for (Game game : allGames) {
            if (game.getPlayers().containsKey(playerIpAddress)) return game;
        }
        return null;
    }

    public static void notifyPlayersOfGameUpdate(Game game, OutputPacketGateway outputPacketGateway, ServerCommand command) {
        LOGGER.info("Notifying players of game update for game " + game.getId() + "and command " + command);
        Set<String> playerIps = game.getPlayerIpAddresses();
        for (String ipAddress : playerIps) {
            ClientGame clientGame = toClientGame(game, ipAddress);
            outputPacketGateway.sendPacket(ipAddress, new Packet.Server(command, clientGame, ContentType.GAME));
        }
    }

    /**
     * Sends a packet to all players accept the actor with the game update
     * and sends a packet with the command of the action to the actor.
     *
     * @param actorIp ip address of actor.
     * @param game    the game
     */
    public static void notifyPlayersOfGameAction(String actorIp, Game game, OutputPacketGateway outputPacketGateway, ServerCommand actionCommand) {
        LOGGER.info("Notifying players of game action by " + actorIp +
                " for game " + game.getId() + "and actionCommand " + actionCommand);
        for (String ipAddress : game.getPlayerIpAddresses()) {
            if (ipAddress.equals(actorIp)) continue;
            ClientGame clientGame = toClientGame(game, ipAddress);
            outputPacketGateway.sendPacket(ipAddress, new Packet.Server(GAME_UPDATED, clientGame, ContentType.GAME));
        }

        ClientGame actorClientGame = toClientGame(game, actorIp);
        outputPacketGateway.sendPacket(actorIp, new Packet.Server(actionCommand, actorClientGame, ContentType.GAME));
    }

    public static void prepareGame(Game game, IGameRepository gameRepository, OutputPacketGateway outputPacketGateway) {
        LOGGER.info("Preparing game " + game.getId());

        game.setStatus(PREPARATION);

        shuffleDecks(game);

        Map<String, Player> players = game.getPlayers();
        List<String> playerIpAddresses = game.getPlayerIpAddresses().stream().toList();
        int playerCount = playerIpAddresses.size();

        List<Integer> playingOrders = generateShuffledPlayingOrders(playerCount);
        List<WheelCard> allWheelCards = game.getDecksOfGame().getWheelCardDeck().getDeckOfCards();

        // the randomly picked cards are removed from this list after each handout availableShortDestCards
        // this is to make sure no players get the same destination cards as options.
        // that's why it is important that this list is a new list, so the actual deck is not altered.
        // these are shuffled because the decks were shuffled before
        List<DestinationCard> availableShortDestCards = new ArrayList<>(game.getDecksOfGame().getShortDestinationCardDeck().getCardDeck());

        for (int i = 0; i < playerCount; i++) {
            String ipAddress = playerIpAddresses.get(i);
            Player player = players.get(ipAddress);

            handOutLongDestinationCard(game, ipAddress);
            handOutShortDestinationCardsTooChoose(player, availableShortDestCards);
            game.getPlayersHaveChosenShortDestinationCards().put(ipAddress, false);

            player.setPlayingOrder(playingOrders.get(i));

            distributeStartingWheelCards(allWheelCards, player);
        }
        gameRepository.updateGame(game);
        notifyPlayersOfGameUpdate(game, outputPacketGateway, GAME_STARTED_SELECT_DESTINATION_CARDS);
    }

    private static void distributeStartingWheelCards(List<WheelCard> allWheelCards, Player player) {
        List<WheelCard> startingWheelCards = new ArrayList<>();
        for (int j = 0; j < GameConfig.START_WHEEL_CARD_HANDOUT_COUNT; j++) {
            startingWheelCards.add(allWheelCards.get(0)); // basically random, because was shuffled before
            allWheelCards.remove(0);
        }
        player.getWheelCards().addAll(startingWheelCards);
    }

    private static void shuffleDecks(Game game) {
        DecksOfGame decksOfGame = game.getDecksOfGame();
        RandomGenerator.shuffle(decksOfGame.getWheelCardDeck().getDeckOfCards());
        RandomGenerator.shuffle(decksOfGame.getLongDestinationCardDeck().getCardDeck());
        RandomGenerator.shuffle(decksOfGame.getShortDestinationCardDeck().getCardDeck());
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

    static void handOutShortDestinationCardsTooChoose(Player player, List<DestinationCard> availableCards) {
        List<DestinationCard> cardsToChooseFrom = new ArrayList<>();
        for (int i = 0; i < SHORT_DEST_CARDS_AT_START_COUNT; i++) {
            DestinationCard shortDestinationCard = RandomGenerator.randomFromArray(availableCards.toArray(new DestinationCard[0]));
            availableCards.remove(shortDestinationCard);
            cardsToChooseFrom.add(shortDestinationCard);
        }
        player.setShortDestinationCardsToChooseFrom(cardsToChooseFrom);
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

        if (!GameServiceUtil.isPlayersTurn(game, ipAddress)) { //TODO: uses this method when cards are selected but in the beginning it's nobodys turn!!!
            sendInvalidActionResponse(ipAddress, NOT_PLAYERS_TURN, outputPacketGateway);
            return false;
        }
        return true;
    }

    public static void sendInvalidActionResponse(String ipAddress, String errorMessage, OutputPacketGateway outputPacketGateway) {
        Packet.Server errorResponse = new Packet.Server(INVALID_ACTION_FATAL,
                errorMessage,
                STRING);
        outputPacketGateway.sendPacket(ipAddress, errorResponse);
    }
}
