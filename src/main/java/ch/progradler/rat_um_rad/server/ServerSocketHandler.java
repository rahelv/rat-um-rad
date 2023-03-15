package ch.progradler.rat_um_rad.server;

import ch.progradler.rat_um_rad.shared.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerSocketHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream in; //TODO: implement using inputStream
    private ObjectOutputStream out; //TODO: implement using own serialization
    private ConnectionPool pool; //TODO: this is here so the ServerSocketHandle can access the Pool, i think it would be cleaner to access it from outside of the class.
    private String username;

    public ServerSocketHandler(Socket socket, ConnectionPool pool) {
        this.socket = socket;
        this.pool = pool;
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace(); //TODO: error management
        }

    }

    @Override
    public void run() {
        try {
            //expects the username first
            username = (String) in.readObject(); //TODO: Sanitize input

            while (true) {
                Message message = (Message) in.readObject(); //TODO: in.read() and then use own serialization method
                System.out.println(message.getMessageAndUsername()); //TODO: only needed for testing, later server won't print messages...

                //TODO: implement QUIT scenario (with break)
                //important to remove client from pool so server doesn't crash
                //TODO: first, broadcast messages
                pool.broadcast(message); //sends message to all clients
                //TODO: later, implement network protocol and chose action accordingly

                out.writeObject(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToClient(Message msg) {
        try {
            out.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientName() {
        return username;
    }
}
