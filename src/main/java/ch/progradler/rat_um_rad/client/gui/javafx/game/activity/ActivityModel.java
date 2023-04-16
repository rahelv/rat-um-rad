package ch.progradler.rat_um_rad.client.gui.javafx.game.activity;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Activity: Username + ausgeführte Aktivität
 * TODO: wo werden Activities generiert ?
 */
public class ActivityModel {
    ObservableList<String> latestActivities;

    public void ActivityModel() {
        this.latestActivities = FXCollections.observableArrayList();
    }

    public void addActivity(String activity) {
        //this.latestActivities.add(activity);
    }
}
