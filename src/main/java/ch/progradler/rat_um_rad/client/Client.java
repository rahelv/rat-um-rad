package ch.progradler.rat_um_rad.client;

import ch.progradler.rat_um_rad.client.command_line.CommandLineHandler;
import ch.progradler.rat_um_rad.client.command_line.InputReader;
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

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;


public class Client {
    public void start(String host, int port) {
        System.out.format("Starting Client on %s %d\n", host, port);

        Coder<Packet> packetCoder = getPacketCoder();
        try {
            Socket socket = new Socket(host, port);
            ServerOutput serverOutput = new ServerOutput(socket, packetCoder);
            startCommandHandler(serverOutput, host);
            startServerListener(socket, packetCoder);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ConnectException) {
                System.out.println("Failed to connect socket. Is the server running on the same port?");
            }
            if(e instanceof IOException) {
                System.out.println("Failed to connect socket");
            }
        }
    }

    private void startCommandHandler(ServerOutput serverOutput, String host) {
        InputReader inputReader = new InputReader();
        CommandLineHandler commandLineHandler = new CommandLineHandler(inputReader, serverOutput, host);
        new Thread(commandLineHandler::startListening).start();
    }

    private void startServerListener(Socket socket, Coder<Packet> packetCoder) {
        PackagePresenter presenter = new CommandLinePresenter();
        ServerInputPacketGateway inputPacketGateway = new ServerResponseHandler(presenter);
        ServerInputListener listener = new ServerInputListener(socket, inputPacketGateway, packetCoder);
        Thread t = new Thread(listener);
        t.start();
    }

    private static Coder<Packet> getPacketCoder() {
        return new PacketCoder(new ChatMessageCoder(), new UsernameChangeCoder());
    }
}

//TODO: stop all threads when logging out