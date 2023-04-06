package ch.progradler.rat_um_rad.client.gui.javafx.chatRoom;

import ch.progradler.rat_um_rad.client.models.User;


public class ChatRoomModel {

    private User user;

    public ChatRoomModel(User user) {
        this.user = user;
    }

    public String getUsername() {
        return user.getUsername();
    }
}
