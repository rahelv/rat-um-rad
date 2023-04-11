package ch.progradler.rat_um_rad.client.services;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.IOException;

public class UserService implements IUserService {
    private final OutputPacketGateway outputPacketGateway;

    public UserService(OutputPacketGateway outputPacketGateway) {
        this.outputPacketGateway = outputPacketGateway;
    }

    @Override
    public void sendUsername(String username) throws IOException {
        Packet packet = new Packet(Command.NEW_USER, username, ContentType.STRING);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void changeUsername(String username) throws IOException {
        Packet packet = new Packet(Command.SET_USERNAME, username, ContentType.STRING);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void sendBroadCastMessage(String message) throws IOException {
        Packet packet = new Packet(Command.SEND_BROADCAST_CHAT, message, ContentType.STRING);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void sendGameInternalMessage(String message) throws IOException {
        Packet packet = new Packet(Command.SEND_GAME_INTERNAL_CHAT, message, ContentType.STRING);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void sendWhisperMessage(String message, String toUsername) throws IOException {
        Packet packet = new Packet(Command.SEND_WHISPER_CHAT,
                new ChatMessage(toUsername, message),
                ContentType.CHAT_MESSAGE);
        outputPacketGateway.sendPacket(packet);
    }

    @Override
    public void requestOnlinePlayers() throws IOException {
        Packet packet = new Packet(Command.REQUEST_ALL_CONNECTED_PLAYERS, null, ContentType.NONE);
        outputPacketGateway.sendPacket(packet);
    }
}
