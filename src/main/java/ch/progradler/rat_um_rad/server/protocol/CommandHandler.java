package ch.progradler.rat_um_rad.server.protocol;

import ch.progradler.rat_um_rad.server.gateway.InputPacketGateway;
import ch.progradler.rat_um_rad.server.services.IUserService;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

/**
 * Handles incoming commands by clients.
 */
public class CommandHandler implements InputPacketGateway {

    private final IUserService userService;

    public CommandHandler(IUserService userService) {
        this.userService = userService;
    }

    public void handleClientCommand(Packet packet, String ipAddress) {
        // TODO: unittest

        switch (packet.getCommand()) {
            case NEW_USER -> {
                userService.handleNewUser((String) packet.getContent(), ipAddress);
            }
            case SEND_CHAT -> {
                userService.handleMessageFromUser((String) packet.getContent(), ipAddress);
            }
            case CLIENT_DISCONNECTED -> {
                userService.handleUserDisconnected(ipAddress);
            }
        }
    }
}
