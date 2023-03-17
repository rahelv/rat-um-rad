package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.shared.models.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerOutputSocket {
    final Socket socket;
    final ObjectOutputStream out;


    public ServerOutputSocket(Socket socket) throws Exception {
        this.socket = socket;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Failed to init OutputSocket");
        }
    }

    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
    }

    public void sendObject(Object obj) throws IOException {
        out.writeObject(obj);
    }
}
