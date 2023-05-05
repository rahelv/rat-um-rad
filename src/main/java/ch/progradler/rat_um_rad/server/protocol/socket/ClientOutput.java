package ch.progradler.rat_um_rad.server.protocol.socket;

import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.util.StreamUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Allows easy output to a client with certain {@link ClientOutput#ipAddress} via the {@link ClientOutput#out} stream.
 */
public class ClientOutput {
    private final String ipAddress;
    private final Coder<Packet<ServerCommand>> packetCoder;
    private Socket socket;
    private OutputStream out; //TODO: implement using own serialization
    private PrintWriter printWriter;
    private ConnectionPoolInfo connectionPoolInfo;
    private static final Logger LOGGER = LogManager.getLogger();

    public ClientOutput(Socket socket, String ipAddress, Coder<Packet<ServerCommand>> packetCoder, ConnectionPoolInfo connectionPoolInfo) {
        this.socket = socket;
        this.packetCoder = packetCoder;
        this.ipAddress = ipAddress;
        this.connectionPoolInfo = connectionPoolInfo;
        try {
            out = socket.getOutputStream();
            printWriter = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace(); //TODO: error management
        }
    }

    public void sendPacketToClient(Packet<ServerCommand> packet) {
        Packet notMeantToBeNull = packet;
        String sendStr = packetCoder.encode(packet, 0);
        if (sendStr == null) {
            LOGGER.info("Thread of player with ipAddress "+connectionPoolInfo.getIpOfThread(Thread.currentThread())+" sends string equals null in method sendPacket(). Command was "+notMeantToBeNull.getCommand()+", ContentType was "+ notMeantToBeNull.getContentType()+" and content was "+ notMeantToBeNull.getContent());
            //printed in console so that developers see that
            System.out.println("\033[0;31m" + "Thread of player with ipAddress "+connectionPoolInfo.getIpOfThread(Thread.currentThread())+" sends string equals null in method sendPacket(). Command was "+notMeantToBeNull.getCommand()+", ContentType was "+ notMeantToBeNull.getContentType()+" and content was "+ notMeantToBeNull.getContent() + "\033[0m");
            return;
        }

        StreamUtils.writeStringToStream(sendStr, printWriter);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Closes output stream.
     */
    public void close() throws IOException {
        out.close();
    }
}
