package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.shared.models.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Listens to messages from server
 * TODO: is not a thread. so why is it called thread?
 */
public class ServerListenerThread implements Runnable {
    private Socket socket;
    private ObjectInputStream in; //TODO: implement using inputStream

    public ServerListenerThread(Socket socket) {
        this.socket = socket;
        try {
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            // TODO: handle?
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) { //so it keeps listening
                Message response = (Message) in.readObject();
                System.out.println(response.getMessageAndUsername());

                //TODO: implement QUIT command and other commands
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
