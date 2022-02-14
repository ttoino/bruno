package pt.toino.bruno.gui.components;

import javafx.animation.*;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;

public class BrunoTextField extends StackPane {

    Timeline borderAnimation;
    BooleanProperty floatProperty;

    @FXML
    public Label label;

    @FXML
    public TextField textfield;

    public BrunoTextField() {
        floatProperty = new BooleanPropertyBase() {
            @Override
            public Object getBean() {
                return BrunoTextField.this;
            }

            @Override
            public String getName() {
                return "float";
            }
        };

        FXMLLoader fxmlLoader = new FXMLLoader(BrunoTextField.class.getResource("textfield.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        System.out.println(textfield.getBorder());

        FloatProperty border = new FloatPropertyBase() {
            @Override
            public Object getBean() {
                return BrunoTextField.this;
            }

            @Override
            public String getName() {
                return "border-width";
            }
        };

        borderAnimation = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(border, 1)),
                new KeyFrame(Duration.millis(300), new KeyValue(border, 2))
        );
        borderAnimation.play();

        textfield.styleProperty().bind(Bindings.concat("-fx-border-width:", border, "px;"));

        BooleanBinding binding = Bindings.isNotEmpty(textfield.textProperty()).or(textfield.focusedProperty());
        floatProperty.bind(binding);
    }

}
