package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.shared.models.game.*;
import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.*;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.ErrorResponse;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.util.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.*;
import static ch.progradler.rat_um_rad.shared.protocol.Command.INVALID_ACTION_FATAL;
import static ch.progradler.rat_um_rad.shared.protocol.Command.NEW_PLAYER;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.STRING;
import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {
    @Mock
    OutputPacketGateway mockOutputPacketGateway;
    @Mock
    IGameRepository mockGameRepository;
    @Mock
    IUserRepository mockUserRepository;

    GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(mockOutputPacketGateway, mockGameRepository, mockUserRepository);
    }

    @Test
    void createGameCreatesNewGameInstance() throws IGameRepository.DuplicateIdException {
        String creator = "clientA";
        int requiredPlayers = 4;

        // act
        gameService.createGame(creator, requiredPlayers);

        // assert
        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(mockGameRepository).addGame(gameCaptor.capture());

        Game game = gameCaptor.getValue();
        DecksOfGame decks = game.getDecksOfGame();

        boolean createdAtIsJustNow = ((new Date()).getTime() -
                game.getCreatedAt().getTime()) <= 100; // created at less than 50 milliseconds ago;

        assertTrue(createdAtIsJustNow);
        assertTrue(game.getId().length() >= 10);
        assertEquals(creator, game.getCreatorPlayerIpAddress());
        assertEquals(requiredPlayers, game.getRequiredPlayerCount());
        assertSame(game.getStatus(), GameStatus.WAITING_FOR_PLAYERS);
        assertTrue(decks.getDiscardDeck().getDeckOfCards().isEmpty());
        assertArrayEquals(
                decks.getWheelCardDeck().getDeckOfCards().toArray(),
                WheelCardDeck.full().getDeckOfCards().toArray());
        assertArrayEquals(
                decks.getLongDestinationCardDeck().getCardDeck().toArray(),
                DestinationCardDeck.longDestinations().getCardDeck().toArray());
        assertArrayEquals(
                decks.getShortDestinationCardDeck().getCardDeck().toArray(),
                DestinationCardDeck.shortDestinations().getCardDeck().toArray());
    }

    @Test
    void createGameRetriesWithOtherIdUntilNoErrorThrown() throws IGameRepository.DuplicateIdException {
        String creator = "clientA";
        int requiredPlayers = 4;

        // throw, throw, do not throw
        boolean[] repoShouldThrow = new boolean[]{true, true, false};
        final int[] mockIndex = {0};

        doThrow(IGameRepository.DuplicateIdException.class)
                .when(mockGameRepository).addGame(argThat((g) -> {
            mockIndex[0]++;
            return repoShouldThrow[mockIndex[0] - 1];
        }));
        gameService.createGame(creator, requiredPlayers);

        verify(mockGameRepository, times(3)).addGame(isA(Game.class));
    }

    @Test
    void createGameAddsCreatorPlayer() throws IGameRepository.DuplicateIdException {
        String creatorIp = "clientA";
        String creatorName = "John";

        when(mockUserRepository.getUsername(creatorIp)).thenReturn(creatorName);

        gameService.createGame(creatorIp, 4);

        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(mockGameRepository).addGame(gameCaptor.capture());

        Game game = gameCaptor.getValue();
        assertEquals(1, game.getPlayers().size());
        Player creator = game.getPlayers().get(creatorIp);
        Player expected = GameServiceUtil.createNewPlayer(creatorIp, mockUserRepository, new LinkedList<>());
        expected.setColor(creator.getColor()); // color is randomly generated so has to be the same

        assertEquals(expected, creator);
    }

    @Test
    void createGameSendsGameInstanceToCreator() throws IGameRepository.DuplicateIdException {
        String creatorIp = "clientA";

        // act
        gameService.createGame(creatorIp, 4);

        // assert
        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(mockGameRepository).addGame(gameCaptor.capture());
        Game createdGame = gameCaptor.getValue();

        ClientGame sentGame = GameServiceUtil.toClientGame(createdGame, creatorIp);//new ClientGame(createdGame.getId())
        Packet packet = new Packet(Command.GAME_CREATED, sentGame, ContentType.GAME);
        verify(mockOutputPacketGateway).sendPacket(creatorIp, packet);
    }

    @Test
    void requestToJoinAlreadyStartedGameFails() {
        String gameId = "idA";
        String ipAddress = "ipAddressA";
        Game game = new Game(gameId, PREPARATION, null, null, 5, new HashMap<>());
        when(mockGameRepository.getGame(gameId)).thenReturn(game);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            gameService.joinGame(ipAddress, gameId);
            verify(mockOutputPacketGateway).sendPacket(ipAddress, new Packet(INVALID_ACTION_FATAL, ErrorResponse.JOINING_NOT_POSSIBLE, STRING));
            utilities.verify(() -> GameServiceUtil.notifyPlayersOfGameUpdate(game, mockOutputPacketGateway, NEW_PLAYER), never());
        }
    }

    @Test
    void requestToJoinWaitingGameWorksAndSendsUpdateMessageToAllPlayers() {
        String ipAddressJoiner = "ipAddressJoiner";

        String gameId = "idA";
        Game game = mock(Game.class);
        when(mockGameRepository.getGame(gameId)).thenReturn(game);
        Map<String, Player> playerMap = new HashMap<>();
        when(game.getPlayers()).thenReturn(playerMap);
        when(game.getStatus()).thenReturn(WAITING_FOR_PLAYERS);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            gameService.joinGame(ipAddressJoiner, gameId);
            verify(mockOutputPacketGateway, never()).sendPacket(ipAddressJoiner, new Packet(INVALID_ACTION_FATAL, ErrorResponse.JOINING_NOT_POSSIBLE, STRING));
            utilities.verify(() -> GameServiceUtil.notifyPlayersOfGameUpdate(game, mockOutputPacketGateway, NEW_PLAYER));
        }
    }

    @Test
    void joinWaitingGameAddsPlayerAndSavesGame() {
        String ipAddressJoiner = "ipAddressJoiner";
        String joinerName = "Joiner";
        when(mockUserRepository.getUsername(ipAddressJoiner)).thenReturn(joinerName);

        String ipAddressB = "ipAddressB";
        Player playerB = mock(Player.class);
        when(playerB.getColor()).thenReturn(WheelColor.BLACK);

        String gameId = "idA";
        Game game = mock(Game.class);
        when(mockGameRepository.getGame(gameId)).thenReturn(game);

        Map<String, Player> playerMap = new HashMap<>();
        playerMap.put(ipAddressB, playerB);
        when(game.getPlayers()).thenReturn(playerMap);
        when(game.getStatus()).thenReturn(WAITING_FOR_PLAYERS);

        // act
        gameService.joinGame(ipAddressJoiner, gameId);

        // assert
        Player joined = playerMap.get(ipAddressJoiner);
        Player expectedNewPlayer = new Player(joinerName, joined.getColor(), 0, GameConfig.STARTING_WHEELS_PER_PLAYER, 0);

        assertEquals(expectedNewPlayer, joined);
        verify(mockGameRepository).updateGame(game);
    }

    @Test
    void whenEnoughPlayersAreInTheGameMethodStartGameShouldBeCalled() {
        String ipAddressJoiner = "ipAddressJoiner";
        ClientGame clientGameForJoiner = mock(ClientGame.class);

        String ipAddressB = "ipAddressB";
        ClientGame clientGameB = mock(ClientGame.class);
        Player playerB = mock(Player.class);
        when(playerB.getColor()).thenReturn(WheelColor.BLACK);

        String gameId = "idA";
        int requiredPlayerCount = 2;

        Map<String, Player> playerMap = new HashMap<>();
        playerMap.put(ipAddressB, playerB);
        Game game = new Game(gameId, WAITING_FOR_PLAYERS, null, null, requiredPlayerCount, playerMap);

        when(mockGameRepository.getGame(gameId)).thenReturn(game);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.toClientGame(game, ipAddressJoiner))
                    .thenReturn(clientGameForJoiner);
            utilities.when(() -> GameServiceUtil.toClientGame(game, ipAddressB))
                    .thenReturn(clientGameB);

            gameService.joinGame(ipAddressJoiner, gameId);

            utilities.verify(() -> GameServiceUtil.startGame(game, mockGameRepository, mockOutputPacketGateway));
        }
    }

    @Test
    void selectShortDestinationCardSendsErrorPacketIfPlayerInNoGame() {
        String ipAddress = "clientA";

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository))
                    .thenReturn(null);

            gameService.selectShortDestinationCards(ipAddress, Collections.singletonList("card1"));

            verify(mockGameRepository, never()).updateGame(any());
            Packet errorResponse = new Packet(Command.SHORT_DESTINATION_CARDS_SELECTED_IN_PREPARATION,
                    ErrorResponse.PLAYER_IN_NO_GAME,
                    STRING);
            verify(mockOutputPacketGateway).sendPacket(ipAddress, errorResponse);
        }
    }

    @Test
    void selectShortDestinationCardSendsErrorPacketIfSelectedCardIdsAreNotAllInOptionalCards() {
        String ipAddress = "clientA";
        List<DestinationCard> allShortDestCards = DestinationCardDeck.shortDestinations().getCardDeck();

        List<DestinationCard> optionalCards = new ArrayList<>(Arrays.asList(
                allShortDestCards.get(5),
                allShortDestCards.get(2),
                allShortDestCards.get(6)));

        List<String> selectedCardIds = Arrays.asList(
                "otherCardId",
                allShortDestCards.get(6).getCardID());

        Map<String, Player> players = new HashMap<>();
        Player player = new Player("Player A", null, 0, 35, 2,
                new ArrayList<>(), null, optionalCards);
        players.put(ipAddress, player);

        Game game = mock(Game.class);
        when(game.getPlayers()).thenReturn(players);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository))
                    .thenReturn(game);

            gameService.selectShortDestinationCards(ipAddress, selectedCardIds);

            verify(mockGameRepository, never()).updateGame(any());
            Packet errorResponse = new Packet(Command.SHORT_DESTINATION_CARDS_SELECTED_IN_PREPARATION,
                    ErrorResponse.SELECTED_SHORT_DESTINATION_CARDS_INVALID,
                    STRING);
            verify(mockOutputPacketGateway).sendPacket(ipAddress, errorResponse);
        }
    }

    @Test
    void selectShortDestinationCardSendsErrorPacketIfSelectedCardIdsAreEmpty() {
        String ipAddress = "clientA";
        gameService.selectShortDestinationCards(ipAddress, new ArrayList<>());

        verify(mockGameRepository, never()).updateGame(any());
        Packet errorResponse = new Packet(Command.SHORT_DESTINATION_CARDS_SELECTED_IN_PREPARATION,
                ErrorResponse.SELECTED_SHORT_DESTINATION_CARDS_INVALID,
                STRING);
        verify(mockOutputPacketGateway).sendPacket(ipAddress, errorResponse);
    }

    @Test
    void selectShortDestinationCardSetsThemForThatPlayerAndRemovesFromDeckAndSetsSelectedAsTrueAndUpdatesGameIfStatusPrep() {
        String ipAddress = "clientA";
        List<DestinationCard> allShortDestCards = DestinationCardDeck.shortDestinations().getCardDeck();

        List<DestinationCard> optionalCards = new ArrayList<>(Arrays.asList(
                allShortDestCards.get(5),
                allShortDestCards.get(2),
                allShortDestCards.get(6)));

        List<String> selectedCardIds = Arrays.asList(
                allShortDestCards.get(2).getCardID(),
                allShortDestCards.get(6).getCardID());

        Map<String, Player> players = new HashMap<>();
        Player player = new Player("Player A", null, 0, 35, 2,
                new ArrayList<>(), null, optionalCards);
        players.put(ipAddress, player);

        Game game = new Game("game1", PREPARATION, GameMap.defaultMap(), "creator", 4, players);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository))
                    .thenReturn(game);

            gameService.selectShortDestinationCards(ipAddress, selectedCardIds);


            List<DestinationCard> selectedCards = Arrays.asList(
                    allShortDestCards.get(2),
                    allShortDestCards.get(6));

            assertEquals(selectedCards, player.getShortDestinationCards());

            List<DestinationCard> gameShortDestDeck = game.getDecksOfGame().getShortDestinationCardDeck().getCardDeck();
            for (DestinationCard selectedCard : selectedCards) {
                assertFalse(gameShortDestDeck.contains(selectedCard));
            }

            assertTrue(game.getPlayersHaveChosenShortDestinationCards().get(ipAddress));

            verify(mockGameRepository).updateGame(game);
        }
    }

    @Test
    void selectShortDestinationCardStartsGameRoundsIfAllPlayersHaveSelected() {
        String ipAddress = "clientA";
        List<DestinationCard> allShortDestCards = DestinationCardDeck.shortDestinations().getCardDeck();

        List<DestinationCard> optionalCards = new ArrayList<>(Arrays.asList(
                allShortDestCards.get(5),
                allShortDestCards.get(2),
                allShortDestCards.get(6)));

        List<String> selectedCardIds = Collections.singletonList(allShortDestCards.get(2).getCardID());

        Map<String, Player> players = new HashMap<>();
        Player player = new Player("Player A", null, 0, 35, 2,
                new ArrayList<>(), null, optionalCards);
        players.put(ipAddress, player);

        Game game = new Game("game1", PREPARATION, GameMap.defaultMap(), "creator", 4, players);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository))
                    .thenReturn(game);

            gameService.selectShortDestinationCards(ipAddress, selectedCardIds);

            utilities.verify(() -> GameServiceUtil.startGameRounds(game, mockGameRepository, mockOutputPacketGateway));
        }
    }

    @Test
    void selectShortDestinationCardSetsDoesNotStartGameRoundsIfNotAllPlayersHaveSelected() {
        String ipAddress = "clientA";
        List<DestinationCard> allShortDestCards = DestinationCardDeck.shortDestinations().getCardDeck();

        List<DestinationCard> optionalCards = new ArrayList<>(Arrays.asList(
                allShortDestCards.get(5),
                allShortDestCards.get(2),
                allShortDestCards.get(6)));

        List<String> selectedCardIds = Collections.singletonList(allShortDestCards.get(2).getCardID());

        Map<String, Player> players = new HashMap<>();
        Player player = new Player("Player A", null, 0, 35, 2,
                new ArrayList<>(), null, optionalCards);
        players.put(ipAddress, player);
        String playerBIp = "clientB";
        players.put(ipAddress, player);
        players.put(playerBIp, mock(Player.class));

        Game game = new Game("game1", PREPARATION, GameMap.defaultMap(), "creator", 4, players);
        game.getPlayersHaveChosenShortDestinationCards().put(playerBIp, false);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository))
                    .thenReturn(game);

            gameService.selectShortDestinationCards(ipAddress, selectedCardIds);

            utilities.verify(() -> GameServiceUtil.startGameRounds(game, mockGameRepository, mockOutputPacketGateway), never());
        }
    }

    @Test
    void handlesWaitingGamesRequest() {
        String ipAddress = "ipAddressA";
        when(mockGameRepository.getWaitingGames()).thenReturn(null);

        gameService.getWaitingGames(ipAddress);

        List<Game> waitingGames = verify(mockGameRepository).getWaitingGames();
        Packet packet = new Packet(Command.SEND_WAITING_GAMES, waitingGames, ContentType.GAME_INFO_LIST);
        verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
    }

    @Test
    void handlesStartedGamesRequest() {
        String ipAddress = "ipAddressA";
        when(mockGameRepository.getStartedGames()).thenReturn(null);

        gameService.getStartedGames(ipAddress);

        List<Game> startedGames = verify(mockGameRepository).getStartedGames();
        Packet packet = new Packet(Command.SEND_STARTED_GAMES, startedGames, ContentType.GAME_INFO_LIST);
        verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
    }

    @Test
    void handlesFinishedGamesRequest() {
        String ipAddress = "ipAddressA";
        when(mockGameRepository.getFinishedGames()).thenReturn(null);

        gameService.getFinishedGames(ipAddress);

        List<Game> finishedGames = verify(mockGameRepository).getFinishedGames();
        Packet packet = new Packet(Command.SEND_FINISHED_GAMES, finishedGames, ContentType.GAME_INFO_LIST);
        verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
    }


    @Test
    void buildRoadSendsErrorAndDoesNotChangeGameIfPlayerInNoGame() {
        String ipAddress = "clientA";


        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository))
                    .thenReturn(null);

            gameService.buildRoad(ipAddress, "someRoad");

            verifyGameNotUpdatedAndCorrectErrorSent(ipAddress, ErrorResponse.PLAYER_IN_NO_GAME);
        }
    }

    @Test
    void buildRoadSendsErrorAndDoesNotChangeGameIfGameDoesNotHaveStatusStarted() {
        String ipAddress = "clientA";
        Game game = mock(Game.class);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            for (GameStatus status : Arrays.asList(WAITING_FOR_PLAYERS, PREPARATION, FINISHED)) {
                when(game.getStatus()).thenReturn(status);
                utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository))
                        .thenReturn(game);

                gameService.buildRoad(ipAddress, "someRoad");

            }

            verify(mockGameRepository, never()).updateGame(any());
            Packet errorResponse = new Packet(Command.INVALID_ACTION_FATAL,
                    ErrorResponse.GAME_NOT_STARTED,
                    STRING);
            verify(mockOutputPacketGateway, times(3)).sendPacket(ipAddress, errorResponse);
        }
    }

    @Test
    void buildRoadSendsErrorAndDoesNotChangeGameIfIsNotPlayersTurn() {
        String ipAddress = "clientA";
        Game game = mock(Game.class);
        when(game.getStatus()).thenReturn(STARTED);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository)).thenReturn(game);
            utilities.when(() -> GameServiceUtil.isPlayersTurn(game, ipAddress)).thenReturn(false);

            gameService.buildRoad(ipAddress, "someRoad");

            verifyGameNotUpdatedAndCorrectErrorSent(ipAddress, ErrorResponse.NOT_PLAYERS_TURN);
        }
    }

    private void verifyGameNotUpdatedAndCorrectErrorSent(String ipAddress, String errorMessage) {
        verify(mockGameRepository, never()).updateGame(any());
        Packet errorResponse = new Packet(Command.INVALID_ACTION_FATAL,
                errorMessage,
                STRING);
        verify(mockOutputPacketGateway).sendPacket(ipAddress, errorResponse);
    }

    @Test
    void buildRoadSendsErrorAndDoesNotChangeGameIfAlreadyBuiltOn() {
        String roadId = "road1";
        String ipAddress = "clientA";
        String playerBIpAddress = "clientB";

        Game game = mock(Game.class);
        when(game.getRoadsBuilt()).thenReturn(Map.of(roadId, playerBIpAddress));
        when(game.getStatus()).thenReturn(STARTED);

        try (MockedStatic<GameServiceUtil> utilities = mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository)).thenReturn(game);
            utilities.when(() -> GameServiceUtil.isPlayersTurn(game, ipAddress)).thenReturn(true);

            gameService.buildRoad(ipAddress, roadId);

            verify(mockGameRepository, never()).updateGame(any());
            Packet packet = new Packet(Command.INVALID_ACTION_FATAL, ROAD_ALREADY_BUILT_ON, ContentType.STRING);
            verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
        }
    }

    @Test
    void buildRoadSendsErrorAndDoesNotChangeGameIfPlayerHasNotEnoughWheels() {
        String roadId = "road1";
        String ipAddress = "clientA";

        Player player = new Player("PlayerA", null, 0, 7, 0);

        Game game = mock(Game.class);
        when(game.getStatus()).thenReturn(STARTED);

        int requiredWheels = 8;
        Road toBuild = new Road(roadId, "city1", "city2", requiredWheels, WheelColor.RED);

        GameMap map = mock(GameMap.class);
        when(game.getMap()).thenReturn(map);
        when(map.getRoads()).thenReturn(Collections.singletonList(toBuild));
        when(game.getPlayers()).thenReturn(Map.of(ipAddress, player));

        try (MockedStatic<GameServiceUtil> utilities = mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository)).thenReturn(game);
            utilities.when(() -> GameServiceUtil.isPlayersTurn(game, ipAddress)).thenReturn(true);

            gameService.buildRoad(ipAddress, roadId);

            verify(mockGameRepository, never()).updateGame(any());
            Packet packet = new Packet(Command.INVALID_ACTION_FATAL, NOT_ENOUGH_WHEELS_TO_BUILD_ROAD, ContentType.STRING);
            verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
        }
    }

    @Test
    void buildRoadSendsErrorAndDoesNotChangeGameIfPlayerHasNotEnoughCardsOfCorrectColor() {
        String roadId = "road1";
        String ipAddress = "clientA";

        List<WheelCard> wheelCards = Arrays.asList(
                new WheelCard(WheelColor.RED.ordinal() * 10),
                new WheelCard(WheelColor.GREEN.ordinal() * 10),
                new WheelCard(WheelColor.BLACK.ordinal() * 10)
        );
        Player player = new Player("PlayerA", null, 0, 20, 0, wheelCards, null, new ArrayList<>());

        Game game = mock(Game.class);
        when(game.getStatus()).thenReturn(STARTED);

        int requiredWheels = 2;
        WheelColor color = WheelColor.RED;
        Road toBuild = new Road(roadId, "city1", "city2", requiredWheels, color);

        GameMap map = mock(GameMap.class);
        when(game.getMap()).thenReturn(map);
        when(map.getRoads()).thenReturn(Collections.singletonList(toBuild));
        when(game.getPlayers()).thenReturn(Map.of(ipAddress, player));

        try (MockedStatic<GameServiceUtil> utilities = mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository)).thenReturn(game);
            utilities.when(() -> GameServiceUtil.isPlayersTurn(game, ipAddress)).thenReturn(true);

            gameService.buildRoad(ipAddress, roadId);

            verify(mockGameRepository, never()).updateGame(any());
            Packet packet = new Packet(Command.INVALID_ACTION_FATAL, NOT_ENOUGH_CARDS_OF_REQUIRED_COLOR_TO_BUILD_ROAD, ContentType.STRING);
            verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
        }
    }

    @Test
    void buildRoadUpdatesGameCorrectlyAndSendsUpdate() {
        String roadId = "road1";
        String ipAddress = "clientA";

        List<WheelCard> wheelCards = new ArrayList<>(Arrays.asList(
                new WheelCard(WheelColor.RED.ordinal() * 10),
                new WheelCard(WheelColor.RED.ordinal() * 10 + 1),
                new WheelCard(WheelColor.RED.ordinal() * 10 + 2),
                new WheelCard(WheelColor.GREEN.ordinal() * 10),
                new WheelCard(WheelColor.BLACK.ordinal() * 10)
        ));
        int score = 10;
        int wheelsRemaining = 20;
        Player player = new Player("PlayerA", null, score, wheelsRemaining, 0, wheelCards, null, new ArrayList<>());

        int requiredWheels = 2;
        WheelColor color = WheelColor.RED;
        Road toBuild = new Road(roadId, "city1", "city2", requiredWheels, color);

        GameMap map = mock(GameMap.class);
        when(map.getRoads()).thenReturn(Collections.singletonList(toBuild));

        Map<String, String> roadsBuilt = new HashMap<>();

        Game game = new Game("game1", STARTED, map, new Date(),
                "creator", 3,
                Map.of(ipAddress, player), 4, roadsBuilt);


        try (MockedStatic<GameServiceUtil> utilities = mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository)).thenReturn(game);
            utilities.when(() -> GameServiceUtil.isPlayersTurn(game, ipAddress)).thenReturn(true);

            // act
            gameService.buildRoad(ipAddress, roadId);

            // assert
            // check wheel cards removed from player
            List<Integer> expectedTakenWheelCards = Arrays.asList(WheelColor.RED.ordinal() * 10,
                    WheelColor.RED.ordinal() * 10 + 1); // 2 reds
            List<Integer> playerRemainingWheelCardIds = player.getWheelCards().stream()
                    .map(WheelCard::getCardID).toList();
            assertFalse(playerRemainingWheelCardIds.contains(expectedTakenWheelCards.get(0)));
            assertFalse(playerRemainingWheelCardIds.contains(expectedTakenWheelCards.get(1)));

            // more assertions
            assertEquals(wheelsRemaining - requiredWheels, player.getWheelsRemaining());
            assertEquals(ipAddress, game.getRoadsBuilt().get(roadId));
            assertEquals(score + GameConfig.scoreForRoadBuild(requiredWheels), player.getScore());

            verify(mockGameRepository).updateGame(game);
            utilities.verify(() -> GameServiceUtil.notifyPlayersOfGameUpdate(game, mockOutputPacketGateway, Command.BUILD_ROAD));
        }
    }


    /*
    @Test
    void buildRoadSendsEventToAllPlayers() {
        String roadId = "road1";
        String ipAddress = "clientA";
        String playerBIpAddress = "clientB";

        Game game = mock(Game.class);
        Map<String, Player> players = Map.of(
                ipAddress, new Player("Player A", WheelColor.WHITE, 5, 10, 0),
                playerBIpAddress, new Player("Player B", WheelColor.GREEN, 5, 10, 0)
        );
        when(game.getPlayers()).thenReturn(players);

        // act
        gameService.buildRoad(ipAddress, roadId);

        // assert
        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(mockGameRepository).up(gameCaptor.capture());
        Game createdGame = gameCaptor.getValue();

        ClientGame sentGame = GameServiceUtil.toClientGame(createdGame, creatorIp);//new ClientGame(createdGame.getId())
        Packet packet = new Packet(Command.GAME_CREATED, sentGame, ContentType.GAME);
        verify(mockOutputPacketGateway).sendPacket(creatorIp, packet);
    }

     */
}