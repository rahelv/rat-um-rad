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

public class ActivityController implements Initializable, IListener<String> {
    private ActivityModel activityModel;
    @FXML
    private ListView activitiesListView;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(this);

        this.activityModel = new ActivityModel();
        this.activitiesListView.setItems(activityModel.latestActivities);
    }

    @Override
    public void serverResponseReceived(String activity) {
        Platform.runLater(() -> {
            activityModel.addActivity(activity);
        });
    }
}
