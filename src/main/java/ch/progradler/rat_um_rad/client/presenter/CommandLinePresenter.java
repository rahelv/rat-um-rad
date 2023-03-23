package ch.progradler.rat_um_rad.client.presenter;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
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

        switch (packet.getCommand()) {
            case NEW_USER -> {
                if (contentType == ContentType.USERNAME) {
                    displayNewUserAdded((String) content);
                }
            }
            case USERNAME_CONFIRMED -> {
            }
            case CHANGED_USERNAME -> {
                displayChangedUsername((UsernameChange) content);
            }
            case USER_DISCONNECTED -> {
                displayUserDisconnected((String) content);
            }
            case SEND_CHAT -> {
                if (contentType == ContentType.CHAT_MESSAGE) {
                    displayChatMessage((ChatMessage) content);
                }
            }
            case CLIENT_DISCONNECTED -> {
            }
        }
    }

    private void displayNewUserAdded(String username) {
        System.out.println("New user joined chat: " + username);
    }

    private void displayChangedUsername(UsernameChange change) {
        System.out.println("User " + change.getOldName() + " changed name to " + change.getNewName());
    }

    private void displayUserDisconnected(String username) {
        System.out.println("User " + username + " disconnected");
    }

    private void displayChatMessage(ChatMessage message) {
        System.out.println("Message by " + message.getUsername() + ": " + message.getMessage());
    }
}
