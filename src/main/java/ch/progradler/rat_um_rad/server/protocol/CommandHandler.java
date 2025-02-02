package ch.progradler.rat_um_rad.server.protocol;

import ch.progradler.rat_um_rad.server.gateway.InputPacketGateway;
import ch.progradler.rat_um_rad.server.protocol.pingpong.ServerPingPongRunner;
import ch.progradler.rat_um_rad.server.services.IGameService;
import ch.progradler.rat_um_rad.server.services.IUserService;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.protocol.ClientCommand;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Handles incoming commands by clients.
 */
public class CommandHandler implements InputPacketGateway {
    public static final Logger LOGGER = LogManager.getLogger();

    private final IUserService userService;
    private final IGameService gameService;
    private final ServerPingPongRunner serverPingPongRunner;

    public CommandHandler(ServerPingPongRunner serverPingPongRunner, IUserService userService, IGameService gameService) {
        this.serverPingPongRunner = serverPingPongRunner;
        this.userService = userService;
        this.gameService = gameService;
    }

    /**
     * Receives from ClientInputListener the packet sent by client and corresponding ipAddress. This is the implementation of the protocol.
     * <p>
     * For more information on the protocol, read the corresponding document or read the javadoc of the
     * commands in the switch cases or the used methods in the code block for each case.
     */
    synchronized public void handleCommand(Packet<ClientCommand> packet, String ipAddress) {
        if (packet.getCommand() != ClientCommand.PONG)
            LOGGER.info("Received client command " + packet.getCommand() + " from " + ipAddress);

        switch (packet.getCommand()) {
            case SEND_WHISPER_CHAT -> {
                ChatMessage chatMessage = (ChatMessage) packet.getContent();
                userService.handleWhisperMessageFromUser(chatMessage.getMessage(),
                        chatMessage.getUsername(), ipAddress);
            }
            case REGISTER_USER -> userService.handleNewUser((String) packet.getContent(), ipAddress);
            case SEND_BROADCAST_CHAT -> userService.handleBroadCastMessageFromUser((String) packet.getContent(), ipAddress);
            case SEND_GAME_INTERNAL_CHAT -> userService.handleGameInternalMessageFromUser((String) packet.getContent(), ipAddress);
            case USER_SOCKET_DISCONNECTED -> {
                userService.handleUserDisconnected(ipAddress);
                gameService.handleConnectionLoss(ipAddress);
            }
            case PONG -> {
                serverPingPongRunner.setPongArrived(ipAddress);
            }
            case SET_USERNAME -> userService.updateUsername((String) packet.getContent(), ipAddress);
            case CREATE_GAME -> {
                int requiredPlayerCount = (int) packet.getContent();
                gameService.createGame(ipAddress, requiredPlayerCount);
            }
            case REQUEST_ALL_CONNECTED_PLAYERS -> userService.requestOnlinePlayers(ipAddress);
            case REQUEST_GAMES -> {
                GameStatus status = (GameStatus) packet.getContent();
                switch (status) {
                    case WAITING_FOR_PLAYERS -> gameService.getWaitingGames(ipAddress);
                    case PREPARATION -> throw new IllegalArgumentException("Cannot request games in preparation");
                    case STARTED -> gameService.getStartedGames(ipAddress);
                    case FINISHED -> gameService.getFinishedGames(ipAddress);
                }
            }
            case WANT_JOIN_GAME -> gameService.joinGame(ipAddress, (String) packet.getContent());
            case SHORT_DESTINATION_CARDS_SELECTED -> {
                gameService.selectShortDestinationCards(ipAddress, (List<String>) packet.getContent());
            }
            case REQUEST_WHEEL_CARDS -> {
                gameService.takeWheelCardFromDeck(ipAddress);
            }
            case BUILD_ROAD -> gameService.buildRoad(ipAddress, (String) packet.getContent());
            case REQUEST_SHORT_DESTINATION_CARDS -> gameService.requestShortDestinationCards(ipAddress);
            case REQUEST_HIGHSCORES -> gameService.requestHighscores(ipAddress);
            default -> throw new IllegalStateException("Unexpected value: " + packet.getCommand());
        }
    }
}
