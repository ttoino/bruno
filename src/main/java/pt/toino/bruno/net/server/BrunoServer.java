package pt.toino.bruno.net.server;

import pt.toino.bruno.Bruno;
import pt.toino.bruno.game.Game;

public class BrunoServer {
    private final Bruno bruno;

    public String roomCode;
    public Game game;

    public BrunoServer(Bruno bruno) {
        this.bruno = bruno;
    }

    public void startGame() {
        game = new Game(bruno.net);
    }
}
