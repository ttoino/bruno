package pt.toino.bruno.gui.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;

import java.io.IOException;

public class BrunoButton extends Button {
    public BrunoButton() {
        FXMLLoader fxmlLoader = new FXMLLoader(BrunoButton.class.getResource("button.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
