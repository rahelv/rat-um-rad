package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.highScore;

import ch.progradler.rat_um_rad.client.utils.listeners.ControllerChangeListener;
import ch.progradler.rat_um_rad.shared.models.Highscore;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class HighScoreModel {
    private final ControllerChangeListener<?> listener;
    private ObservableList<Highscore> highScoreList;

    public HighScoreModel(ControllerChangeListener<?> listener) {
        this.listener = listener;
        this.highScoreList = FXCollections.observableArrayList();
    }
    public ObservableList<Highscore> getHighScoreList(){
        return highScoreList;
    }

    public void setHighScoreList(List<Highscore> highScoreList) {
        this.highScoreList = FXCollections.observableArrayList(highScoreList);
    }

    public ControllerChangeListener<?> getListener() {
        return listener;
    }
}
