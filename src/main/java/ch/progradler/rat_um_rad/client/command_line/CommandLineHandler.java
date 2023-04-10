package ch.progradler.rat_um_rad.client.command_line;

import ch.progradler.rat_um_rad.client.Client;
import ch.progradler.rat_um_rad.client.services.IUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import static ch.progradler.rat_um_rad.client.command_line.UsernameHandler.PROPERTY_NAME_USERNAME;

/**
 * Contains business logic by handling user input and possibly sending packet to server.
 */
public class CommandLineHandler implements PropertyChangeListener, Runnable {
    public static final Logger LOGGER = LogManager.getLogger();

    private final InputReader inputReader;
    private final IUserService userService;
    private final UsernameHandler usernameHandler;
    private boolean quit = false;

    public CommandLineHandler(InputReader inputReader, IUserService userService, String host, UsernameHandler usernameHandler) {
        this.inputReader = inputReader;
        this.userService = userService;
        this.usernameHandler = usernameHandler;
    }

    /**
     * starts the CommandLineHandler. Condition: username has to be sent to the server, waits until username is set
     *
     * @see UsernameHandler#addUsernameObserver(PropertyChangeListener)
     */
    @Override
    public void run() {
        usernameHandler.chooseAndSendUsername(userService);
        try {
            synchronized (this) {
                wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        listenToCommands();
    }

    /**
     * Listens for username changes, as CommandLineHandler only runs when user has set username.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     * @see Client (CommandLineHandler observes UsernameHandler)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PROPERTY_NAME_USERNAME)) {
            synchronized (this) {
                notify();
            }
        }
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
            usernameHandler.changeAndSendNewUsername(userService);
        } else { //TODO: add more commands and handle seperately
            try {
                userService.sendBroadCastMessage(message);
            } catch (IOException e) {
                LOGGER.warn("Failed to send command to server!");
                e.printStackTrace();
            }
        }
    }
}
