package ch.progradler.rat_um_rad.client.command_line;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.client.utils.ComputerInfo;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.util.UsernameValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.regex.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

public class UsernameHandler {
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String PROPERTY_NAME_USERNAME = "username";

    private final ComputerInfo computerInfo;
    private final InputReader inputReader;
    private String username;
    private UsernameValidator usernameValidator;

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
        this.usernameValidator = new UsernameValidator();
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
     * @return Username chosen by user as String
     */
    public String chooseUsername() {
        String suggestedUsername = computerInfo.getSystemUsername();
        String answerToSuggestedUsername = inputReader.readInputWithPrompt(
                new StringBuilder()
                        .append( "The username suggested for you is: ")
                        .append(suggestedUsername)
                        .append("\nPress enter to confirm. Otherwise enter your new username below and click Enter.")
                        .append("\nUsername Rules: 5-30 characters. only letters, digits and underscores allowed. first char must be a letter!")
                        .append("\nTo change your username in the future, type CHANGEUSERNAME and press Enter")
                        .toString());
        // TODO: check if username is not empty or null and unittest
        if (answerToSuggestedUsername.equals("")) {
            return suggestedUsername;
        } else {
            return answerToSuggestedUsername;
        }
    }

    /**
     * choose Username @see chooseUsername(), and send this chosen Username to Server
     * @return username which was chosen by server
     */
    public String chooseAndSendUsername(OutputPacketGateway outputPacketGateway) {
        String username = chooseUsername();
        while(!usernameValidator.isUsernameValid(username)) {
            username = reenterUsernameAfterInValidUsernameEntered();
        }

        try {
            outputPacketGateway.sendPacket(new Packet(Command.NEW_USER, username, ContentType.STRING));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.warn("Failed to send username to server!"); //TODO: choose appropriate logger levels for all logs
            return chooseAndSendUsername(outputPacketGateway);
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
                        .append(".\nPress enter to keep this one. Otherwise enter your new username below and click Enter.")
                        .append("\nUsername Rules: 5-30 characters. only letters, digits and underscores allowed. first char must be a letter!")
                        .toString());
    }

    /**
     * Prompts the user to reenter a valid username after the first entered username was false.
     * @return entered username as string.
     */
    public String reenterUsernameAfterInValidUsernameEntered() {
        // TODO: check if username is not empty or null and unittest or if username entered is the same as current username
        return inputReader.readInputWithPrompt(
                new StringBuilder()
                        .append("The username you entered isn't valid! Please try again.")
                        .append("\nUsername Rules: 5-30 characters. only letters, digits and underscores allowed. first char must be a letter!")
                        .toString());
    }

    /**
     * get new username from changeUsername() and send the new chosen Username to the Server.
     */
    public void changeAndSendNewUsername(OutputPacketGateway outputPacketGateway) {
        String chosenUsername = readNewUsernameToChange();
        while(!usernameValidator.isUsernameValid(chosenUsername)) {
            chosenUsername = reenterUsernameAfterInValidUsernameEntered();
        }

        if(chosenUsername == this.username) {
            System.out.println("Your username already is: " + chosenUsername); //TODO: implement using presenter 
            return;
        }

        try {
            outputPacketGateway.sendPacket(new Packet(Command.SET_USERNAME, chosenUsername, ContentType.STRING)); //TODO: sanitize username
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.warn("Failed to send username to server!");
            changeAndSendNewUsername(outputPacketGateway);
        }
    }
}
