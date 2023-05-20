package ch.progradler.rat_um_rad.client.gui.javafx.game.playerOverview;

import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class PlayerOverviewModel {
    //TODO: implement "Stapel"
    private final ObservableList<VisiblePlayer> visiblePlayers = FXCollections.observableArrayList();

    public void updatePlayers(Player ownPlayer, List<VisiblePlayer> players) {
        this.visiblePlayers.clear();
        this.visiblePlayers.add(toVisiblePlayer(ownPlayer));
        this.visiblePlayers.addAll(players);
    }

    private VisiblePlayer toVisiblePlayer(Player player) {
        VisiblePlayer visiblePlayer = new VisiblePlayer(player.getName(), player.getColor(), player.getScore(), player.getWheelsRemaining(), player.getPlayingOrder(), "", player.getWheelCards().size(), player.getShortDestinationCards().size());
        return visiblePlayer;
    }

    public ObservableList<VisiblePlayer> getVisiblePlayers() {
        return visiblePlayers;
    }
}
