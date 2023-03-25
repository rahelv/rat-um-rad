package ch.progradler.rat_um_rad.client.command_line;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.client.utils.ComputerInfo;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

public class UsernameHandler {
    private static final String PROPERTY_NAME_USERNAME = "username";

    private final ComputerInfo computerInfo;
    private final InputReader inputReader;
    private String username;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Adds PropertyChangeListener for the username property
     * @param listener
     */
    public void addUsernameObserver(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(PROPERTY_NAME_USERNAME, listener);
    }

    public UsernameHandler() {
        this.computerInfo = new ComputerInfo();
        this.inputReader = new InputReader();
    }

    /**
     * Constructor added for testing purposes.
     * @param computerInfo
     * @param inputReader
     */
    public UsernameHandler(ComputerInfo computerInfo, InputReader inputReader) {
        this.computerInfo = computerInfo;
        this.inputReader = inputReader;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Sets the username which is received from server. Triggers PropertyChange so Observers (CommandLineHandler) are notified of username change.
     * @param username
     */
    public void setConfirmedUsername(String username) {
        String oldUsername = this.username;
        this.username = username;
        propertyChangeSupport.firePropertyChange(PROPERTY_NAME_USERNAME, oldUsername, username);
    }

    /**
     * Asks the user to set a username, suggests the system username as default username.
     * This method is used to set the username the first time.
     * @param
     * @return Username chosen by user as String
     */
    public String chooseUsername() {
        String suggestedUsername = computerInfo.getSystemUsername();
        String answerToSuggestedUsername = inputReader.readInputWithPrompt(
                "The username suggested for you is: " +
                        suggestedUsername +
                        ".\nPress enter to confirm. Otherwise enter your new username below and click Enter." +
                        "\nTo change your username in the future, type CHANGEUSERNAME and press Enter");
        // TODO: check if username is not empty or null and unittest
        if (answerToSuggestedUsername.equals("")) {
            return suggestedUsername;
        } else {
            return answerToSuggestedUsername;
        }
    }

    /**
     * choose Username @see chooseUsername(), and send this chosen Username to Server
     *
     * @return username which was chosen by server
     */
    public String chooseAndSendUsername(OutputPacketGateway outputPacketGateway) {
        String username = chooseUsername();

        try {
            outputPacketGateway.sendPacket(new Packet(Command.NEW_USER, username, ContentType.STRING));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to send username to server!");
            return chooseAndSendUsername(outputPacketGateway); //TODO: is this the best solution ?
        }
        return username;
    }

    /**
     * Asks the user if he wants to change his username. and prompts him to enter his new username.
     * This method is triggered when user already has a username but wants to change it.
     * @return Username chosen by user as String, returns empty String if username is not changed.
     */
    public String readNewUsernameToChange() {
        // TODO: check if username is not empty or null and unittest or if username entered is the same as current username
        return inputReader.readInputWithPrompt(
                new StringBuilder()
                        .append("You opened the username changer  with the command CHANGEUSERNAME, your current username is: ")
                        .append(getUsername())
                        .append(".\n Press enter to keep this one. Otherwise enter your new username below and click Enter.")
                        .toString());
    }

    /**
     * get new username from changeUsername() and send the new chosen Username to the Server.
     */
    public void changeAndSendNewUsername(OutputPacketGateway outputPacketGateway) {
        String username = readNewUsernameToChange();

        try {
            outputPacketGateway.sendPacket(new Packet(Command.SET_USERNAME, username, ContentType.STRING)); //TODO: sanitize username
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to send username to server!");
            changeAndSendNewUsername(outputPacketGateway);
        }
    }
}
