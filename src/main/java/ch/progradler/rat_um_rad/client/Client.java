package ch.progradler.rat_um_rad.client;

import ch.progradler.rat_um_rad.client.command_line.CommandLineHandler;
import ch.progradler.rat_um_rad.client.command_line.InputReader;
import ch.progradler.rat_um_rad.client.command_line.UsernameHandler;
import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
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

public class Client {
    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * starts the client and creates a socket which tries connecting to the server on the specified host and port.
     *
     * @param host: ip of the server
     * @param port: port of the server socket
     */
    public void start(String host, int port) {
        System.out.format("Starting Client on %s %d\n", host, port);

        Coder<Packet> packetCoder = getPacketCoder();
        try {
            Socket socket = new Socket(host, port);
            ServerOutput serverOutput = new ServerOutput(socket, packetCoder);

            OutputPacketGatewaySingleton.setOutputPacketGateway(serverOutput);

            IUserService userService = new UserService(serverOutput);
            ClientPingPongRunner clientPingPongRunner = startClientPingPong(serverOutput);
            //startCommandHandler(serverOutput, host, userService);
            startServerListener(socket, packetCoder, clientPingPongRunner, userService, serverOutput);

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
     * starts the Client Ping Pong Runner (Thread)
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
     * @param serverOutput
     * @param host
     * @param userService

    private void startCommandHandler(ServerOutput serverOutput, String host, UserService userService) {
    InputReader inputReader = new InputReader();
    CommandLineHandler commandLineHandler = new CommandLineHandler(inputReader, serverOutput, host, userService);
    Thread t = new Thread(commandLineHandler);
    t.start();
    }  */

    /**
     * starts the ServerListener in a new thread, which listens to input from server.
     *
     * @param socket
     * @param packetCoder
     * @param userService
     */
    private void startServerListener(Socket socket,
                                     Coder<Packet> packetCoder,
                                     ClientPingPongRunner clientPingPongRunner,
                                     IUserService userService,
                                     ServerOutput serverOutput) {
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
                new GameBaseCoder(gameMapCoder));
    }
}

//TODO: stop all threads when logging out