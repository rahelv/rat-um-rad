package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.util.GameConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.PREPARATION;
import static ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor.*;
import static ch.progradler.rat_um_rad.shared.protocol.Command.GAME_STARTED_SELECT_DESTINATION_CARDS;
import static ch.progradler.rat_um_rad.shared.protocol.Command.NEW_PLAYER;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.GAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceUtilTest {

    @Mock
    IUserRepository mockUserRepository;

    @Mock
    IGameRepository mockGameRepository;

    @Mock
    OutputPacketGateway mockOutputPacketGateway;

    @Test
    void toClientGame() {
        String gameId = "gameA";
        GameStatus status = GameStatus.STARTED;
        GameMap map = GameMap.defaultMap();
        Date createdAt = new Date(2023, Calendar.JUNE, 3);
        String creatorIp = "clientCreator";
        int requiredPlayerCount = 5;
        int turn = 20;

        Player creator = new Player("player A", WheelColor.RED, 100, 10, 2);
        Player otherPlayer = new Player("player B", BLUE, 50, 15, 1);
        String otherPlayerIp = "clientB";
        Map<String, Player> players = Map.of(
                creatorIp, creator,
                otherPlayerIp, otherPlayer
        );

        Game game = new Game(gameId, status, map, createdAt, creatorIp, requiredPlayerCount, players, turn);
        String forPlayerIp = otherPlayerIp;

        ClientGame expected = new ClientGame(gameId,
                status,
                map,
                createdAt,
                creatorIp,
                requiredPlayerCount,
                Collections.singletonList(GameServiceUtil.toVisiblePlayer(creator, creatorIp)),
                otherPlayer,
                turn);

        assertEquals(expected, GameServiceUtil.toClientGame(game, forPlayerIp));
    }

    @Test
    void createNewPlayer() {
        String ipAddress = "clientA";
        String name = "John";
        List<WheelColor> takenColors = Arrays.asList(RED, BLUE, ORANGE, GREEN, PINK);

        when(mockUserRepository.getUsername(ipAddress)).thenReturn(name);

        Player player = GameServiceUtil.createNewPlayer(ipAddress, mockUserRepository, takenColors);
        assertEquals(name, player.getName());
        assertNotNull(player.getColor());
        assertFalse(takenColors.contains(player.getColor()));

        assertEquals(0, player.getScore());
        assertEquals(0, player.getPlayingOrder());
        assertEquals(GameConfig.STARTING_WHEELS_PER_PLAYER, player.getWheelsRemaining());
        assertNull(player.getLongDestinationCard());
        assertTrue(player.getShortDestinationCards().isEmpty());
        assertTrue(player.getWheelCards().isEmpty());
    }

    @Test
    void toVisiblePlayer() {
        String ipAddress = "playerA";
        Player player = new Player("John", WheelColor.WHITE, 30, 20, 3,
                Collections.singletonList(new WheelCard(3)), null, new ArrayList<>());

        VisiblePlayer expected = new VisiblePlayer(player.getName(), player.getColor(),
                player.getScore(), player.getWheelsRemaining(), player.getPlayingOrder(), ipAddress,
                player.getWheelCards().size(),
                player.getShortDestinationCards().size()
        );

        assertEquals(expected, GameServiceUtil.toVisiblePlayer(player, ipAddress));
    }

    @Test
    void getGameOfPlayer() {
        String gameId = "gameA";
        String playerIpAddress = "clientA";

        Player playerA = new Player("player A", WheelColor.RED, 100, 10, 2);
        Player playerB = new Player("player B", BLUE, 50, 15, 1);
        Map<String, Player> players1 = Map.of(
                playerIpAddress, playerA,
                "clientB", playerB
        );

        Map<String, Player> players2 = Map.of(
                "clientC", new Player("player C", WheelColor.RED, 100, 10, 2),
                "clientD", new Player("player D", WheelColor.RED, 100, 10, 2)
        );

        Game game1 = new Game(gameId, null, null, null, "playerB", 4, players1, 0);
        Game game2 = new Game(gameId, null, null, null, "playerD", 4, players2, 3);

        when(mockGameRepository.getAllGames()).thenReturn(Arrays.asList(game1, game2));

        Game result = GameServiceUtil.getCurrentGameOfPlayer(playerIpAddress, mockGameRepository);
        assertEquals(game1, result);
    }

    @Test
    void notifyPlayersOfGameUpdateTest() {
        String ip1 = "ip1";
        String ip2 = "ip2";
        String name1 = "name1";
        String name2 = "name2";
        when(mockUserRepository.getUsername(ip1)).thenReturn(name1);
        when(mockUserRepository.getUsername(ip2)).thenReturn(name2);
        Player player1 = GameServiceUtil.createNewPlayer(ip1, mockUserRepository, new ArrayList<>());
        List<WheelColor> takenColors = Collections.singletonList(player1.getColor());
        Player player2 = GameServiceUtil.createNewPlayer(ip2, mockUserRepository, takenColors);
        Map<String, Player> playerMap = new HashMap<>();
        playerMap.put(ip1, player1);
        playerMap.put(ip2, player2);

        GameMap mockGameMap = mock(GameMap.class);
        Game game = new Game("gameId", GameStatus.STARTED, mockGameMap, "creator", 3, playerMap);

        GameServiceUtil.notifyPlayersOfGameUpdate(game, mockOutputPacketGateway, NEW_PLAYER);
        verify(mockOutputPacketGateway).sendPacket(ip1, new Packet(NEW_PLAYER, GameServiceUtil.toClientGame(game, ip1), ContentType.GAME));
        verify(mockOutputPacketGateway).sendPacket(ip2, new Packet(NEW_PLAYER, GameServiceUtil.toClientGame(game, ip2), ContentType.GAME));
    }

    @Test
    void startGameTest() {
        String ip1 = "ip1";
        String ip2 = "ip2";
        String name1 = "name1";
        String name2 = "name2";
        when(mockUserRepository.getUsername(ip1)).thenReturn(name1);
        when(mockUserRepository.getUsername(ip2)).thenReturn(name2);

        Player player1 = GameServiceUtil.createNewPlayer(ip1, mockUserRepository, new ArrayList<>());
        List<WheelColor> takenColors = Collections.singletonList(player1.getColor());
        Player player2 = GameServiceUtil.createNewPlayer(ip2, mockUserRepository, takenColors);

        Map<String, Player> playerMap = new HashMap<>();
        playerMap.put(ip1, player1);
        playerMap.put(ip2, player2);

        GameMap mockGameMap = mock(GameMap.class);
        Game game = new Game("gameId", GameStatus.STARTED, mockGameMap, "creator", 3, playerMap);

        // act
        GameServiceUtil.startGame(game, mockGameRepository, mockOutputPacketGateway);

        // assert
        assertEquals(PREPARATION, game.getStatus());

        // check long dest cards handed out:
        DestinationCard long1 = player1.getLongDestinationCard();
        DestinationCard long2 = player2.getLongDestinationCard();
        assertNotNull(long1);
        assertNotNull(long2);
        assertNotEquals(long1, long2);

        // check short dest cards handed out:
        List<DestinationCard> shorts1 = player1.getShortDestinationCards();
        List<DestinationCard> shorts2 = player2.getShortDestinationCards();
        assertEquals(3, shorts1.size());
        assertEquals(3, shorts2.size());
        assertFalse(shorts1.contains(shorts2.get(0)));
        assertFalse(shorts1.contains(shorts2.get(1)));
        assertFalse(shorts1.contains(shorts2.get(2)));

        // check order was assigned:
        int order1 = player1.getPlayingOrder();
        int order2 = player2.getPlayingOrder();
        assertTrue(order1 <= 1 && order1 >= 0);
        assertTrue(order2 <= 1 && order2 >= 0);
        assertNotEquals(order1, order2);

        verify(mockGameRepository, times(1)).updateGame(game);

        verify(mockOutputPacketGateway).sendPacket(ip1, new Packet(GAME_STARTED_SELECT_DESTINATION_CARDS, GameServiceUtil.toClientGame(game, ip1), GAME));
        verify(mockOutputPacketGateway).sendPacket(ip2, new Packet(GAME_STARTED_SELECT_DESTINATION_CARDS, GameServiceUtil.toClientGame(game, ip2), GAME));
    }

    @Test
    void handOutLongDestinationCardsTest() {

        //preparation
        String ip1 = "ip1";
        String ip2 = "ip2";
        String name1 = "name1";
        String name2 = "name2";
        when(mockUserRepository.getUsername(ip1)).thenReturn(name1);
        when(mockUserRepository.getUsername(ip2)).thenReturn(name2);
        Player player1 = GameServiceUtil.createNewPlayer(ip1, mockUserRepository, new ArrayList<>());
        List<WheelColor> takenColors = Collections.singletonList(player1.getColor());
        Player player2 = GameServiceUtil.createNewPlayer(ip2, mockUserRepository, takenColors);
        Map<String, Player> playerMap = new HashMap<>();
        playerMap.put(ip1, player1);
        playerMap.put(ip2, player2);

        GameMap mockGameMap = mock(GameMap.class);
        Game game = new Game("gameId", GameStatus.STARTED, mockGameMap, "creator", 3, playerMap);

        //testing
        int sizeInGameBeforeCalling = game.getDecksOfGame().getLongDestinationCardDeck().getCardDeck().size();
        GameServiceUtil.handOutLongDestinationCard(game, ip1);
        GameServiceUtil.handOutLongDestinationCard(game, ip2);

        DestinationCard destinationCard1 = game.getPlayers().get(ip1).getLongDestinationCard();
        DestinationCard destinationCard2 = game.getPlayers().get(ip2).getLongDestinationCard();
        List<DestinationCard> longDestinationCards = game.getDecksOfGame().getLongDestinationCardDeck().getCardDeck();

        assertEquals(sizeInGameBeforeCalling - 2, longDestinationCards.size());
        assertNotNull(destinationCard1);
        assertNotNull(destinationCard2);
        assertFalse(longDestinationCards.contains(destinationCard1));
        assertFalse(longDestinationCards.contains(destinationCard2));
        assertNotEquals(destinationCard1, destinationCard2);
    }

    @Test
    void handoutShortDestinationCardsTest() {
        //preparation
        String ip1 = "ip1";
        String ip2 = "ip2";
        String name1 = "name1";
        String name2 = "name2";
        when(mockUserRepository.getUsername(ip1)).thenReturn(name1);
        when(mockUserRepository.getUsername(ip2)).thenReturn(name2);
        Player player1 = GameServiceUtil.createNewPlayer(ip1, mockUserRepository, new ArrayList<>());
        List<WheelColor> takenColors = Collections.singletonList(player1.getColor());

        Player player2 = GameServiceUtil.createNewPlayer(ip2, mockUserRepository, takenColors);
        Map<String, Player> playerMap = new HashMap<>();
        playerMap.put(ip1, player1);
        playerMap.put(ip2, player2);

        GameMap mockGameMap = mock(GameMap.class);
        Game game = new Game("gameId", GameStatus.STARTED, mockGameMap, "creator", 3, playerMap);

        //testing
        int sizeInGameBeforeCalling = game.getDecksOfGame().getShortDestinationCardDeck().getCardDeck().size();
        GameServiceUtil.handOutShortDestinationCards(game, ip1);
        GameServiceUtil.handOutShortDestinationCards(game, ip2);

        List<DestinationCard> destinationCards1 = game.getPlayers().get(ip1).getShortDestinationCards();
        List<DestinationCard> destinationCards2 = game.getPlayers().get(ip2).getShortDestinationCards();
        List<DestinationCard> longDestinationCards = game.getDecksOfGame().getShortDestinationCardDeck().getCardDeck();

        assertEquals(sizeInGameBeforeCalling - 6, longDestinationCards.size());
        assertEquals(3, destinationCards1.size());
        assertEquals(3, destinationCards2.size());
        for (DestinationCard card : destinationCards1) {
            assertFalse(longDestinationCards.contains(card));
        }
        for (DestinationCard card : destinationCards2) {
            assertFalse(longDestinationCards.contains(card));
        }
        assertNotEquals(destinationCards1, destinationCards2);
    }
}