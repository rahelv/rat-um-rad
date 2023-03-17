package ch.progradler.rat_um_rad.client.command_line;

import ch.progradler.rat_um_rad.client.protocol.ServerOutputSocket;
import ch.progradler.rat_um_rad.client.utils.ComputerInfo;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.IOException;

public class CommandHandler {
    public static final String ANSWER_NO = "no";

    final CommandReader commandReader;
    final ServerOutputSocket serverOutputSocket;
    final ComputerInfo computerInfo;
    boolean quit = false;

    public CommandHandler(CommandReader commandReader, ServerOutputSocket serverOutputSocket, String host) {
        this.commandReader = commandReader;
        this.serverOutputSocket = serverOutputSocket;
        this.computerInfo = new ComputerInfo(host);
    }

    public void startListening() {
        String username = requestAndSendUsername(); // TODO: what if username changes?
        listenToCommands(username);
    }

    private String requestUsername() {
        String suggestedUsername = computerInfo.getHostName();
        String answerToSuggestedUsername = commandReader.readInputWithPrompt(
                "The username suggested for you is: " +
                        suggestedUsername +
                        ".\nIf you want to change it, enter your new username now and click the Enter key.\n" +
                        "If you do not want to change it,\nenter \"" + ANSWER_NO +
                        "\" and click the Enter key.");

        if (answerToSuggestedUsername.equals(ANSWER_NO)) {
            return suggestedUsername;
        } else {
            return answerToSuggestedUsername;
        }
    }

    private String requestAndSendUsername() {
        String username = requestUsername();

        try {
            serverOutputSocket.sendObject(username); //TODO: sanitize username
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
        String message = commandReader.readInputWithPrompt("Enter your message: ");
        // TODO: handle command properly
        if (message.toLowerCase().contains("quit")) {
            quit = true;
            return;
        }

        Packet packet = new Packet(Command.SEND_ALL, username, message);
        try {
            serverOutputSocket.sendPacket(packet);
        } catch (IOException e) {
            System.out.println("Failed to send command to server!");
            e.printStackTrace();
        }
    }
}
