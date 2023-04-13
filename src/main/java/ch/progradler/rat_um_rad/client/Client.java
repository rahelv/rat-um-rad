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
import ch.progradler.rat_um_rad.client.services.IUserService;
import ch.progradler.rat_um_rad.client.services.UserService;
import ch.progradler.rat_um_rad.client.protocol.pingpong.ClientPingPongRunner;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.coder.*;
import ch.progradler.rat_um_rad.shared.protocol.coder.cards_and_decks.DestinationCardCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.cards_and_decks.WheelCardCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.game.CityCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.game.PointCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.player.PlayerCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.player.VisiblePlayerCoder;
import javafx.application.Application;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class Client {
    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * starts the client and creates a socket which tries connecting to the server on the specified host and port.
     *  @param host : ip of the server
     * @param port : port of the server socket
     * @param username
     */
    public void start(String host, int port, String username) { //TODO: handle initial username
        System.out.format("Starting Client on %s %d\n", host, port);

        Coder<Packet> packetCoder = getPacketCoder();
        try {
            Socket socket = new Socket(host, port);
            ServerOutput serverOutput = new ServerOutput(socket, packetCoder);

            OutputPacketGatewaySingleton.setOutputPacketGateway(serverOutput);

            ClientPingPongRunner clientPingPongRunner = startClientPingPong(serverOutput);
            //            startCommandHandler(userService, host, usernameHandler, username);
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
     * starts the command handler in a new thread.
     *
     * @param userService
     * @param host
     * @param usernameHandler

    private void startCommandHandler(IUserService userService, String host, UsernameHandler usernameHandler, String initialUsername) {
        InputReader inputReader = new InputReader();
        CommandLineHandler commandLineHandler = new CommandLineHandler(inputReader, userService, host, usernameHandler, initialUsername);
        usernameHandler.addUsernameObserver(commandLineHandler);
        Thread t = new Thread(commandLineHandler);
        t.start();
    }*/

    /**
     * starts the ServerListener in a new thread, which listens to input from server.
     *
     * @param socket
     * @param packetCoder
     */
    private void startServerListener(Socket socket,
                                     Coder<Packet> packetCoder,
                                     ClientPingPongRunner clientPingPongRunner ){
        PackagePresenter presenter = new CommandLinePresenter();
        ServerInputPacketGateway inputPacketGateway = new ServerResponseHandler(presenter, clientPingPongRunner);
        ServerInputListener listener = new ServerInputListener(socket, inputPacketGateway, packetCoder);

        InputPacketGatewaySingleton.setInputPacketGateway(inputPacketGateway);

        Thread t = new Thread(listener);
        t.start();
    }

    private static Coder<Packet> getPacketCoder() {
        Coder<GameMap> gameMapCoder = new Coder<>() {
            @Override
            public String encode(GameMap object, int level) {
                return null;
            }

            @Override
            public GameMap decode(String encoded, int level) {
                return null;
            }
        }; // TODO: implement correctly
        return new PacketCoder(new ChatMessageCoder(),
                new UsernameChangeCoder(),
                new GameBaseCoder(gameMapCoder),
                new ClientGameCoder(gameMapCoder, new VisiblePlayerCoder(), new PlayerCoder(new WheelCardCoder(), new DestinationCardCoder(new CityCoder(new PointCoder())))));
    }
}

//TODO: stop all threads when logging out