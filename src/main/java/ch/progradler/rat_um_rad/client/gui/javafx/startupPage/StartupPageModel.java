package ch.progradler.rat_um_rad.client.gui.javafx.startupPage;

import ch.progradler.rat_um_rad.client.utils.listeners.ControllerChangeListener;

public class StartupPageModel {
    private final ControllerChangeListener<?> listener;

    public StartupPageModel(ControllerChangeListener<?> listener) {
        this.listener = listener;
    }

    public ControllerChangeListener<?> getListener() {
        return listener;
    }
}
