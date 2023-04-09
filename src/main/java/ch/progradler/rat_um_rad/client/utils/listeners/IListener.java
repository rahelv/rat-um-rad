package ch.progradler.rat_um_rad.client.utils.listeners;

public interface IListener<T> {
    void serverResponseReceived(T content);
}
