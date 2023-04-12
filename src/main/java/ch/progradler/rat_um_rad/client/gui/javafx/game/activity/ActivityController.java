package ch.progradler.rat_um_rad.client.gui.javafx.game.activity;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom.ChatRoomModel;
import ch.progradler.rat_um_rad.client.services.UserService;
import ch.progradler.rat_um_rad.client.utils.listeners.IListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

/**
 * Controller for activitiesList.fxml. Activities that happened in the game are displayed in a list.
 */
public class ActivityController implements Initializable, IListener<String> {
    private ActivityModel activityModel;
    @FXML
    private ListView activitiesListView;

    /**
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(this); //add Listener for ServerInput (ServerReponseHandler)

        this.activityModel = new ActivityModel();
        this.activitiesListView.setItems(activityModel.latestActivities); //bind ListView to latestActivities in the activityModel
    }

    /** Listens for Responses from the ServerResponseHandler
     * @param activity
     */
    @Override
    public void serverResponseReceived(String activity) {
        Platform.runLater(() -> {
            activityModel.addActivity(activity);
        });
    }
}
