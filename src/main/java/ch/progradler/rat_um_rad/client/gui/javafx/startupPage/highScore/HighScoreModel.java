package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.highScore;

import ch.progradler.rat_um_rad.client.utils.listeners.ControllerChangeListener;
import ch.progradler.rat_um_rad.shared.models.Highscore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HighScoreModel {
    private final ControllerChangeListener<?> listener;
    private ObservableList<Highscore> highScoreList;

    public HighScoreModel(ControllerChangeListener<?> listener) {
        this.listener = listener;
        this.highScoreList = FXCollections.observableArrayList();
    }

    public ObservableList<Highscore> getHighScoreList() {
        return highScoreList;
    }

    public void setHighScoreList(List<Highscore> highScoreList) {
        List<Highscore> sorted = new ArrayList<>(highScoreList);
        sorted.sort(Comparator.comparingInt(Highscore::getScore));
        // highest score at top
        Collections.reverse(sorted);
        this.highScoreList = FXCollections.observableArrayList(sorted);
    }

    public ControllerChangeListener<?> getListener() {
        return listener;
    }
}
