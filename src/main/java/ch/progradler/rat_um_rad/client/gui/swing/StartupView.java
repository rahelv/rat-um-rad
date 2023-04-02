package ch.progradler.rat_um_rad.client.gui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class StartupView extends JFrame {
    final int WIDTH = 600;
    final int HEIGHT = 400;
    JMenuBar menuBar;

    JLabel labelGameName = new JLabel("Rat um Rad");
    JTextField usernameField = new JTextField();
    JButton quitButton = new JButton("quit");
    JButton connectButton = new JButton("connect");

    Box verticalBox = Box.createVerticalBox();
    Box horizontalBox = Box.createHorizontalBox();

    public StartupView() {
        init();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        // Erzeugung eines Objektes der Klasse JMenu
        JMenu menu = new JMenu("Help");
        // Erzeugung eines Objektes der Klasse JMenuItem
        JMenuItem item = new JMenuItem("Handbook");
        // Wir fügen das JMenuItem unserem JMenu hinzu
        menu.add(item);
        // Menü wird der Menüleiste hinzugefügt
        menuBar.add(menu);
        return menuBar;
    }

    public void init(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        this.menuBar = createMenuBar();
        this.setJMenuBar(menuBar);

        labelGameName.setFont(new Font("Arial",Font.BOLD,50));
        labelGameName.setForeground(Color.BLACK);

        usernameField.addActionListener(new UsernameFieldActionListener());
        usernameField.addFocusListener(new UsernameFieldFocusListener());

        //.addActionListener(new ButtonListener());
        quitButton.addActionListener(new ButtonListener());
        connectButton.addActionListener(new ButtonListener());

        verticalBox.add(Box.createVerticalStrut(90));
        //verticalBox.add(handbookButton);
        verticalBox.add(labelGameName);
        verticalBox.add(usernameField);
        horizontalBox.add(Box.createHorizontalStrut(100));
        horizontalBox.add(quitButton);
        horizontalBox.add(connectButton);
       // horizontalBox.add(handbookButton);

        verticalBox.add(horizontalBox);

        this.setLayout(new FlowLayout());
        this.add(verticalBox);
        this.pack();
        this.setTitle("Rat um Rad");

        this.setBounds((screenWidth-WIDTH)/2,(screenHeight-HEIGHT)/2,WIDTH,HEIGHT);//frame will be shown in the middle of screen
        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

    }

    private class UsernameFieldActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            //TODO: send username to server

        }
    }
    private class UsernameFieldFocusListener implements FocusListener {
        private String hintText = "please enter username";

        @Override
        public void focusGained(FocusEvent e) {
            usernameField.setText(hintText);
        }

        @Override
        public void focusLost(FocusEvent e) {
            String suggestName = "suggest name XXX";//TODO: here to get suggest name from server

            usernameField.setForeground(Color.GRAY);
            usernameField.setText(suggestName);
        }
    }
    private class ButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton)e.getSource();
            String name = button.getName();
            switch(name){
                case "quitButton":
                    //TODO : quit
                    break;
                case "connectButton":
                    //TODO : connect to server
                    // MessageDialog with responses from server
                    break;
                case "handbookButton":
                    //TODO : MessageDialog will be shown with game rules
                    break;
            }

        }
    }

    public static void main(String[] args) {
        new StartupView();
    }
}
