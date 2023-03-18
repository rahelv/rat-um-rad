package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.*;
import java.net.Socket;

/**
 * Listens to messages from server
 */
public class ServerInputListener implements Runnable {
    private Socket socket;
    private ObjectInputStream in; //TODO: implement using inputStream
    private InputStream ins;//rui

    private final ServerInputPacketGateway inputPacketGateway;

    public ServerInputListener(Socket socket, ServerInputPacketGateway inputPacketGateway) {
        this.socket = socket;
        this.inputPacketGateway = inputPacketGateway;
        try {
            ins = socket.getInputStream();//rui
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            // TODO: handle?
            e.printStackTrace();
        }
    }

    public String streamToString(InputStream is){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] b = new byte[1024];
        int len;

        String s = null;
        try {
            //write direct ,what inputStream reads,into byteArray through ByteArrayOutputStream
            while((len=is.read(b)) != -1){
                bos.write(b,0,len);
            }
            byte[] byteArray = bos.toByteArray();
            s = byteArray.toString();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
    @Override
    public void run() {
        try {
            while (true) { //so it keeps listening
                String listenedStr = streamToString(ins);//rui
                String[] splits = listenedStr.split("|");//rui
                Packet response1 = new Packet(splits[0], splits[1], splits[2]);//rui

                Packet response = (Packet) in.readObject();
                inputPacketGateway.handleResponse(response);

                //TODO: implement QUIT command and other commands
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

