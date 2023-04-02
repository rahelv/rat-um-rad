package ch.progradler.rat_um_rad.client.gui.javafx;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class RatUmRadMenuBar extends MenuBar {
    private Menu menu;
    private MenuItem handbookMenuItem;

    public RatUmRadMenuBar() {
        this.menu = new Menu("Help");
        handbookMenuItem = new MenuItem("Handbook");
        menu.getItems().add(handbookMenuItem);
        handbookMenuItem.setOnAction(e -> {
            HandbookPopup handbookPopup = new HandbookPopup(); //TODO: only open once
        });
        this.getMenus().add(menu);
    }
}
