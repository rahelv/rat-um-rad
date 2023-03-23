package ch.progradler.rat_um_rad.client.command_line;

import ch.progradler.rat_um_rad.client.protocol.ServerOutput;
import ch.progradler.rat_um_rad.client.utils.ComputerInfo;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.IOException;

/**
 * Contains business logic by handling user input and possibly sending packet to server.
 */
public class CommandLineHandler {
    private static final String ANSWER_NO = "no";

    private final InputReader inputReader;
    private final ServerOutput serverOutput;
    private final ComputerInfo computerInfo;
    private boolean quit = false;

    public CommandLineHandler(InputReader inputReader, ServerOutput serverOutput, String host) {
        this.inputReader = inputReader;
        this.serverOutput = serverOutput;
        this.computerInfo = new ComputerInfo();
    }

    public void startListening() {
        String username = requestAndSendUsername(); // TODO: what if username changes?
        listenToCommands(username);
    }

    private String chooseUsername() {
        String suggestedUsername = computerInfo.getSystemUsername();
        String answerToSuggestedUsername = inputReader.readInputWithPrompt(
                "The username suggested for you is: " +
                        suggestedUsername +
                        ".\nPress enter to confirm. Otherwise enter your new username below and press enter.");
        // TODO: check if username is not empty or null and unittest
        if (answerToSuggestedUsername.equals("")) {
            return suggestedUsername;
        } else {
            return answerToSuggestedUsername;
        }
    }

    private String requestAndSendUsername() {
        String username = chooseUsername();

        try {
            serverOutput.sendPacket(new Packet(Command.NEW_USER, username, ContentType.USERNAME)); //TODO: sanitize username
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to send username to server!");
            return requestAndSendUsername();
        }
        return username;
    }

    private void listenToCommands(String username) {
        while (!quit) {
            readCommand(username);
            //TODO: implement QUIT case
        }

        System.out.println("Quit application successfully!");
        System.exit(0);
    }

    private void readCommand(String username) {
        String message = inputReader.readInputWithPrompt("Enter your message: ");
        // TODO: handle command properly
        if (message.toLowerCase().contains("quit")) {
            quit = true;
            return;
        }

        Packet packet = new Packet(Command.SEND_CHAT,
                new ChatMessage(username, message),
                ContentType.CHAT_MESSAGE);
        try {
            serverOutput.sendPacket(packet);
        } catch (IOException e) {
            System.out.println("Failed to send command to server!");
            e.printStackTrace();
        }
    }
}
