package ch.progradler.rat_um_rad.client.utils.listeners;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;

public interface IListener {
    void chatMessageReceived(ChatMessage chatMessage);
}
