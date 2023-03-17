package ch.progradler.rat_um_rad.client.command_line;

import ch.progradler.rat_um_rad.client.protocol.ServerOutputSocket;
import ch.progradler.rat_um_rad.shared.models.Message;

import java.io.IOException;

public class CommandHandler {
    final CommandReader commandReader;
    final ServerOutputSocket serverOutputSocket;
    boolean quit = false;

    public CommandHandler(CommandReader commandReader, ServerOutputSocket serverOutputSocket) {
        this.commandReader = commandReader;
        this.serverOutputSocket = serverOutputSocket;
    }

    public void startListening() {
        String username = requestAndSendUsername(); // TODO: what if username changes?
        listenToCommands(username);
    }

    private String requestAndSendUsername() {
        String username = commandReader.readInputWithPrompt("Please insert your username: ");
        // TODO: validate username

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
        String command = commandReader.readInputWithPrompt("Enter your message: ");
         // TODO: handle command properly
        if(command.toLowerCase().contains("quit")){
            quit = true;
            return;
        }

        Message message = new Message(command, username);
        try {
            serverOutputSocket.sendMessage(message);
        } catch (IOException e) {
            System.out.println("Failed to send command to server!");
            e.printStackTrace();
        }
    }
}
