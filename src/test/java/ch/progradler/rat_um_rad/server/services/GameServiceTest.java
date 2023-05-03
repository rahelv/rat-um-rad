package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IHighscoreRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.server.services.action_handlers.ActionHandlerFactory;
import ch.progradler.rat_um_rad.server.services.action_handlers.RoadActionHandler;
import ch.progradler.rat_um_rad.server.services.action_handlers.SelectDestinationCardsActionHandler;
import ch.progradler.rat_um_rad.server.services.action_handlers.TakeWheelCardsActionHandler;
import ch.progradler.rat_um_rad.server.validation.SelectDestinationCardsValidator;
import ch.progradler.rat_um_rad.shared.models.Highscore;
import ch.progradler.rat_um_rad.shared.models.game.*;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DecksOfGame;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCardDeck;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCardDeck;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.ErrorResponse;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
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
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.HIGHSCORE_LIST;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.STRING;
import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.PLAYER_IN_NO_GAME;
import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.SELECTED_SHORT_DESTINATION_CARDS_INVALID;
import static ch.progradler.rat_um_rad.shared.protocol.ServerCommand.*;
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
    @Mock
    IHighscoreRepository mockHighscoreRepository;
    @Mock
    ActionHandlerFactory mockActionHandlerFactory;
    @Mock
    SelectDestinationCardsValidator selectDestinationCardsValidator;

    GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(mockOutputPacketGateway,
                mockGameRepository,
                mockUserRepository,
                mockHighscoreRepository,
                mockActionHandlerFactory,
                selectDestinationCardsValidator);
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

        GameMap map = GameMap.defaultMap();
        assertArrayEquals(
                decks.getWheelCardDeck().getDeckOfCards().toArray(),
                WheelCardDeck.full().getDeckOfCards().toArray());
        assertArrayEquals(
                DestinationCardDeck.longDestinations(map).getCardDeck().toArray(),
                decks.getLongDestinationCardDeck().getCardDeck().toArray());
        assertArrayEquals(
                DestinationCardDeck.shortDestinations(map).getCardDeck().toArray(),
                decks.getShortDestinationCardDeck().getCardDeck().toArray());
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
        Packet.Server packet = new Packet.Server(ServerCommand.GAME_CREATED, sentGame, ContentType.GAME);
        verify(mockOutputPacketGateway).sendPacket(creatorIp, packet);
    }

    @Test
    void requestToJoinAlreadyStartedGameFails() {
        String gameId = "idA";
        String ipAddress = "ipAddressA";
        Game game = new Game(gameId, PREPARATION, GameMap.defaultMap(), null, 5, new HashMap<>());
        when(mockGameRepository.getGame(gameId)).thenReturn(game);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            gameService.joinGame(ipAddress, gameId);
            verify(mockOutputPacketGateway).sendPacket(ipAddress, new Packet.Server(INVALID_ACTION_FATAL, ErrorResponse.JOINING_NOT_POSSIBLE, STRING));
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
            verify(mockOutputPacketGateway, never()).sendPacket(ipAddressJoiner, new Packet.Server(INVALID_ACTION_FATAL, ErrorResponse.JOINING_NOT_POSSIBLE, STRING));
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
        when(playerB.getColor()).thenReturn(PlayerColor.LILA);

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
        when(playerB.getColor()).thenReturn(PlayerColor.PINK);

        String gameId = "idA";
        int requiredPlayerCount = 2;

        Map<String, Player> playerMap = new HashMap<>();
        playerMap.put(ipAddressB, playerB);
        Game game = new Game(gameId, WAITING_FOR_PLAYERS, GameMap.defaultMap(), null, requiredPlayerCount, playerMap);

        when(mockGameRepository.getGame(gameId)).thenReturn(game);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.toClientGame(game, ipAddressJoiner))
                    .thenReturn(clientGameForJoiner);
            utilities.when(() -> GameServiceUtil.toClientGame(game, ipAddressB))
                    .thenReturn(clientGameB);

            gameService.joinGame(ipAddressJoiner, gameId);

            utilities.verify(() -> GameServiceUtil.prepareGame(game, mockGameRepository, mockOutputPacketGateway));
        }
    }

    @Test
    void selectShortDestinationCardSendsErrorPacketIfPlayerInNoGame() {
        String ipAddress = "clientA";

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository))
                    .thenReturn(null);

            gameService.selectShortDestinationCards(ipAddress, Collections.singletonList("card1"));

            utilities.verify(() -> GameServiceUtil.sendInvalidActionResponse(ipAddress, PLAYER_IN_NO_GAME, mockOutputPacketGateway));
        }
    }

    @Test
    void selectShortDestinationCardSendsErrorPacketIfSelectedCardIdsAreNotAllInOptionalCards() {
        String ipAddress = "clientA";
        List<DestinationCard> allShortDestCards = DestinationCardDeck.shortDestinations(GameMap.defaultMap()).getCardDeck();

        List<DestinationCard> optionalCards = new ArrayList<>(Arrays.asList(
                allShortDestCards.get(5),
                allShortDestCards.get(2),
                allShortDestCards.get(6)));

        List<String> selectedCardIds = Arrays.asList(
                "otherCardId",
                allShortDestCards.get(6).getCardID());

        Map<String, Player> players = new HashMap<>();
        Player player = new Player("Player A", null, 0, 35, 2);
        player.setShortDestinationCardsToChooseFrom(optionalCards);
        players.put(ipAddress, player);

        Game game = mock(Game.class);
        when(game.getStatus()).thenReturn(PREPARATION);
        when(game.getPlayers()).thenReturn(players);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository))
                    .thenReturn(game);
            utilities.when(() -> GameServiceUtil.validateAndHandleActionPrecondition(
                    ipAddress, game, mockOutputPacketGateway)).thenReturn(true);

            gameService.selectShortDestinationCards(ipAddress, selectedCardIds);

            utilities.verify(() -> GameServiceUtil.sendInvalidActionResponse(ipAddress,
                    SELECTED_SHORT_DESTINATION_CARDS_INVALID, mockOutputPacketGateway));
        }
    }

    @Test
    void selectShortDestinationCardForPrepSendsErrorPacketIfSelectedCardIdsAreEmpty() {
        String ipAddress = "clientA";
        Game game = mock(Game.class);
        when(game.getStatus()).thenReturn(PREPARATION);
        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository))
                    .thenReturn(game);

            gameService.selectShortDestinationCards(ipAddress, new ArrayList<>());

            utilities.verify(() -> GameServiceUtil.sendInvalidActionResponse(ipAddress,
                    SELECTED_SHORT_DESTINATION_CARDS_INVALID, mockOutputPacketGateway));
        }
    }

    @Test
    void selectShortDestinationUpdatedStateCorrectlyAndUpdatesGameIfStatusPrep() {
        String ipAddress = "clientA";
        List<DestinationCard> allShortDestCards = DestinationCardDeck.shortDestinations(GameMap.defaultMap()).getCardDeck();

        List<DestinationCard> optionalCards = new ArrayList<>(Arrays.asList(
                allShortDestCards.get(5),
                allShortDestCards.get(2),
                allShortDestCards.get(6)));

        List<String> selectedCardIds = Arrays.asList(
                allShortDestCards.get(2).getCardID(),
                allShortDestCards.get(6).getCardID());

        Map<String, Player> players = new HashMap<>();
        Player player = new Player("Player A", null, 0, 35, 2);
        player.setShortDestinationCardsToChooseFrom(optionalCards);
        players.put(ipAddress, player);

        when(selectDestinationCardsValidator.validate(player, selectedCardIds)).thenReturn(true);

        Game game = new Game("game1", PREPARATION, GameMap.defaultMap(), "creator", 4, players);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository))
                    .thenReturn(game);

            gameService.selectShortDestinationCards(ipAddress, selectedCardIds);

            GameServiceUtil.updateGameStateForShortDestCardsSelectionGeneral(selectedCardIds, game, player);
            assertTrue(game.getPlayersHaveChosenShortDestinationCards().get(ipAddress));
            verify(mockGameRepository).updateGame(game);
        }
    }

    @Test
    void selectShortDestinationCardSetsStatusToStartedAndNotifiesEveryoneIfAllPlayersHaveSelectedAndStatusWasPrep() {
        String ipAddress = "clientA";
        List<DestinationCard> allShortDestCards = DestinationCardDeck.shortDestinations(GameMap.defaultMap()).getCardDeck();

        List<DestinationCard> optionalCards = new ArrayList<>(Arrays.asList(
                allShortDestCards.get(5),
                allShortDestCards.get(2),
                allShortDestCards.get(6)));

        List<String> selectedCardIds = Collections.singletonList(allShortDestCards.get(2).getCardID());

        Map<String, Player> players = new HashMap<>();
        Player player = new Player("Player A", null, 0, 35, 2);
        player.setShortDestinationCardsToChooseFrom(optionalCards);
        players.put(ipAddress, player);

        when(selectDestinationCardsValidator.validate(player, selectedCardIds)).thenReturn(true);

        Game game = new Game("game1", PREPARATION, GameMap.defaultMap(), "creator", 4, players);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository))
                    .thenReturn(game);

            gameService.selectShortDestinationCards(ipAddress, selectedCardIds);

            assertEquals(STARTED, game.getStatus());
            utilities.verify(() -> GameServiceUtil.notifyPlayersOfGameAction(ipAddress, game,
                    mockOutputPacketGateway, DESTINATION_CARDS_SELECTED));
        }
    }

    @Test
    void selectShortDestinationCardSetsDoesNotSetStatusToStartedIfNotAllPlayersHaveSelected() {
        String ipAddress = "clientA";
        List<DestinationCard> allShortDestCards = DestinationCardDeck.shortDestinations(GameMap.defaultMap()).getCardDeck();

        List<DestinationCard> optionalCards = new ArrayList<>(Arrays.asList(
                allShortDestCards.get(5),
                allShortDestCards.get(2),
                allShortDestCards.get(6)));

        List<String> selectedCardIds = Collections.singletonList(allShortDestCards.get(2).getCardID());
        Map<String, Player> players = new HashMap<>();
        Player player = new Player("Player A", null, 0, 35, 2);
        player.setShortDestinationCardsToChooseFrom(optionalCards);
        players.put(ipAddress, player);
        String playerBIp = "clientB";
        players.put(ipAddress, player);
        players.put(playerBIp, mock(Player.class));

        when(selectDestinationCardsValidator.validate(player, selectedCardIds)).thenReturn(true);

        Game game = new Game("game1", PREPARATION, GameMap.defaultMap(), "creator", 4, players);
        game.getPlayersHaveChosenShortDestinationCards().put(playerBIp, false);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository))
                    .thenReturn(game);

            // act
            gameService.selectShortDestinationCards(ipAddress, selectedCardIds);

            // assert
            utilities.verify(() -> GameServiceUtil.notifyPlayersOfGameAction(ipAddress, game,
                    mockOutputPacketGateway, DESTINATION_CARDS_SELECTED));
            assertEquals(PREPARATION, game.getStatus());
        }
    }

    @Test
    void selectShortDestinationCardWithStatusStartedCallSelectDestinationCardsActionHandler() {
        String ipAddress = "clientA";
        SelectDestinationCardsActionHandler selectDestinationCardsActionHandler = mock(SelectDestinationCardsActionHandler.class);
        when(mockActionHandlerFactory.createSelectDestinationCardsActionHandler()).thenReturn(selectDestinationCardsActionHandler);

        Game game = mock(Game.class);
        when(game.getStatus()).thenReturn(GameStatus.STARTED);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.getCurrentGameOfPlayer(ipAddress, mockGameRepository))
                    .thenReturn(game);

            List<String> cardIds = Collections.singletonList("card1");
            gameService.selectShortDestinationCards(ipAddress, cardIds);

            verify(selectDestinationCardsActionHandler).handle(ipAddress, cardIds);
        }
    }

    @Test
    void handlesWaitingGamesRequest() {
        String ipAddress = "ipAddressA";
        when(mockGameRepository.getWaitingGames()).thenReturn(null);

        gameService.getWaitingGames(ipAddress);

        List<Game> waitingGames = verify(mockGameRepository).getWaitingGames();
        Packet.Server packet = new Packet.Server(ServerCommand.SEND_WAITING_GAMES, waitingGames, ContentType.GAME_INFO_LIST);
        verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
    }

    @Test
    void handlesStartedGamesRequest() {
        String ipAddress = "ipAddressA";
        when(mockGameRepository.getStartedGames()).thenReturn(null);

        gameService.getStartedGames(ipAddress);

        List<Game> startedGames = verify(mockGameRepository).getStartedGames();
        Packet.Server packet = new Packet.Server(ServerCommand.SEND_STARTED_GAMES, startedGames, ContentType.GAME_INFO_LIST);
        verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
    }

    @Test
    void handlesFinishedGamesRequest() {
        String ipAddress = "ipAddressA";
        when(mockGameRepository.getFinishedGames()).thenReturn(null);

        gameService.getFinishedGames(ipAddress);

        List<Game> finishedGames = verify(mockGameRepository).getFinishedGames();
        Packet.Server packet = new Packet.Server(ServerCommand.SEND_FINISHED_GAMES, finishedGames, ContentType.GAME_INFO_LIST);
        verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
    }

    @Test
    void requestHighscores() {
        String ipAddress = "ipAddressA";
        List<Highscore> highscores = Collections.singletonList(new Highscore("user1", 55, new Date()));

        when(mockHighscoreRepository.getHighscores()).thenReturn(highscores);

        gameService.requestHighscores(ipAddress);

        Packet.Server packet = new Packet.Server(SEND_HIGHSCORES, highscores, HIGHSCORE_LIST);
        verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
    }

    @Test
    void buildRoadWorks() {
        RoadActionHandler mockRoadActionHandler = mock(RoadActionHandler.class);
        when(mockActionHandlerFactory.createRoadActionHandler()).thenReturn(mockRoadActionHandler);
        String ipAddress = "ipAddress";
        String roadId = "roadId";
        gameService.buildRoad(ipAddress, roadId);
        verify(mockActionHandlerFactory).createRoadActionHandler();
        verify(mockRoadActionHandler).handle(ipAddress, roadId);
    }

    @Test
    void takeWheelCardsWorks() {
        TakeWheelCardsActionHandler mockTakeWheelCardsActionHandler = mock(TakeWheelCardsActionHandler.class);
        when(mockActionHandlerFactory.createTakeWheelCardsActionHandler()).thenReturn(mockTakeWheelCardsActionHandler);
        String ipAddress = "ipAddress";
        gameService.takeWheelCardFromDeck(ipAddress);
        verify(mockActionHandlerFactory).createTakeWheelCardsActionHandler();
        verify(mockTakeWheelCardsActionHandler).handle(ipAddress, "");
    }
}