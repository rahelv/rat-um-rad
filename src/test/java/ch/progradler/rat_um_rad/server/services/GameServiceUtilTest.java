package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.Activity;
import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.*;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.*;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.util.GameConfig;
import ch.progradler.rat_um_rad.shared.util.RandomGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.PREPARATION;
import static ch.progradler.rat_um_rad.shared.models.game.PlayerColor.*;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.GAME;
import static ch.progradler.rat_um_rad.shared.protocol.ServerCommand.*;
import static ch.progradler.rat_um_rad.shared.util.GameConfig.SHORT_DEST_CARDS_AT_START_COUNT;
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

        Player creator = new Player("player A", PlayerColor.LILA, 100, 10, 2);
        Player otherPlayer = new Player("player B", PlayerColor.LIGHT_BROWN, 50, 15, 1);
        String otherPlayerIp = "clientB";
        Map<String, Player> players = Map.of(
                creatorIp, creator,
                otherPlayerIp, otherPlayer
        );
        Map<String, String> roadsBuilt = Map.of("road1", "playerA", "road4", "playerB");
        List<Activity> activities = new ArrayList<>();

        Game game = new Game(gameId, status, map, createdAt, creatorIp, requiredPlayerCount, players, turn, roadsBuilt, activities);
        String forPlayerIp = otherPlayerIp;

        ClientGame expected = new ClientGame(gameId,
                status,
                map,
                createdAt,
                creatorIp,
                requiredPlayerCount,
                Collections.singletonList(GameServiceUtil.toVisiblePlayer(creator, creatorIp)),
                otherPlayer,
                turn,
                roadsBuilt,
                activities);

        assertEquals(expected, GameServiceUtil.toClientGame(game, forPlayerIp));
    }

    @Test
    void createNewPlayer() {
        String ipAddress = "clientA";
        String name = "John";
        List<PlayerColor> takenColors = Arrays.asList(PINK, LILA, LIGHT_BROWN);

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
        assertTrue(player.getShortDestinationCardsToChooseFrom().isEmpty());
        assertTrue(player.getWheelCards().isEmpty());
    }

    @Test
    void toVisiblePlayer() {
        String ipAddress = "playerA";
        PlayerEndResult endResult = new PlayerEndResult(
                new ArrayList<>(),
                new ArrayList<>(),
                true
        );
        Player player = new Player("John", PlayerColor.LILA, 30, 20, 3,
                Collections.singletonList(new WheelCard(3)), null, new ArrayList<>(), new ArrayList<>(), endResult);

        VisiblePlayer expected = new VisiblePlayer(player.getName(), player.getColor(),
                player.getScore(), player.getWheelsRemaining(), player.getPlayingOrder(), ipAddress,
                player.getWheelCards().size(),
                player.getShortDestinationCards().size(),
                player.getEndResult()
        );

        assertEquals(expected, GameServiceUtil.toVisiblePlayer(player, ipAddress));
    }

    @Test
    void getGameOfPlayer() {
        String gameId = "gameA";
        String playerIpAddress = "clientA";

        Player playerA = new Player("player A", LIGHT_BROWN, 100, 10, 2);
        Player playerB = new Player("player B", LIGHT_BLUE, 50, 15, 1);
        Map<String, Player> players1 = Map.of(
                playerIpAddress, playerA,
                "clientB", playerB
        );

        Map<String, Player> players2 = Map.of(
                "clientC", new Player("player C", LIGHT_BROWN, 100, 10, 2),
                "clientD", new Player("player D", LIGHT_GREEN, 100, 10, 2)
        );

        Map<String, String> roadsBuilt = Map.of("road1", "playerA", "road4", "playerB");
        List<Activity> activities = new ArrayList<>();

        Game game1 = new Game(gameId, null, GameMap.defaultMap(), null, "playerB", 4, players1, 0, roadsBuilt, activities);
        Game game2 = new Game(gameId, null, GameMap.defaultMap(), null, "playerD", 4, players2, 3, roadsBuilt, activities);

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
        List<PlayerColor> takenColors = Collections.singletonList(player1.getColor());
        Player player2 = GameServiceUtil.createNewPlayer(ip2, mockUserRepository, takenColors);
        Map<String, Player> playerMap = new HashMap<>();
        playerMap.put(ip1, player1);
        playerMap.put(ip2, player2);

        Game game = new Game("gameId", GameStatus.STARTED, GameMap.defaultMap(), "creator", 3, playerMap);

        GameServiceUtil.notifyPlayersOfGameUpdate(game, mockOutputPacketGateway, NEW_PLAYER);
        verify(mockOutputPacketGateway).sendPacket(ip1, new Packet.Server(NEW_PLAYER, GameServiceUtil.toClientGame(game, ip1), ContentType.GAME));
        verify(mockOutputPacketGateway).sendPacket(ip2, new Packet.Server(NEW_PLAYER, GameServiceUtil.toClientGame(game, ip2), ContentType.GAME));
    }

    @Test
    void notifyPlayersOfGameActionTest() {
        String actorIp = "actor";
        String ip2 = "ip2";
        String ip3 = "ip3";

        when(mockUserRepository.getUsername(actorIp)).thenReturn("actor");
        when(mockUserRepository.getUsername(ip2)).thenReturn("name2");
        when(mockUserRepository.getUsername(ip3)).thenReturn("name3");
        Player actor = GameServiceUtil.createNewPlayer(actorIp, mockUserRepository, new ArrayList<>());
        Player player2 = GameServiceUtil.createNewPlayer(ip2, mockUserRepository, new ArrayList<>());
        Player player3 = GameServiceUtil.createNewPlayer(ip3, mockUserRepository, new ArrayList<>());

        Map<String, Player> playerMap = new HashMap<>();
        playerMap.put(actorIp, actor);
        playerMap.put(ip2, player2);
        playerMap.put(ip3, player3);

        Game game = new Game("gameId", GameStatus.STARTED, GameMap.defaultMap(), "creator", 3, playerMap);

        ServerCommand actionCommand = ROAD_BUILT;

        GameServiceUtil.notifyPlayersOfGameAction(actorIp, game, mockOutputPacketGateway, actionCommand);

        verify(mockOutputPacketGateway).sendPacket(actorIp, new Packet.Server(actionCommand, GameServiceUtil.toClientGame(game, actorIp), ContentType.GAME));
        verify(mockOutputPacketGateway).sendPacket(ip2, new Packet.Server(GAME_UPDATED, GameServiceUtil.toClientGame(game, ip2), ContentType.GAME));
        verify(mockOutputPacketGateway).sendPacket(ip3, new Packet.Server(GAME_UPDATED, GameServiceUtil.toClientGame(game, ip3), ContentType.GAME));
    }

    @Test
    void prepareGameTest() {
        String ip1 = "ip1";
        String ip2 = "ip2";
        String name1 = "name1";
        String name2 = "name2";
        when(mockUserRepository.getUsername(ip1)).thenReturn(name1);
        when(mockUserRepository.getUsername(ip2)).thenReturn(name2);

        Player player1 = GameServiceUtil.createNewPlayer(ip1, mockUserRepository, new ArrayList<>());
        List<PlayerColor> takenColors = Collections.singletonList(player1.getColor());
        Player player2 = GameServiceUtil.createNewPlayer(ip2, mockUserRepository, takenColors);

        Map<String, Player> playerMap = new HashMap<>();
        playerMap.put(ip1, player1);
        playerMap.put(ip2, player2);

        Game game = new Game("gameId", GameStatus.STARTED, GameMap.defaultMap(), "creator", 3, playerMap);

        // act
        GameServiceUtil.prepareGame(game, mockGameRepository, mockOutputPacketGateway);

        // assert
        assertEquals(PREPARATION, game.getStatus());

        // check long dest cards handed out:
        DestinationCard long1 = player1.getLongDestinationCard();
        DestinationCard long2 = player2.getLongDestinationCard();
        assertNotNull(long1);
        assertNotNull(long2);
        assertNotEquals(long1, long2);

        // check short dest cards too choose handed out:
        List<DestinationCard> shorts1 = player1.getShortDestinationCardsToChooseFrom();
        List<DestinationCard> shorts2 = player2.getShortDestinationCardsToChooseFrom();
        assertEquals(SHORT_DEST_CARDS_AT_START_COUNT, shorts1.size());
        assertEquals(SHORT_DEST_CARDS_AT_START_COUNT, shorts2.size());
        for (DestinationCard shortPlayer1 : shorts1) {
            assertFalse(shorts2.contains(shortPlayer1));
        }

        // check order was assigned:
        int order1 = player1.getPlayingOrder();
        int order2 = player2.getPlayingOrder();
        assertTrue(order1 <= 1 && order1 >= 0);
        assertTrue(order2 <= 1 && order2 >= 0);
        assertNotEquals(order1, order2);

        // check that certain amount of wheel cards distributed
        assertEquals(GameConfig.START_WHEEL_CARD_HANDOUT_COUNT, player1.getWheelCards().size());
        assertEquals(GameConfig.START_WHEEL_CARD_HANDOUT_COUNT, player2.getWheelCards().size());

        assertEquals(GameConfig.TOTAL_WHEEL_CARD_COUNT - 2 * GameConfig.START_WHEEL_CARD_HANDOUT_COUNT,
                game.getDecksOfGame().getWheelCardDeck().getDeckOfCards().size());

        verify(mockGameRepository, times(1)).updateGame(game);

        verify(mockOutputPacketGateway).sendPacket(ip1, new Packet.Server(GAME_STARTED_SELECT_DESTINATION_CARDS, GameServiceUtil.toClientGame(game, ip1), GAME));
        verify(mockOutputPacketGateway).sendPacket(ip2, new Packet.Server(GAME_STARTED_SELECT_DESTINATION_CARDS, GameServiceUtil.toClientGame(game, ip2), GAME));
    }

    @Test
    void prepareGameShufflesDecks() {
        Game game = mock(Game.class);
        doNothing().when(game).setStatus(PREPARATION);
        List<WheelCard> wheelCards = Collections.singletonList(new WheelCard(0));
        List<DestinationCard> longDestinationCards = Collections.singletonList(mock(DestinationCard.class));
        List<DestinationCard> shortDestinationCards = Collections.singletonList(mock(DestinationCard.class));

        DecksOfGame decksOfGame = new DecksOfGame(
                new WheelCardDeck(wheelCards),
                WheelCardDeck.empty(),
                new DestinationCardDeck(longDestinationCards),
                new DestinationCardDeck(shortDestinationCards)
        );
        when(game.getDecksOfGame()).thenReturn(decksOfGame);

        try (MockedStatic<RandomGenerator> utilities = Mockito.mockStatic(RandomGenerator.class)) {
            utilities.when(() -> RandomGenerator.shuffle(any())).then(invocation -> null);

            GameServiceUtil.prepareGame(game, mockGameRepository, mockOutputPacketGateway);

            utilities.verify(() -> RandomGenerator.shuffle(wheelCards));
            utilities.verify(() -> RandomGenerator.shuffle(longDestinationCards));
            utilities.verify(() -> RandomGenerator.shuffle(shortDestinationCards));
        }
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
        List<PlayerColor> takenColors = Collections.singletonList(player1.getColor());
        Player player2 = GameServiceUtil.createNewPlayer(ip2, mockUserRepository, takenColors);
        Map<String, Player> playerMap = new HashMap<>();
        playerMap.put(ip1, player1);
        playerMap.put(ip2, player2);

        Game game = new Game("gameId", GameStatus.STARTED, GameMap.defaultMap(), "creator", 3, playerMap);

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
        Player player1 = GameServiceUtil.createNewPlayer("clientA", mockUserRepository, new ArrayList<>());
        Player player2 = GameServiceUtil.createNewPlayer("clientB", mockUserRepository, new ArrayList<>());

        List<DestinationCard> availableCards = DestinationCardDeck.shortDestinations(GameMap.defaultMap()).getCardDeck();

        // act
        int sizOfAvailableBeforeCalling = availableCards.size();
        GameServiceUtil.handOutShortDestinationCardsTooChoose(player1, availableCards);
        GameServiceUtil.handOutShortDestinationCardsTooChoose(player2, availableCards);

        //assert
        List<DestinationCard> destinationCards1 = player1.getShortDestinationCardsToChooseFrom();
        List<DestinationCard> destinationCards2 = player2.getShortDestinationCardsToChooseFrom();

        assertEquals(sizOfAvailableBeforeCalling - SHORT_DEST_CARDS_AT_START_COUNT * 2, availableCards.size());
        assertEquals(SHORT_DEST_CARDS_AT_START_COUNT, destinationCards1.size());
        assertEquals(SHORT_DEST_CARDS_AT_START_COUNT, destinationCards2.size());
        for (DestinationCard player1Card : destinationCards1) {
            assertFalse(destinationCards2.contains(player1Card));
        }
    }

    @Test
    void isPlayersTurnChecksByModuloOperation() {
        Game game = mock(Game.class);
        String player1 = "player1";
        String player2 = "player2";
        String player3 = "player3";
        String player4 = "player4";

        when(game.getPlayers()).thenReturn(Map.of(
                player1, new Player("Player1", null, 1, 1, 0),
                player2, new Player("Player2", null, 1, 1, 1),
                player3, new Player("Player3", null, 1, 1, 2),
                player4, new Player("Player4", null, 1, 1, 3)
        ));

        when(game.getTurn()).thenReturn(0);
        assertTrue(GameServiceUtil.isPlayersTurn(game, player1));
        assertFalse(GameServiceUtil.isPlayersTurn(game, player2));
        assertFalse(GameServiceUtil.isPlayersTurn(game, player3));
        assertFalse(GameServiceUtil.isPlayersTurn(game, player4));

        when(game.getTurn()).thenReturn(1);
        assertFalse(GameServiceUtil.isPlayersTurn(game, player1));
        assertTrue(GameServiceUtil.isPlayersTurn(game, player2));
        assertFalse(GameServiceUtil.isPlayersTurn(game, player3));
        assertFalse(GameServiceUtil.isPlayersTurn(game, player4));

        when(game.getTurn()).thenReturn(3);
        assertFalse(GameServiceUtil.isPlayersTurn(game, player1));
        assertFalse(GameServiceUtil.isPlayersTurn(game, player2));
        assertFalse(GameServiceUtil.isPlayersTurn(game, player3));
        assertTrue(GameServiceUtil.isPlayersTurn(game, player4));


        when(game.getTurn()).thenReturn(5);
        assertFalse(GameServiceUtil.isPlayersTurn(game, player1));
        assertTrue(GameServiceUtil.isPlayersTurn(game, player2));
        assertFalse(GameServiceUtil.isPlayersTurn(game, player3));
        assertFalse(GameServiceUtil.isPlayersTurn(game, player4));
    }
}