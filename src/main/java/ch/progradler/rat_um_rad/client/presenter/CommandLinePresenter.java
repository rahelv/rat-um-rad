package ch.progradler.rat_um_rad.client.presenter;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

/**
 * Presents packets via command line.
 */
public class CommandLinePresenter implements PackagePresenter {
    /**
     * chooses how to display packet depending on command.
     * @param packet
     */
    @Override
    public  void display(Packet packet) {
        Object content = packet.getContent();
        ContentType contentType = packet.getContentType();

        switch (packet.getCommand()) {
            case NEW_USER -> {
                if (contentType == ContentType.STRING) {
                    displayNewUserAdded((String) content);
                }
            }
            case USERNAME_CONFIRMED -> {
                UsernameChange usernameChange = (UsernameChange) content;
                displayUsernameChangeResult(usernameChange);
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
            case INVALID_ACTION_WARNING, INVALID_ACTION_FATAL -> {
                displayString((String) content);
            }
        }
    }

    /** decides which message to display depending on the result of the username change.
     * @param usernameChange
     */
    private void displayUsernameChangeResult(UsernameChange usernameChange) {
        if(!usernameChange.getOldName().equals(usernameChange.getNewName())) { //TODO: here oldname is the chosenname by user and new name is the name set by server
            displayChangedUsernameAgainstUserChoice(usernameChange);
        } else {
            displayConfirmChangedUsername(usernameChange);
        }
    }

    private void displayNewUserAdded(String username) {
        System.out.println("New user joined chat: " + username);
    }

    private void displayChangedUsername(UsernameChange change) {
        System.out.println("User " + change.getOldName() + " changed name to " + change.getNewName());
    }

    /** displays username change after username is changed. this method is called when the username confirmed by server matches the one chosen by user.
     * @param usernameChange
     */
    private void displayConfirmChangedUsername(UsernameChange usernameChange) {
        System.out.println("Your username has been changed to: " + usernameChange.getNewName());
    }

    /** displays username change after username is changed, when the server didn't take the username chosen by the user.
     * @param usernameChange
     */
    private void displayChangedUsernameAgainstUserChoice(UsernameChange usernameChange) {
        System.out.println("Your chosen username was already taken. You were assigned this username instead: " + usernameChange.getNewName());
    }

    private void displayUserDisconnected(String username) {
        System.out.println("User " + username + " disconnected");
    }

    private void displayChatMessage(ChatMessage message) {
        System.out.println("Message by " + message.getUsername() + ": " + message.getMessage());
    }

    private void displayString(String message) {
        System.out.println(message);
    }
}
