package pt.toino.bruno.game.client;

import pt.toino.bruno.game.user.LobbyUser;

public class ClientPlayer {
    public int handSize;
    public LobbyUser user;

    public ClientPlayer(int handSize, LobbyUser user) {
        this.handSize = handSize;
        this.user = user;
    }
}
