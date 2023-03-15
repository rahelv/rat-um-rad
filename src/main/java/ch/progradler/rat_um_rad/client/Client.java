package ch.progradler.rat_um_rad.client;

import ch.progradler.rat_um_rad.client.protocol.ListenerThread;
import ch.progradler.rat_um_rad.shared.models.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public void start(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            Scanner scanner = new Scanner(System.in);

            String username = requestUsername(out, scanner);

            ListenerThread listener = new ListenerThread(socket);
            Thread t = new Thread(listener);
            t.start();

            while (true) {
                readMessages(out, scanner, username);
                //TODO: implement QUIT case
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages(ObjectOutputStream out, Scanner scanner, String username) throws IOException {
        System.out.println("Enter your message: ");
        String msg = scanner.nextLine();
        Message m = new Message(msg, username);
        out.writeObject(m);
    }

    /**
     * TODO: move to different class
     */
    private String requestUsername(ObjectOutputStream out, Scanner scanner) throws IOException {
        // TODO: validate username
        System.out.println("Please insert your username: ");
        String username = scanner.nextLine();
        out.writeObject(username); //TODO: sanitize username
        return username;
    }
}

//TODO: stop all threads when logging out