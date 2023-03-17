package ch.progradler.rat_um_rad.client;

import ch.progradler.rat_um_rad.client.command_line.CommandHandler;
import ch.progradler.rat_um_rad.client.command_line.CommandReader;
import ch.progradler.rat_um_rad.client.protocol.ServerListenerThread;
import ch.progradler.rat_um_rad.client.protocol.ServerOutputSocket;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
    public void start(String host, int port) {
        System.out.format("Starting Client on %s %d\n", host, port);
        try {
            Socket socket = new Socket(host, port);
            ServerOutputSocket serverOutputSocket = new ServerOutputSocket(socket);
            startCommandHandler(serverOutputSocket, host);
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

    private void startCommandHandler(ServerOutputSocket serverOutputSocket, String host) {
        CommandReader commandReader = new CommandReader();
        CommandHandler commandHandler = new CommandHandler(commandReader, serverOutputSocket, host);
        commandHandler.startListening();
    }

    private void startServerListener(Socket socket) {
        ServerListenerThread listener = new ServerListenerThread(socket);
        Thread t = new Thread(listener);
        t.start();
    }
}

//TODO: stop all threads when logging out