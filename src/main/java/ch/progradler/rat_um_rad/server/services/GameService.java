package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.client.models.ClientGame;
import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.PlayerBase;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
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

import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.WAITING_FOR_PLAYERS;
import static ch.progradler.rat_um_rad.shared.protocol.Command.*;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.GAME_INFO_LIST;
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
    }

    @Override
    public void joinGame(String ipAddress, String gameId) {
        Game game = gameRepository.getGame(gameId);
        if (game.getStatus() == WAITING_FOR_PLAYERS) {
            addPlayerAndSaveGame(ipAddress, game);
            GameServiceUtil.notifyPlayersOfGameUpdate(game, outputPacketGateway, NEW_PLAYER);
        } else {
            outputPacketGateway.sendPacket(ipAddress, new Packet(INVALID_ACTION_FATAL, ErrorResponse.JOINING_NOT_POSSIBLE, STRING));
            return;
        }

        if (game.hasReachedRequiredPlayers()) {
            GameServiceUtil.startGame(game, gameRepository, outputPacketGateway);
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
    public void selectShortDestinationCards(String ipAddress, List<String> selectedCards) {
        Game game = GameServiceUtil.getCurrentGameOfPlayer(ipAddress, gameRepository);
        GameStatus gameStatus = game.getStatus();
        Player player = game.getPlayers().get(ipAddress);
        switch(gameStatus) {
            case PREPARATION -> {
                for (DestinationCard shortDestinationCard: player.getShortDestinationCards()) {
                    if (! selectedCards.contains(shortDestinationCard.getCardID())) {
                        putBackDestinationCard(ipAddress, player.getShortDestinationCards().indexOf(shortDestinationCard));
                    }
                }
                game.getPlayersHaveChosenShortDestinationCards().put(ipAddress, true);
                if (! game.getPlayersHaveChosenShortDestinationCards().containsValue(false)) {
                    startGameRounds(game.getId());
                }
            }
            case STARTED -> {
                //TODO: implement
            }
        }
    }

    private void putBackDestinationCard(String ipAddress, int indexOfCard) {
        Game game = GameServiceUtil.getCurrentGameOfPlayer(ipAddress, gameRepository);
        Player player = game.getPlayers().get(ipAddress);
        DestinationCard destinationCard = player.getShortDestinationCards().get(indexOfCard);
        player.getShortDestinationCards().remove(destinationCard);
        game.getDecksOfGame().getShortDestinationCardDeck().getCardDeck().add(destinationCard);
    }

    private void startGameRounds(String gameId) {
        //TODO: implement
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
        Packet packet = new Packet(SEND_GAMES, gameRepository.getWaitingGames(), GAME_INFO_LIST);
        outputPacketGateway.sendPacket(ipAddress, packet);
    }

    @Override
    public void getStartedGames(String ipAddress) {
        Packet packet = new Packet(SEND_GAMES, gameRepository.getStartedGames(), GAME_INFO_LIST);
        outputPacketGateway.sendPacket(ipAddress, packet);
    }

    @Override
    public void getFinishedGames(String ipAddress) {
        Packet packet = new Packet(SEND_GAMES, gameRepository.getFinishedGames(), GAME_INFO_LIST);
        outputPacketGateway.sendPacket(ipAddress, packet);
    }
}
