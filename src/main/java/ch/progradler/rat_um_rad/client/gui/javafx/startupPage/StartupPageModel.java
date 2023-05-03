package ch.progradler.rat_um_rad.client.gui.javafx.startupPage;

import ch.progradler.rat_um_rad.client.utils.listeners.ControllerChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class StartupPageModel {
    private final ControllerChangeListener<?> listener;
    private ObservableList<String> allOnlinePlayersList;

    public StartupPageModel(ControllerChangeListener<?> listener) {
        this.listener = listener;
        this.allOnlinePlayersList = FXCollections.observableArrayList();
    }

    public ControllerChangeListener<?> getListener() {
        return listener;
    }

    public void addPlayersToList(List<String> allOnlinePlayers) {
        this.allOnlinePlayersList.clear();
        this.allOnlinePlayersList.addAll(allOnlinePlayers);
    }

    public Integer getOnlinePlayersCount() {
        return allOnlinePlayersList.size();
    }

    public String getOnlinePlayersListAsString() {
        String list = "";
        for (String string : allOnlinePlayersList) {
            list += string + ", ";
        }
        return list;
    }


}
