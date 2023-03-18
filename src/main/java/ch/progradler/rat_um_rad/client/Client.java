package ch.progradler.rat_um_rad.client;

import ch.progradler.rat_um_rad.client.command_line.CommandLineHandler;
import ch.progradler.rat_um_rad.client.command_line.InputReader;
import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.client.presenter.CommandLinePresenter;
import ch.progradler.rat_um_rad.client.presenter.PackagePresenter;
import ch.progradler.rat_um_rad.client.protocol.ServerInputListener;
import ch.progradler.rat_um_rad.client.protocol.ServerOutput;
import ch.progradler.rat_um_rad.client.protocol.ServerResponseHandler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
    public void start(String host, int port) {
        System.out.format("Starting Client on %s %d\n", host, port);
        try {
            Socket socket = new Socket(host, port);
            ServerOutput serverOutput = new ServerOutput(socket);
            startCommandHandler(serverOutput, host);
            startServerListener(socket);
        } catch (ConnectException e) {
            e.printStackTrace();
            System.out.println("Failed to connect socket. Is the server running on the same port?");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to connect socket");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCommandHandler(ServerOutput serverOutput, String host) {
        InputReader inputReader = new InputReader();
        CommandLineHandler commandLineHandler = new CommandLineHandler(inputReader, serverOutput, host);
        new Thread(commandLineHandler::startListening).start();
    }

    private void startServerListener(Socket socket) {
        PackagePresenter presenter = new CommandLinePresenter();
        ServerInputPacketGateway inputPacketGateway = new ServerResponseHandler(presenter);
        ServerInputListener listener = new ServerInputListener(socket, inputPacketGateway);
        Thread t = new Thread(listener);
        t.start();
    }
}

//TODO: stop all threads when logging out