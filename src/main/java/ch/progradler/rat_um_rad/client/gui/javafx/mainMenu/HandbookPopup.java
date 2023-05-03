package ch.progradler.rat_um_rad.client.gui.javafx.mainMenu;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Displays the Handbook for the game. Handbook is stored in file handbuch.htm
 */
public class HandbookPopup extends Stage {
    private final WebView webView = new WebView();
    ScrollPane scrollPane = new ScrollPane();
    private WebEngine webEngine;

    public HandbookPopup() {
        setTitle("Rat um Rad - Handbuch");

        webEngine = webView.getEngine();
        scrollPane.setContent(webView);

        try {
            URL url = HandbookPopup.class.getClassLoader().getResource("handbuch.htm");
            webEngine.load(url.toString());
        } catch (RuntimeException e) {
            e.printStackTrace();
            webEngine.loadContent("<html>Page not found</html>", "text/html");
        }

        VBox vBox = new VBox(scrollPane);
        Scene scene = new Scene(vBox, 400, 600);
        setScene(scene);
        show();
        //TODO: implement close button and format page
    }
}
