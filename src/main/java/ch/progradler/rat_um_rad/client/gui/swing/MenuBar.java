package ch.progradler.rat_um_rad.client.gui.swing;

import javax.swing.*;

public class MenuBar extends JMenuBar {
    JMenu menu;
    JMenuItem handBookItem;

    public MenuBar() {
        // Erzeugung eines Objektes der Klasse JMenu
        menu = new JMenu("Help");
        // Erzeugung eines Objektes der Klasse JMenuItem
        handBookItem = new JMenuItem("Handbook");
        handBookItem.addActionListener(e -> new HandbookDialog());
        // Wir fügen das JMenuItem unserem JMenu hinzu
        menu.add(handBookItem);
        // Menü wird der Menüleiste hinzugefügt
        add(menu);
    }
}
