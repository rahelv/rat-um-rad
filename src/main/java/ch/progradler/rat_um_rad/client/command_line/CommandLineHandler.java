package ch.progradler.rat_um_rad.client.command_line;

import ch.progradler.rat_um_rad.client.controllers.UserController;
import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Contains business logic by handling user input and possibly sending packet to server.
 */
public class CommandLineHandler implements Runnable {
    public static final Logger LOGGER = LogManager.getLogger();

    private final InputReader inputReader;
    private final OutputPacketGateway outputPacketGateway;
    private final UserController userController;
    private boolean quit = false;

    public CommandLineHandler(InputReader inputReader, OutputPacketGateway outputPacketGateway, String host, UserController userController) {
        this.inputReader = inputReader;
        this.outputPacketGateway = outputPacketGateway;
        this.userController = userController;
    }

    /**
     * starts the CommandLineHandler.
     */
    @Override
    public void run() {
        listenToCommands();
    }

    /**
     * continuously listens for User Input (Commands), stops when quit-command is triggered
     */
    private void listenToCommands() {
        LOGGER.debug("Listening to user input commands...");
        while (!quit) {
            readCommand();
            //TODO: implement QUIT case
        }

        System.out.println("Quit application successfully!");
        System.exit(0);
    }

    /**
     * Users InputReader to get User Commands. Analyses the incoming commands and distributes them to responsible classes.
     */
    private void readCommand() {
        // TODO: unittest and create enum for user commands
        String message = inputReader.readInputWithPrompt("Enter your message: ");
        // TODO: handle command properly
        if (message.toLowerCase().contains("quit")) {
            quit = true;
            return;
        } else if (message.toLowerCase().contains("changeusername")) {
            userController.changeAndSendNewUsername(outputPacketGateway);
        } else { //TODO: add more commands and handle seperately
            Packet packet = new Packet(Command.SEND_CHAT,
                    message,
                    ContentType.STRING);
            try {
                outputPacketGateway.sendPacket(packet);
            } catch (IOException e) {
                LOGGER.warn("Failed to send command to server!");
                e.printStackTrace();
            }
        }
    }
}
