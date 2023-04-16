package ch.progradler.rat_um_rad.client.gui.javafx.game.activity;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Activitieslist.fxml. Activities that happened in the game are displayed in a list.
 */
public class ActivityController extends AnchorPane {
    private ActivityModel activityModel;
    @FXML
    private ListView activitiesListView;

    public ActivityController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/game/ActivitiesList.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void initData(ActivityModel activityModel) {
        this.activityModel = activityModel;
        this.activitiesListView.setItems(this.activityModel.getLatestActivities()); //bind ListView to latestActivities in the activityModel
    }

    public void updateActitivites(List<String> activities) {
        this.activityModel.updateLatestActivities(activities);
    }
}
