package ch.progradler.rat_um_rad.client.services;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.client.gateway.OutputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.IOException;

/**
 * Implementation of {@link IUserService}.
 * Uses {@link OutputPacketGateway} to send correct {@link Packet}s to server.
 */
public class UserService implements IUserService {
    private OutputPacketGateway outputPacketGateway;

    public UserService() {
        //Do nothing TODO: remove this constructor!!!
        outputPacketGateway = OutputPacketGatewaySingleton.getOutputPacketGateway();
    }

    public UserService(OutputPacketGateway outputPacketGateway) {
        this.outputPacketGateway = outputPacketGateway;
    }

    @Override
    public void sendChosenUsernameToServer(String username) {
        try {
            Packet packet = new Packet(Command.NEW_USER, username, ContentType.STRING); //TODO: Commands NEW_USER und SET_USERNAME ändern
            outputPacketGateway.sendPacket(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendChatMessageToServer(String chatContent)  {
        try {
            Packet packet = new Packet(Command.SEND_CHAT, chatContent, ContentType.STRING);
            outputPacketGateway.sendPacket(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

