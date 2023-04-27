package ch.progradler.rat_um_rad.client.utils.listeners;

import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;

public interface ServerResponseListener<T> {
    void serverResponseReceived(T content);

    ServerCommand forCommand();
}
