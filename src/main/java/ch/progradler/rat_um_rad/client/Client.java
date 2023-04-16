package ch.progradler.rat_um_rad.client;

import ch.progradler.rat_um_rad.client.command_line.presenter.CommandLinePresenter;
import ch.progradler.rat_um_rad.client.command_line.presenter.PackagePresenter;
import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.gateway.OutputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.client.gui.javafx.GUI;
import ch.progradler.rat_um_rad.client.protocol.ServerInputListener;
import ch.progradler.rat_um_rad.client.protocol.ServerOutput;
import ch.progradler.rat_um_rad.client.protocol.ServerResponseHandler;
import ch.progradler.rat_um_rad.client.protocol.pingpong.ClientPingPongRunner;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.coder.PacketCoder;
import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class Client {
    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * starts the client and creates a socket which tries connecting to the server on the specified host and port.
     *
     * @param host     : ip of the server
     * @param port     : port of the server socket
     * @param username
     */
    public void start(String host, int port, String username) { //TODO: handle initial username
        System.out.format("Starting Client on %s %d\n", host, port);

        Coder<Packet> packetCoder = PacketCoder.defaultPacketCoder();
        try {
            Socket socket = new Socket(host, port);
            ServerOutput serverOutput = new ServerOutput(socket, packetCoder);

            OutputPacketGatewaySingleton.setOutputPacketGateway(serverOutput);

            ClientPingPongRunner clientPingPongRunner = startClientPingPong(serverOutput);
            startServerListener(socket, packetCoder, clientPingPongRunner);

            Application.launch(GUI.class); //TODO: how to pass userService to this class
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ConnectException) {
                LOGGER.error("Failed to connect socket. Is the server running on the same port?");
            }
            if (e instanceof IOException) {
                LOGGER.error("Failed to connect socket");
            }
        }
    }

    /**
     * starts the Client Ping-Pong Runner (Thread)
     *
     * @param serverOutput
     * @return
     */
    private ClientPingPongRunner startClientPingPong(ServerOutput serverOutput) {
        ClientPingPongRunner clientPingPongRunner = new ClientPingPongRunner(serverOutput);
        Thread clientPingPongThread = new Thread(clientPingPongRunner);
        clientPingPongThread.start();
        return clientPingPongRunner;
    }

    /**
     * starts the ServerListener in a new thread, which listens to input from server.
     *
     * @param socket
     * @param packetCoder
     * @param clientPingPongRunner
     */
    private void startServerListener(Socket socket,
                                     Coder<Packet> packetCoder,
                                     ClientPingPongRunner clientPingPongRunner) {
        PackagePresenter presenter = new CommandLinePresenter();
        ServerInputPacketGateway inputPacketGateway = new ServerResponseHandler(presenter, clientPingPongRunner);
        ServerInputListener listener = new ServerInputListener(socket, inputPacketGateway, packetCoder);

        InputPacketGatewaySingleton.setInputPacketGateway(inputPacketGateway);

        Thread t = new Thread(listener);
        t.start();
    }
}

//TODO: stop all threads when logging out