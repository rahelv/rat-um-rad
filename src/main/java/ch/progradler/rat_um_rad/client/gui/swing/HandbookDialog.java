package ch.progradler.rat_um_rad.client.gui.swing;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class HandbookDialog extends JDialog {
    private JTextPane textPane;
    private JScrollPane scrollPane;

    public HandbookDialog() {

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setContentType("text/html");

        try {
            URL url= HandbookDialog.class.getClassLoader().getResource("handbuch.htm");
            textPane.setPage(url); //TODO: format handbook page correctly and update text
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            textPane.setText("<html>Page not found.</html>");
        }

        scrollPane = new JScrollPane(textPane);
        add(scrollPane, BorderLayout.CENTER);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setSize(400, 600); //(Toolkit.getDefaultToolkit().getScreenSize());
        setVisible(true);
    }
}
