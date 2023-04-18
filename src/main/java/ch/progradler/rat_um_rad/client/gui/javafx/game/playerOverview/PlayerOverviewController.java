package ch.progradler.rat_um_rad.client.gui.javafx.game.playerOverview;

import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.List;

/**
 * Controller for custom control Activitieslist.fxml. Activities that happened in the game are displayed in a list.
 */
public class PlayerOverviewController extends AnchorPane {
    private PlayerOverviewModel playerOverviewModel;
    @FXML
    private ListView playerOverviewListView;

    public PlayerOverviewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/game/PlayerOverview.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void initData(PlayerOverviewModel playerOverviewModel) {
        this.playerOverviewModel = playerOverviewModel;
        //this.playerOverviewListView.setItems(this.playerOverviewModel.get()); //bind ListView to latestActivities in the activityModel
    }

    public void updatePlayerOverview(List<VisiblePlayer> players) {
        this.playerOverviewModel.updatePlayers(players);
    }
}
