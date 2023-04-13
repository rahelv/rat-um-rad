package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.client.Client;
import ch.progradler.rat_um_rad.client.models.ClientGame;
import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.game.*;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DecksOfGame;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCardDeck;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCardDeck;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.ErrorResponse;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.PREPARATION;
import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.WAITING_FOR_PLAYERS;
import static ch.progradler.rat_um_rad.shared.protocol.Command.*;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.GAME;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.STRING;
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
        Player expected = GameServiceUtil.createNewPlayer(creatorIp, mockUserRepository, new HashSet<>());
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
        ClientGame clientGame = GameServiceUtil.toClientGame(game, game.getCreatorPlayerIpAddress());
        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            gameService.joinGame(ipAddress, gameId);
            verify(mockOutputPacketGateway).sendPacket(ipAddress, new Packet(INVALID_ACTION_FATAL, ErrorResponse.JOINING_NOT_POSSIBLE, STRING));
            verify(mockOutputPacketGateway, never()).sendPacket(ipAddress, new Packet(GAME_JOINED, clientGame, GAME));
            utilities.verify(() -> GameServiceUtil.notifyPlayersOfGameUpdate(gameId, mockGameRepository, mockOutputPacketGateway, NEW_PLAYER), never());
        }
    }

    @Test
    void requestToJoinWaitingGameWorks() {

        String ipAddressJoiner = "ipAddressJoiner";
        ClientGame clientGameForJoiner = mock(ClientGame.class);

        //String ipAddressB = "ipAddressB";
        //ClientGame clientGameB = mock(ClientGame.class);
        //Player playerB = mock(Player.class);
        //when(playerB.getColor()).thenReturn(WheelColor.BLACK);
        List<Player> players = new LinkedList<>();
        //players.add(playerB);
        //Set<String> ipAddresses = new HashSet<>();
        //ipAddresses.add(ipAddressB);

        String gameId = "idA";
        Game game = mock(Game.class);
        when(mockGameRepository.getGame(gameId)).thenReturn(game);
        Map<String, Player> playerMap = mock(HashMap.class);
        when(game.getPlayers()).thenReturn(playerMap);
        when(game.getStatus()).thenReturn(WAITING_FOR_PLAYERS);
        when(playerMap.values()).thenReturn(players);
        //when(playerMap.keySet()).thenReturn(ipAddresses);

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.toClientGame(game, ipAddressJoiner))
                    .thenReturn(clientGameForJoiner);
            //utilities.when(() -> GameServiceUtil.toClientGame(game, ipAddressB))
            //            .thenReturn(clientGameB);

            gameService.joinGame(ipAddressJoiner, gameId);
            verify(mockOutputPacketGateway, never()).sendPacket(ipAddressJoiner, new Packet(INVALID_ACTION_FATAL, ErrorResponse.JOINING_NOT_POSSIBLE, STRING));
            verify(mockOutputPacketGateway).sendPacket(ipAddressJoiner, new Packet(GAME_JOINED, clientGameForJoiner, GAME));
            utilities.verify(() -> GameServiceUtil.notifyPlayersOfGameUpdate(gameId, mockGameRepository, mockOutputPacketGateway, NEW_PLAYER), never());
        }
    }

    @Test
    void whenEnoughPlayersAreInTheGameMethodStartGameShouldBeCalled() {

        String ipAddressJoiner = "ipAddressJoiner";
        ClientGame clientGameForJoiner = mock(ClientGame.class);

        String ipAddressB = "ipAddressB";
        ClientGame clientGameB = mock(ClientGame.class);
        Player playerB = mock(Player.class);
        when(playerB.getColor()).thenReturn(WheelColor.BLACK);
        /*List<Player> players = new LinkedList<>();
        players.add(playerB);
        Set<String> ipAddresses = new HashSet<>();
        ipAddresses.add(ipAddressB);
        ipAddresses.add(ipAddressJoiner);*/

        String gameId = "idA";
        Game game = mock(Game.class);
        when(mockGameRepository.getGame(gameId)).thenReturn(game);
        Map<String, Player> playerMap = new HashMap<>();
        playerMap.put(ipAddressB, playerB);
        when(game.getPlayers()).thenReturn(playerMap);
        when(game.getStatus()).thenReturn(WAITING_FOR_PLAYERS);
        /*int actualPlayerCount = playerMap.keySet().size();
        when(playerMap.size()).thenReturn(actualPlayerCount);*/
        when(game.getRequiredPlayerCount()).thenReturn(2);

        //add these line because game.start() as a non mocked method is called

        try (MockedStatic<GameServiceUtil> utilities = Mockito.mockStatic(GameServiceUtil.class)) {
            utilities.when(() -> GameServiceUtil.toClientGame(game, ipAddressJoiner))
                    .thenReturn(clientGameForJoiner);
            utilities.when(() -> GameServiceUtil.toClientGame(game, ipAddressB))
                    .thenReturn(clientGameB);

            gameService.joinGame(ipAddressJoiner, gameId);

            //startGame() is called
            assertEquals(game.getRequiredPlayerCount(), playerMap.size());
        }
    }

    @Test
    void handlesWaitingGamesRequest() {
        String ipAddress = "ipAddressA";
        when(mockGameRepository.getWaitingGames()).thenReturn(null);

        gameService.getWaitingGames(ipAddress);

        List<Game> waitingGames = verify(mockGameRepository).getWaitingGames();
        Packet packet = new Packet(Command.SEND_GAMES, waitingGames, ContentType.GAME_INFO_LIST);
        verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
    }

    @Test
    void handlesStartedGamesRequest() {
        String ipAddress = "ipAddressA";
        when(mockGameRepository.getStartedGames()).thenReturn(null);

        gameService.getStartedGames(ipAddress);

        List<Game> startedGames = verify(mockGameRepository).getStartedGames();
        Packet packet = new Packet(Command.SEND_GAMES, startedGames, ContentType.GAME_INFO_LIST);
        verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
    }

    @Test
    void handlesFinishedGamesRequest() {
        String ipAddress = "ipAddressA";
        when(mockGameRepository.getFinishedGames()).thenReturn(null);

        gameService.getFinishedGames(ipAddress);

        List<Game> finishedGames = verify(mockGameRepository).getFinishedGames();
        Packet packet = new Packet(Command.SEND_GAMES, finishedGames, ContentType.GAME_INFO_LIST);
        verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
    }
}