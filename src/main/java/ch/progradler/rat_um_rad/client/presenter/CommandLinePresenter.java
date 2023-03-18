package ch.progradler.rat_um_rad.client.presenter;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

/**
 * Presents packets via command line.
 */
public class CommandLinePresenter implements PackagePresenter {
    @Override
    public  void display(Packet packet) {
        Object content = packet.getContent();
        ContentType contentType = packet.getContentType();

        switch (contentType){
            case CHAT_MESSAGE -> {
                displayChatMessage((ChatMessage) content);
            }
            case USERNAME -> {
                if (packet.getCommand() == Command.NEW_USER) {
                    displayNewUserAdded((String) packet.getContent());
                }
            }
        }
    }

    private void displayNewUserAdded(String username) {
        System.out.println("New user joined chat: " + username);
    }

    private void displayChatMessage(ChatMessage message) {
        System.out.println("Message by " + message.getUsername() + ": " + message.getMessage());
    }
}
