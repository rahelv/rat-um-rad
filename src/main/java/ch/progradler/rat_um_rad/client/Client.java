package ch.progradler.rat_um_rad.client;

import ch.progradler.rat_um_rad.client.command_line.CommandLineHandler;
import ch.progradler.rat_um_rad.client.command_line.InputReader;
import ch.progradler.rat_um_rad.client.command_line.UsernameHandler;
import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.client.presenter.CommandLinePresenter;
import ch.progradler.rat_um_rad.client.presenter.PackagePresenter;
import ch.progradler.rat_um_rad.client.protocol.ServerInputListener;
import ch.progradler.rat_um_rad.client.protocol.ServerOutput;
import ch.progradler.rat_um_rad.client.protocol.ServerResponseHandler;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.coder.ChatMessageCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.coder.PacketCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.UsernameChangeCoder;
import ch.progradler.rat_um_rad.client.protocol.pingpong.ClientPingPongRunner;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class Client  {
    public static final Logger LOGGER = LogManager.getLogger();
    /** starts the client and creates a socket which tries connecting to the server on the specified host and port.
     * @param host: ip of the server
     * @param port: port of the server socket
     */
    public void start(String host, int port) {
        System.out.format("Starting Client on %s %d\n", host, port);

        Coder<Packet> packetCoder = getPacketCoder();
        try {
            Socket socket = new Socket(host, port);
            ServerOutput serverOutput = new ServerOutput(socket, packetCoder);
            UsernameHandler usernameHandler = new UsernameHandler();
            ClientPingPongRunner clientPingPongRunner = startClientPingPong(serverOutput);
            startCommandHandler(serverOutput, host, usernameHandler);
            startServerListener(socket, packetCoder, clientPingPongRunner, usernameHandler);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ConnectException) {
                LOGGER.error("Failed to connect socket. Is the server running on the same port?");
            }
            if(e instanceof IOException) {
                LOGGER.error("Failed to connect socket");
            }
        }
    }

    /**
     * starts the Client Ping Pong Runner (Thread)
     * @param serverOutput
     * @return
     */
    private ClientPingPongRunner startClientPingPong(ServerOutput serverOutput) {
        ClientPingPongRunner clientPingPongRunner = new ClientPingPongRunner(serverOutput);
        Thread clientPingPongThread = new Thread(clientPingPongRunner);
        clientPingPongThread.start();
        return clientPingPongRunner;
    }

    /** starts the command handler in a new thread.
     * @param serverOutput
     * @param host
     * @param usernameHandler
     */
    private void startCommandHandler(ServerOutput serverOutput, String host, UsernameHandler usernameHandler) {
        InputReader inputReader = new InputReader();
        CommandLineHandler commandLineHandler = new CommandLineHandler(inputReader, serverOutput, host, usernameHandler);
        usernameHandler.addUsernameObserver(commandLineHandler);
        Thread t = new Thread(commandLineHandler);
        t.start();
    }

    /** starts the ServerListener in a new thread, which listens to input from server.
     * @param socket
     * @param packetCoder
     * @param usernameHandler
     */
    private void startServerListener(Socket socket, Coder<Packet> packetCoder, ClientPingPongRunner clientPingPongRunner, UsernameHandler usernameHandler) {
        PackagePresenter presenter = new CommandLinePresenter();
        ServerInputPacketGateway inputPacketGateway = new ServerResponseHandler(presenter, clientPingPongRunner, usernameHandler);
        ServerInputListener listener = new ServerInputListener(socket, inputPacketGateway, packetCoder);
        Thread t = new Thread(listener);
        t.start();
    }

    private static Coder<Packet> getPacketCoder() {
        return new PacketCoder(new ChatMessageCoder(), new UsernameChangeCoder());
    }
}

//TODO: stop all threads when logging out