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

    Box verticalBox = Box.createVerticalBox();
    Box horizontalBox = Box.createHorizontalBox();

    public StartupView() {
        init();
    }

    public void init(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        this.menuBar = new MenuBar();
        this.setJMenuBar(menuBar);

        labelGameName.setFont(new Font("Arial",Font.BOLD,50));
        labelGameName.setForeground(Color.BLACK);

        verticalBox.add(Box.createVerticalStrut(90));
        verticalBox.add(labelGameName);
        horizontalBox.add(Box.createHorizontalStrut(100));

        verticalBox.add(horizontalBox);

        this.setLayout(new FlowLayout());
        this.add(verticalBox);
        this.pack();
        this.setTitle("Rat um Rad");

        this.setBounds((screenWidth-WIDTH)/2,(screenHeight-HEIGHT)/2,WIDTH,HEIGHT);//frame will be shown in the middle of screen
        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        ChangeUsernameDialog changeUsernameDialog = new ChangeUsernameDialog();
    }

    public static void main(String[] args) {
        new StartupView();
    }
}
