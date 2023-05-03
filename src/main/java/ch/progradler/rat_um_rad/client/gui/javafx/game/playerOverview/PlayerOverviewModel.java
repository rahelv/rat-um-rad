package ch.progradler.rat_um_rad.client.gui.javafx.game.playerOverview;

import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class PlayerOverviewModel {
    //TODO: implement "Stapel"
    private final ObservableList<VisiblePlayer> visiblePlayers = FXCollections.observableArrayList();

    public void updatePlayers(List<VisiblePlayer> players) {
        this.visiblePlayers.clear();
        this.visiblePlayers.addAll(players);
    }

    public ObservableList<VisiblePlayer> getVisiblePlayers() {
        return visiblePlayers;
    }
}
