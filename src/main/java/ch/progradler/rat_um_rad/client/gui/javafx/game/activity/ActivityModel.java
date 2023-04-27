package ch.progradler.rat_um_rad.client.gui.javafx.game.activity;

import ch.progradler.rat_um_rad.shared.models.Activity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Activity: Username + ausgeführte Aktivität
 * TODO: wo werden Activities generiert ?
 */
public class ActivityModel {
    ObservableList<String> latestActivities = FXCollections.observableArrayList();

    public void updateLatestActivities(List<Activity> activitiesList) {
        this.latestActivities.clear();
        for (Activity activity : activitiesList) {
            this.latestActivities.add(activity.getUsername() + " did " + activity.getCommand());
        }
    }

    public ObservableList getLatestActivities() {
        return latestActivities;
    }
}
