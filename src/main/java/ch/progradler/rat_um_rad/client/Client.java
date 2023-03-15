package ch.progradler.rat_um_rad.client;

import ch.progradler.rat_um_rad.shared.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public void start() {
        try {
            Socket socket = new Socket("localhost", 8090);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            Scanner scanner = new Scanner(System.in);

            //SetUsername TODO: handle in own method
            System.out.println("Please insert your username: ");
            String username = scanner.nextLine();
            out.writeObject(username); //TODO: sanitize username

            ListenerThread listener = new ListenerThread(socket);
            Thread t = new Thread(listener);
            t.start();

            while (true) {
                System.out.println("Enter your message: ");
                String msg = scanner.nextLine();
                Message m = new Message(msg, username);
                out.writeObject(m);

                //TODO: implement QUIT case
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//TODO: stop all threads when logging out