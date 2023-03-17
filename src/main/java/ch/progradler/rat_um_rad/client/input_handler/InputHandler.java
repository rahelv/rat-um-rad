package ch.progradler.rat_um_rad.client.input_handler;

import ch.progradler.rat_um_rad.client.protocol.ServerOutputSocket;
import ch.progradler.rat_um_rad.client.utils.ComputerInfo;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.IOException;

public class InputHandler {
    public static final String ANSWER_NO = "no";

    final InputReader inputReader;
    final ServerOutputSocket serverOutputSocket;
    final ComputerInfo computerInfo;
    boolean quit = false;

    public InputHandler(InputReader inputReader, ServerOutputSocket serverOutputSocket, String host) {
        this.inputReader = inputReader;
        this.serverOutputSocket = serverOutputSocket;
        this.computerInfo = new ComputerInfo(host);
    }

    public void startListening() {
        String username = requestAndSendUsername(); // TODO: what if username changes?

        while (!quit) {
            readInput(username);
            //TODO: implement QUIT case
        }
        System.out.println("Quit application successfully!");
        System.exit(0);
    }

    private String requestUsername() {
        String suggestedUsername = computerInfo.getHostName();
        String answerToSuggestedUsername = inputReader.readInputWithPrompt(
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

    private void readInput(String username) {
        String message = inputReader.readInputWithPrompt("Enter your message: ");
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
