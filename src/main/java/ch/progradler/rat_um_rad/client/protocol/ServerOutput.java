package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.*;
import java.net.Socket;

/**
 * Responsible for sending packets to server via socket stream.
 */
public class ServerOutput implements OutputPacketGateway {
    final Socket socket;
    final ObjectOutputStream out;
    final OutputStream out1;


    public ServerOutput(Socket socket) throws Exception {
        this.socket = socket;
        try {
            out1 =  socket.getOutputStream();
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Failed to init OutputSocket");
        }
    }

    @Override
    public  void sendPacket(Packet packet) throws IOException {
        out.writeObject(packet);

        String sendStr = packet.encode();//rui
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out1));//rui
        bufferedWriter.write(sendStr);//rui

    }

    public void sendObject(Object obj) throws IOException {
        out.writeObject(obj);
    }
}
