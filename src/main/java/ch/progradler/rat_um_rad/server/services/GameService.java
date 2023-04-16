package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.shared.models.Point;
import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.*;
import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCardDeck;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.ErrorResponse;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.progradler.rat_um_rad.shared.protocol.Command.SEND_GAMES;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.*;
import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.WAITING_FOR_PLAYERS;
import static ch.progradler.rat_um_rad.shared.protocol.Command.*;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.STRING;
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
        Packet response = new Packet(Command.GAME_CREATED, clientGame, ContentType.GAME);
        outputPacketGateway.sendPacket(creatorIpAddress, response);

        //Send updated game list to all players
        Packet packet = new Packet(SEND_GAMES, gameRepository.getWaitingGames(), GAME_INFO_LIST_WAITING);
        outputPacketGateway.broadcast(packet);
    }

    @Override
    public void joinGame(String ipAddress, String gameId) {
        Game game = gameRepository.getGame(gameId);
        if (game.getStatus() == WAITING_FOR_PLAYERS) {
            addPlayerAndSaveGame(ipAddress, game);
            //send game to joined player
            ClientGame clientGame = GameServiceUtil.toClientGame(game, ipAddress);
            outputPacketGateway.sendPacket(ipAddress, new Packet(GAME_JOINED, clientGame, GAME));

            GameServiceUtil.notifyPlayersOfGameUpdate(game, outputPacketGateway, NEW_PLAYER);
        } else {
            outputPacketGateway.sendPacket(ipAddress, new Packet(INVALID_ACTION_FATAL, ErrorResponse.JOINING_NOT_POSSIBLE, STRING));
            return;
        }

        if (game.hasReachedRequiredPlayers()) {
            GameServiceUtil.startGame(game, gameRepository, outputPacketGateway);

            //send updated game list to all players
            Packet packet = new Packet(SEND_GAMES, gameRepository.getWaitingGames(), GAME_INFO_LIST_WAITING);
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
            sendInvalidSelectedShortDestCardsResponse(ipAddress, ErrorResponse.SELECTED_SHORT_DESTINATION_CARDS_INVALID);
            return;
        }

        Game game = GameServiceUtil.getCurrentGameOfPlayer(ipAddress, gameRepository);
        if (game == null) {
            sendInvalidSelectedShortDestCardsResponse(ipAddress, ErrorResponse.PLAYER_IN_NO_GAME);
            return;
        }

        Player player = game.getPlayers().get(ipAddress);

        if (!checkValidSelectedCardIds(player, selectedCardIds)) {
            sendInvalidSelectedShortDestCardsResponse(ipAddress, ErrorResponse.SELECTED_SHORT_DESTINATION_CARDS_INVALID);
            return;
        }

        switch (game.getStatus()) {
            case PREPARATION -> {
                List<DestinationCard> shortDestCardDeck = game.getDecksOfGame()
                        .getShortDestinationCardDeck().getCardDeck();
                List<DestinationCard> selectedCards = shortDestCardDeck.stream()
                        .filter(c -> selectedCardIds.contains(c.getCardID())).toList();

                player.setShortDestinationCards(selectedCards);
                shortDestCardDeck.removeAll(selectedCards);
                game.getPlayersHaveChosenShortDestinationCards().put(ipAddress, true);

                gameRepository.updateGame(game);

                if (allPlayersSelectedShortDestCards(game)) {
                    GameServiceUtil.startGameRounds(game, gameRepository, outputPacketGateway);
                }
            }
            case STARTED -> {
                //TODO: implement
            }
        }
    }

    private boolean allPlayersSelectedShortDestCards(Game game) {
        List<String> playerIps = new ArrayList<>(game.getPlayerIpAddresses());
        for (String playerIp : playerIps) {
            boolean playerSelected = game.getPlayersHaveChosenShortDestinationCards().getOrDefault(playerIp, false);
            if (!playerSelected) return false;
        }
        return true;
    }

    private void sendInvalidSelectedShortDestCardsResponse(String ipAddress, String errorMessage) {
        Packet errorResponse = new Packet(Command.SHORT_DESTINATION_CARDS_SELECTED_IN_PREPARATION,
                errorMessage,
                STRING);
        outputPacketGateway.sendPacket(ipAddress, errorResponse);
    }

    private boolean checkValidSelectedCardIds(Player player, List<String> selectedCardIds) {
        if (selectedCardIds.size() > 3) return false;
        List<String> optionCardIds = player.getShortDestinationCards().stream().map(DestinationCard::getCardID).toList();
        for (String selectedCardId : selectedCardIds) {
            if (!optionCardIds.contains(selectedCardId)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void buildRoad(String ipAddress, String roadId) {
        //TODO: implement
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
        Packet packet = new Packet(SEND_GAMES, gameRepository.getWaitingGames(), GAME_INFO_LIST_WAITING);
        outputPacketGateway.sendPacket(ipAddress, packet);
    }

    @Override
    public void getStartedGames(String ipAddress) {
        Packet packet = new Packet(SEND_GAMES, gameRepository.getStartedGames(), GAME_INFO_LIST_STARTED);
        outputPacketGateway.sendPacket(ipAddress, packet);
    }

    @Override
    public void getFinishedGames(String ipAddress) {
        Packet packet = new Packet(SEND_GAMES, gameRepository.getFinishedGames(), GAME_INFO_LIST_FINISHED);
        outputPacketGateway.sendPacket(ipAddress, packet);
    }
}
