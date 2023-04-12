package ch.progradler.rat_um_rad.client.utils.listeners;

public interface ServerResponseListener<T> {
    void serverResponseReceived(T content);
}
