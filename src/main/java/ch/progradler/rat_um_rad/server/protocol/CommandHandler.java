package ch.progradler.rat_um_rad.server.protocol;

import ch.progradler.rat_um_rad.server.gateway.InputPacketGateway;
import ch.progradler.rat_um_rad.server.protocol.pingpong.ServerPingPongRunner;
import ch.progradler.rat_um_rad.server.services.IGameService;
import ch.progradler.rat_um_rad.server.services.IUserService;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

/**
 * Handles incoming commands by clients.
 */
public class CommandHandler implements InputPacketGateway {

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
     *
     * For more information on the protocol, read the corresponding document or read the javadoc of the
     * commands in the switch cases or the used methods in the code block for each case.
     */
    public void handleClientCommand(Packet packet, String ipAddress) {
        // TODO: unittest

        switch (packet.getCommand()) {
            case NEW_USER -> {
                System.out.println("NEW_USER angekommen");
                userService.handleNewUser((String) packet.getContent(), ipAddress);
            }
            case SEND_CHAT -> {
                System.out.println("message vom user angekommen beim server");
                userService.handleMessageFromUser((String) packet.getContent(), ipAddress);
            }
            case USER_DISCONNECTED -> {
                userService.handleUserDisconnected(ipAddress);
            }
            case PONG -> {
                serverPingPongRunner.setPongArrived(ipAddress);
            }
            case SET_USERNAME -> {
                userService.updateUsername((String) packet.getContent(), ipAddress);
            }
            case CREATE_GAME -> {
                int requiredPlayerCount = (int) packet.getContent();
                gameService.createGame(ipAddress, requiredPlayerCount);
            }
        }
    }
}
