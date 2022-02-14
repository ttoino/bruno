package pt.toino.bruno.gui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import pt.toino.bruno.Bruno;
import pt.toino.bruno.BrunoUserInterface;
import pt.toino.bruno.game.Card;

public class JFXInterface extends Application implements BrunoUserInterface {
    private Bruno bruno;
    private Scale scale;
    private Stage stage;
    private Parent root;

    @Override
    public void init() throws Exception {
        super.init();

        bruno = new Bruno(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        root = FXMLLoader.load(JFXInterface.class.getResource("auth.fxml"));

        stage.setTitle("Bruno");

        Group group = new Group(root);
        group.setAutoSizeChildren(false);

        final Scene scene = new Scene(group);
        var styleSheets = scene.getStylesheets();
        styleSheets.add("https://fonts.googleapis.com/css?family=Roboto:300,400,500&display=swap");
        styleSheets.add("pt/toino/bruno/gui/styles.css");
        stage.setScene(scene);

        scale = new Scale();
        group.getTransforms().add(scale);

        stage.widthProperty().addListener(onResize);
        stage.heightProperty().addListener(onResize);

        stage.setWidth(960);
        stage.setHeight(540);
        stage.show();
    }

    private final ChangeListener<Number> onResize = (observable, oldValue, newValue) -> {
        double s = stage.getHeight() / 360d;

        scale.setX(s);
        scale.setY(s);
        root.resize(stage.getWidth()/s, 360);

        System.out.println(stage.getWidth() + " " + stage.getWidth()/s);
    };

    @Override
    public void gameStarted() {

    }

    @Override
    public void nextTurn(int index) {

    }

    @Override
    public void playedCard(int index, Card card) {

    }

    @Override
    public void drewCards(int index, int amount, Card[] cards) {

    }
}
