package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.game.*;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.ErrorResponse;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.util.GameConfig;
import ch.progradler.rat_um_rad.shared.util.RandomGenerator;

import java.util.*;

import static ch.progradler.rat_um_rad.server.services.GameServiceUtil.sendInvalidActionResponse;
import static ch.progradler.rat_um_rad.server.services.GameServiceUtil.validateAndHandleActionPrecondition;
import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.STARTED;
import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.WAITING_FOR_PLAYERS;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.*;
import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.*;
import static ch.progradler.rat_um_rad.shared.protocol.ServerCommand.*;
import static ch.progradler.rat_um_rad.shared.util.RandomGenerator.generateRandomId;

/**
 * This is the implementation of {@link IGameService}.
 */
public class GameService implements IGameService {
    private final OutputPacketGateway outputPacketGateway;
    private final IGameRepository gameRepository;
    private final IUserRepository userRepository;

    public GameService(OutputPacketGateway outputPacketGateway, IGameRepository gameRepository, IUserRepository userRepository) {
        this.outputPacketGateway = outputPacketGateway;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
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
    }

    @Override
    public void joinGame(String ipAddress, String gameId) {
        // TODO: check if player not already in other game?
        Game game = gameRepository.getGame(gameId);
        if (game.getStatus() == WAITING_FOR_PLAYERS) {
            addPlayerAndSaveGame(ipAddress, game);
            //send game to joined player
            ClientGame clientGame = GameServiceUtil.toClientGame(game, ipAddress);
            outputPacketGateway.sendPacket(ipAddress, new Packet.Server(GAME_JOINED, clientGame, GAME));

            GameServiceUtil.notifyPlayersOfGameUpdate(game, outputPacketGateway, NEW_PLAYER);
        } else {
            outputPacketGateway.sendPacket(ipAddress, new Packet.Server(INVALID_ACTION_FATAL, ErrorResponse.JOINING_NOT_POSSIBLE, STRING));
            return;
        }

        if (game.hasReachedRequiredPlayers()) {
            GameServiceUtil.prepareGame(game, gameRepository, outputPacketGateway);

            //send updated game list to all players
            Packet.Server packet = new Packet.Server(SEND_WAITING_GAMES, gameRepository.getWaitingGames(), GAME_INFO_LIST);
            outputPacketGateway.broadcast(packet);
        }
    }

    private void addPlayerAndSaveGame(String ipAddress, Game game) {
        List<WheelColor> takenColors = game.getPlayers().values()
                .stream().map((PlayerBase::getColor)).toList();
        Player newPlayer = GameServiceUtil.createNewPlayer(ipAddress, userRepository, takenColors);
        game.getPlayers().put(ipAddress, newPlayer);
        gameRepository.updateGame(game);
    }

    @Override
    public void exitGame(String ipAddress) {
        //TODO: implement
    }

    @Override
    public void selectShortDestinationCards(String ipAddress, List<String> selectedCardIds) {
        if (selectedCardIds.isEmpty()) {
            sendInvalidActionResponse(ipAddress,
                    ErrorResponse.SELECTED_SHORT_DESTINATION_CARDS_INVALID, outputPacketGateway);
            return;
        }

        Game game = GameServiceUtil.getCurrentGameOfPlayer(ipAddress, gameRepository);
        if (game == null) {
            sendInvalidActionResponse(ipAddress, ErrorResponse.PLAYER_IN_NO_GAME, outputPacketGateway);
            return;
        }

        Player player = game.getPlayers().get(ipAddress);

        switch (game.getStatus()) {
            case PREPARATION -> {
                handleShortDestinationCardsSelectedInPreparation(ipAddress, selectedCardIds, game, player);
            }
            case STARTED -> {
                handleShortDestinationCardsSelectedAsAction(ipAddress, selectedCardIds, game, player);
            }
        }
    }

    private void handleShortDestinationCardsSelectedInPreparation(String ipAddress, List<String> selectedCardIds, Game game, Player player) {
        if (!checkValidSelectedCardIds(player, selectedCardIds)) {
            sendInvalidActionResponse(ipAddress, ErrorResponse.SELECTED_SHORT_DESTINATION_CARDS_INVALID, outputPacketGateway);
            return;
        }

        handleShortDestCardsSelectionGeneral(selectedCardIds, game, player);
        game.getPlayersHaveChosenShortDestinationCards().put(ipAddress, true);

        if (allPlayersSelectedShortDestCards(game)) {
            game.setStatus(STARTED);
            GameServiceUtil.notifyPlayersOfGameAction(ipAddress, game, outputPacketGateway, DESTINATION_CARDS_SELECTED);
        }

        gameRepository.updateGame(game);
    }

    private void handleShortDestinationCardsSelectedAsAction(String ipAddress, List<String> selectedCardIds, Game game, Player player) {
        if (!validateAndHandleActionPrecondition(ipAddress, game, outputPacketGateway)) {
            return;
        }

        handleShortDestCardsSelectionGeneral(selectedCardIds, game, player);

        gameRepository.updateGame(game);
        GameServiceUtil.notifyPlayersOfGameAction(ipAddress, game, outputPacketGateway, DESTINATION_CARDS_SELECTED);
    }

    private void handleShortDestCardsSelectionGeneral(List<String> selectedCardIds, Game game, Player player) {
        List<DestinationCard> shortDestCardDeck = game.getDecksOfGame()
                .getShortDestinationCardDeck().getCardDeck();
        List<DestinationCard> selectedCards = shortDestCardDeck.stream()
                .filter(c -> selectedCardIds.contains(c.getCardID())).toList();

        player.setShortDestinationCards(selectedCards);
        shortDestCardDeck.removeAll(selectedCards);
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
        if (!GameServiceUtil.validateAndHandleActionPrecondition(ipAddress, game, outputPacketGateway)) {
            return;
        }

        List<DestinationCard> available = game.getDecksOfGame().getShortDestinationCardDeck().getCardDeck();
        List<DestinationCard> tooChoseFrom = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (available.size() > 0) {
                DestinationCard picked = RandomGenerator.randomFromList(available);
                available.remove(picked);
                tooChoseFrom.add(picked);
            }
        }
        Player player = game.getPlayers().get(ipAddress);
        player.setShortDestinationCardsToChooseFrom(tooChoseFrom);
        gameRepository.updateGame(game);

        Packet.Server packet = new Packet.Server(REQUEST_SHORT_DESTINATION_CARDS_RESULT, GameServiceUtil.toClientGame(game, ipAddress), GAME);
        outputPacketGateway.sendPacket(ipAddress, packet);
    }

    private boolean checkValidSelectedCardIds(Player player, List<String> selectedCardIds) {
        if (selectedCardIds.size() > 3) return false;
        List<String> optionCardIds = player.getShortDestinationCardsToChooseFrom().stream()
                .map(DestinationCard::getCardID).toList();
        for (String selectedCardId : selectedCardIds) {
            if (!optionCardIds.contains(selectedCardId)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void buildRoad(String ipAddress, String roadId) {
        Game game = GameServiceUtil.getCurrentGameOfPlayer(ipAddress, gameRepository);
        if (!GameServiceUtil.validateAndHandleActionPrecondition(ipAddress, game, outputPacketGateway)) {
            return;
        }

        Map<String, String> roadsBuilt = game.getRoadsBuilt();
        if (roadsBuilt.containsKey(roadId)) {
            sendInvalidActionResponse(ipAddress, ROAD_ALREADY_BUILT_ON, outputPacketGateway);
            return;
        }

        List<Road> roads = game.getMap().getRoads();
        Optional<Road> roadOpt = roads.stream().filter((r) -> r.getId().equals(roadId)).findFirst();
        if (roadOpt.isEmpty()) {
            sendInvalidActionResponse(ipAddress, ROAD_DOES_NOT_EXIST, outputPacketGateway);
            return;
        }

        Road road = roadOpt.get();
        Player player = game.getPlayers().get(ipAddress);

        if (player.getWheelsRemaining() < road.getRequiredWheels()) {
            sendInvalidActionResponse(ipAddress, NOT_ENOUGH_WHEELS_TO_BUILD_ROAD, outputPacketGateway);
            return;
        }

        if (!hasRequiredCardsToBuild(player, road)) {
            sendInvalidActionResponse(ipAddress, NOT_ENOUGH_CARDS_OF_REQUIRED_COLOR_TO_BUILD_ROAD, outputPacketGateway);
            return;
        }

        handleBuildRoad(ipAddress, game, road);
    }

    /**
     * This method assumes, all input is already checked.
     */
    private void handleBuildRoad(String ipAddress, Game game, Road road) {
        Player player = game.getPlayers().get(ipAddress);

        List<WheelCard> playersCardsOfColor = player.getWheelCards().stream()
                .filter((c) -> c.getColor() == road.getColor())
                .toList();

        for (int i = 0; i < road.getRequiredWheels(); i++) {
            player.getWheelCards().remove(playersCardsOfColor.get(i));
        }
        player.setWheelsRemaining(player.getWheelsRemaining() - road.getRequiredWheels());
        player.setScore(player.getScore() + GameConfig.scoreForRoadBuild(road.getRequiredWheels()));

        game.getRoadsBuilt().put(road.getId(), ipAddress);

        // TODO: check if has very few wheels left -> send info that game will finish soon

        gameRepository.updateGame(game);
        GameServiceUtil.notifyPlayersOfGameUpdate(game, outputPacketGateway, ROAD_BUILT);
    }

    private boolean hasRequiredCardsToBuild(Player player, Road road) {
        List<WheelCard> playersCardsOfColor = player.getWheelCards().stream()
                .filter((c) -> c.getColor() == road.getColor())
                .toList();
        return playersCardsOfColor.size() >= road.getRequiredWheels();
    }

    @Override
    public void buildGreyRoad(String ipAddress, String roadId, WheelColor color) {
        //TODO: implement
    }

    @Override
    public void takeWheelCardFromDeck(String ipAddress) {
        //TODO: implement
    }

    @Override
    public void takeOpenWheelCard(String ipAdress, WheelCard wheelCard) {
        //TODO: implement
    }

    @Override
    public void takeDestinationCard(String ipAdress) {
        //TODO: implement
    }

    @Override
    public void handleConnectionLoss(String ipAddress) {
        //TODO: implement
    }

    @Override
    public void wantToFinishGame(String ipAddress) {
        //TODO: implement
    }

    @Override
    public void dontWantToFinishGame(String ipAddress) {
        //TODO: implement
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
}
