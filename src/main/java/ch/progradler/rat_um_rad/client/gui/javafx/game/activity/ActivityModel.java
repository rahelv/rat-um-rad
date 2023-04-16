package ch.progradler.rat_um_rad.client.gui.javafx.game.activity;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Activity: Username + ausgeführte Aktivität
 * TODO: wo werden Activities generiert ?
 */
public class ActivityModel {
    ObservableList<String> latestActivities;

    public void ActivityModel() {
        this.latestActivities = FXCollections.observableArrayList();
    }

    public void updateLatestActivities(List<String> activitiesList) {
        this.latestActivities.clear();
        for (String activity : activitiesList) {
            this.latestActivities.add(activity);
        }
    }

    public ObservableList getLatestActivities() {
        return latestActivities;
    }
}
