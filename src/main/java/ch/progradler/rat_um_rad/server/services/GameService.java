package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Action;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IHighscoreRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.server.services.action_handlers.ActionHandlerFactory;
import ch.progradler.rat_um_rad.server.validation.ActionValidator;
import ch.progradler.rat_um_rad.server.validation.SelectDestinationCardsValidator;
import ch.progradler.rat_um_rad.shared.models.Activity;
import ch.progradler.rat_um_rad.shared.models.game.*;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.ErrorResponse;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.util.RandomGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.progradler.rat_um_rad.server.services.GameServiceUtil.sendInvalidActionResponse;
import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.*;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.*;
import static ch.progradler.rat_um_rad.shared.protocol.ServerCommand.*;
import static ch.progradler.rat_um_rad.shared.util.GameConfig.SHORT_DEST_CARDS_AT_START_COUNT;
import static ch.progradler.rat_um_rad.shared.util.RandomGenerator.generateRandomId;

/**
 * This is the implementation of {@link IGameService}.
 */
public class GameService implements IGameService {
    public static final Logger LOGGER = LogManager.getLogger();
    private final OutputPacketGateway outputPacketGateway;
    private final IGameRepository gameRepository;
    private final IUserRepository userRepository;
    private final IHighscoreRepository highscoreRepository;
    private final ActionHandlerFactory actionHandlerFactory;
    private final SelectDestinationCardsValidator selectDestinationCardsValidator;

    public GameService(OutputPacketGateway outputPacketGateway,
                       IGameRepository gameRepository,
                       IUserRepository userRepository,
                       IHighscoreRepository highscoreRepository,
                       ActionHandlerFactory actionHandlerFactory,
                       SelectDestinationCardsValidator selectDestinationCardsValidator) {
        this.outputPacketGateway = outputPacketGateway;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.highscoreRepository = highscoreRepository;
        this.actionHandlerFactory = actionHandlerFactory;
        this.selectDestinationCardsValidator = selectDestinationCardsValidator;
    }

    public GameService(OutputPacketGateway outputPacketGateway, IGameRepository gameRepository, IUserRepository userRepository, IHighscoreRepository highscoreRepository) {
        this(outputPacketGateway,
                gameRepository,
                userRepository,
                highscoreRepository,
                new ActionHandlerFactory(gameRepository, userRepository, highscoreRepository, outputPacketGateway),
                new SelectDestinationCardsValidator());
    }

    @Override
    public void createGame(String creatorIpAddress, int requiredPlayerCount) {
        boolean created = false;

        GameMap map = GameMap.defaultMap();
        Map<String, Player> players = new HashMap<>();
        Player creator = GameServiceUtil.createNewPlayer(creatorIpAddress, userRepository, new ArrayList<>());
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
        Packet.Server response = new Packet.Server(ServerCommand.GAME_CREATED, clientGame, ContentType.GAME);
        outputPacketGateway.sendPacket(creatorIpAddress, response);

        //Send updated game list to all players
        Packet.Server packet = new Packet.Server(SEND_WAITING_GAMES, gameRepository.getWaitingGames(), GAME_INFO_LIST);
        outputPacketGateway.broadcast(packet);
        LOGGER.info("Game with Id " + gameCreated.getId() + " created from user with Id " + gameCreated.getCreatorPlayerIpAddress());
    }

    @Override
    public void joinGame(String ipAddress, String gameId) {
        Game game = gameRepository.getGame(gameId);
        if (game.getStatus() == WAITING_FOR_PLAYERS) {
            addPlayerAndSaveGame(ipAddress, game);
            //send game to joined player
            ClientGame clientGame = GameServiceUtil.toClientGame(game, ipAddress);
            outputPacketGateway.sendPacket(ipAddress, new Packet.Server(GAME_JOINED, clientGame, GAME));

            GameServiceUtil.notifyPlayersOfGameUpdate(game, outputPacketGateway, NEW_PLAYER);
            LOGGER.info("User with Id " + ipAddress + " joined Game with id " + gameId);
        } else {
            outputPacketGateway.sendPacket(ipAddress, new Packet.Server(INVALID_ACTION_FATAL, ErrorResponse.JOINING_NOT_POSSIBLE, STRING));
            LOGGER.info("User with Id " + ipAddress + " couldn't join game with id " + gameId);
            return;
        }

        if (game.hasReachedRequiredPlayers()) {
            LOGGER.info("Enough players in game with id " + gameId);
            GameServiceUtil.prepareGame(game, gameRepository, outputPacketGateway);
            LOGGER.info("Called prepareGame() for game with id " + gameId);

            //send updated game list to all players
            Packet.Server packet = new Packet.Server(SEND_WAITING_GAMES, gameRepository.getWaitingGames(), GAME_INFO_LIST);
            outputPacketGateway.broadcast(packet);
        }
    }

    private void addPlayerAndSaveGame(String ipAddress, Game game) {
        List<PlayerColor> takenColors = game.getPlayers().values()
                .stream().map((PlayerBase::getColor)).toList();
        Player newPlayer = GameServiceUtil.createNewPlayer(ipAddress, userRepository, takenColors);
        game.getPlayers().put(ipAddress, newPlayer);
        gameRepository.updateGame(game);
    }

    @Override
    public void selectShortDestinationCards(String ipAddress, List<String> selectedCardIds) {
        Game game = GameServiceUtil.getCurrentGameOfPlayer(ipAddress, gameRepository);
        if (game == null) {
            sendInvalidActionResponse(ipAddress, ErrorResponse.PLAYER_IN_NO_GAME, outputPacketGateway);
            LOGGER.info("User with Id " + ipAddress + " coudln't select shortDestinationCards due to game == null");
            return;
        }

        switch (game.getStatus()) {
            case PREPARATION -> {
                handleShortDestinationCardsSelectedInPreparation(ipAddress, selectedCardIds, game);
                LOGGER.info("User with Id " + ipAddress + " selected shortDestinaitonCards in status " + PREPARATION.toString());
            }
            case STARTED -> {
                actionHandlerFactory.createSelectDestinationCardsActionHandler()
                        .handle(ipAddress, selectedCardIds);
                LOGGER.info("User with Id " + ipAddress + " selected shortDestinaitonCards in status " + STARTED.toString());
            }
        }
    }

    private void handleShortDestinationCardsSelectedInPreparation(String ipAddress, List<String> selectedCardIds, Game game) {
        Player player = game.getPlayers().get(ipAddress);

        if (!selectDestinationCardsValidator.validate(player, selectedCardIds)) {
            sendInvalidActionResponse(ipAddress, ErrorResponse.SELECTED_SHORT_DESTINATION_CARDS_INVALID, outputPacketGateway);
            return;
        }

        GameServiceUtil.updateGameStateForShortDestCardsSelectionGeneral(selectedCardIds, game, player);
        game.getPlayersHaveChosenShortDestinationCards().put(ipAddress, true);

        GameServiceUtil.notifyPlayersOfGameAction(ipAddress, game, outputPacketGateway, DESTINATION_CARDS_SELECTED);

        if (allPlayersSelectedShortDestCards(game)) {
            game.setStatus(STARTED);
        }

        game.getActivities().add(new Activity(player.getName(), DESTINATION_CARDS_SELECTED));
        gameRepository.updateGame(game);
    }

    private boolean allPlayersSelectedShortDestCards(Game game) {
        List<String> playerIps = new ArrayList<>(game.getPlayerIpAddresses());
        for (String playerIp : playerIps) {
            boolean playerSelected = game.getPlayersHaveChosenShortDestinationCards().getOrDefault(playerIp, false);
            if (!playerSelected) return false;
        }
        return true;
    }

    @Override
    public void requestShortDestinationCards(String ipAddress) {
        // TODO: add more checks and unit test
        Game game = GameServiceUtil.getCurrentGameOfPlayer(ipAddress, gameRepository);
        ActionValidator<?> actionValidator = new ActionValidator<>();
        String error = actionValidator.validate(new Action<>(game, ipAddress, null));
        if (error != null) {
            GameServiceUtil.sendInvalidActionResponse(ipAddress, error, outputPacketGateway);
        }

        List<DestinationCard> available = game.getDecksOfGame().getShortDestinationCardDeck().getCardDeck();
        List<DestinationCard> tooChoseFrom = new ArrayList<>();
        for (int i = 0; i < SHORT_DEST_CARDS_AT_START_COUNT; i++) {
            if (available.size() > 0) {
                DestinationCard picked = RandomGenerator.randomFromList(available);
                tooChoseFrom.add(picked);
            }
        }
        Player player = game.getPlayers().get(ipAddress);
        player.setShortDestinationCardsToChooseFrom(tooChoseFrom);
        gameRepository.updateGame(game);

        Packet.Server packet = new Packet.Server(REQUEST_SHORT_DESTINATION_CARDS_RESULT, GameServiceUtil.toClientGame(game, ipAddress), GAME);
        outputPacketGateway.sendPacket(ipAddress, packet);
    }


    @Override
    public void buildRoad(String ipAddress, String roadId) {
        LOGGER.info("Player " + ipAddress + " attempting to build road " + roadId);
        actionHandlerFactory.createRoadActionHandler().handle(ipAddress, roadId);
    }

    @Override
    public void takeWheelCardFromDeck(String ipAddress) {
        LOGGER.info("Player " + ipAddress + " attempting to take wheelCards.");
        actionHandlerFactory.createTakeWheelCardsActionHandler().handle(ipAddress, "");
    }

    @Override
    public void handleConnectionLoss(String ipAddress) {
        Game game = GameServiceUtil.getCurrentGameOfPlayer(ipAddress, gameRepository);
        if(game != null){
            game.setStatus(FINISHED);
            gameRepository.updateGame(game);
            GameServiceUtil.notifyPlayersOfGameUpdate(game, outputPacketGateway, GAME_ENDED_BY_PLAYER_DISCONNECTION);
        }
    }

    @Override
    public void getWaitingGames(String ipAddress) {
        Packet.Server packet = new Packet.Server(SEND_WAITING_GAMES, gameRepository.getWaitingGames(), GAME_INFO_LIST);
        outputPacketGateway.sendPacket(ipAddress, packet);
    }

    @Override
    public void getStartedGames(String ipAddress) {
        Packet.Server packet = new Packet.Server(SEND_STARTED_GAMES, gameRepository.getStartedGames(), GAME_INFO_LIST);
        outputPacketGateway.sendPacket(ipAddress, packet);
    }

    @Override
    public void getFinishedGames(String ipAddress) {
        Packet.Server packet = new Packet.Server(SEND_FINISHED_GAMES, gameRepository.getFinishedGames(), GAME_INFO_LIST);
        outputPacketGateway.sendPacket(ipAddress, packet);
    }

    @Override
    public void requestHighscores(String ipAddress) {
        Packet.Server packet = new Packet.Server(SEND_HIGHSCORES, highscoreRepository.getHighscores(), HIGHSCORE_LIST);
        outputPacketGateway.sendPacket(ipAddress, packet);
    }
}
