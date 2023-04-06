package ch.progradler.rat_um_rad.client.services;

import java.io.IOException;

public interface IUserService {
    void sendChosenUsernameToServer(String username) throws IOException;

    void sendChatMessageToServer(String sender,String chatMessage) throws IOException;
}
