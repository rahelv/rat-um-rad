package ch.progradler.rat_um_rad.client.gui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ChangeUsernameDialog extends JDialog {
    JLabel labelGameName = new JLabel("Rat um Rad");
    JTextField usernameField = new JTextField();
    JButton quitButton = new JButton("quit");
    JButton connectButton = new JButton("connect");
    Box verticalBox = Box.createVerticalBox();
    Box horizontalBox = Box.createHorizontalBox();

    public ChangeUsernameDialog() {
        setTitle("Change Username Dialog");

        labelGameName.setFont(new Font("Arial",Font.BOLD,50));
        labelGameName.setForeground(Color.BLACK);

        usernameField.addActionListener(new UsernameFieldActionListener());
        usernameField.addFocusListener(new UsernameFieldFocusListener());

        quitButton.addActionListener(new ButtonListener());
        connectButton.addActionListener(new ButtonListener());

        verticalBox.add(Box.createVerticalStrut(90));
        verticalBox.add(labelGameName);
        verticalBox.add(usernameField);
        horizontalBox.add(Box.createHorizontalStrut(100));
        horizontalBox.add(quitButton);
        horizontalBox.add(connectButton);

        verticalBox.add(horizontalBox);

        add(verticalBox);
        pack();

        //setBounds((screenWidth-WIDTH)/2,(screenHeight-HEIGHT)/2,WIDTH,HEIGHT);//frame will be shown in the middle of screen
        setResizable(false);

        setVisible(true);
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


}
