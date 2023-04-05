package ch.progradler.rat_um_rad.client.services;
import java.io.IOException;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.client.models.User;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

/**
 * Implementation of {@link IUserService}.
 * Uses {@link OutputPacketGateway} to send correct {@link Packet}s to server.
 */
public class UserService implements IUserService {
    private OutputPacketGateway outputPacketGateway;
    public UserService() {
        //Do nothing
    }
    public UserService(OutputPacketGateway outputPacketGateway) {
        this.outputPacketGateway = outputPacketGateway;
    }

    @Override
    public void sendChosenUsernameToServer(String username) {
        try {
            System.out.println("userService sendchosenusernametoserver");
            Packet packet = new Packet(Command.NEW_USER, username, ContentType.STRING); //TODO: Commands NEW_USER und SET_USERNAME ändern
            outputPacketGateway.sendPacket(packet);
        } catch (IOException e) {
            e.printStackTrace();
            //LOGGER.warn("Failed to send username to server!"); //TODO: choose appropriate logger levels for all logs
            //return chooseAndSendUsername(outputPacketGateway);
        }
    }

    public void changeUsername(String username) {
        User.getInstance().setConfirmedUsername(username);
    }
}

