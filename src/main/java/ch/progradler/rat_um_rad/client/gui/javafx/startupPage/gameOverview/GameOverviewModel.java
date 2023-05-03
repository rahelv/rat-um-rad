package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.gameOverview;

import ch.progradler.rat_um_rad.client.utils.listeners.ControllerChangeListener;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class GameOverviewModel {
    private final ControllerChangeListener<?> listener;
    private ObservableList<GameBase> ongoingGameList;
    private ObservableList<GameBase> finishedGameList;

    public GameOverviewModel(ControllerChangeListener<?> listener) {
        this.listener = listener;
        this.ongoingGameList = FXCollections.observableArrayList();
        this.finishedGameList = FXCollections.observableArrayList();
    }

    public ObservableList<GameBase> getOngoingGameList() {
        return ongoingGameList;
    }

    public void setOngoingGameList(List<GameBase> ongoingGameList) {
        this.ongoingGameList = FXCollections.observableArrayList(ongoingGameList);
    }

    public ObservableList<GameBase> getFinishedGameList() {
        return finishedGameList;
    }

    public void setFinishedGameList(List<GameBase> finishedGameList) {
        this.finishedGameList = FXCollections.observableArrayList(finishedGameList);
    }

    public ControllerChangeListener<?> getListener() {
        return listener;
    }
}
