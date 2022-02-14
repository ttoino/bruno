package pt.toino.bruno.game;

import pt.toino.bruno.net.NetUser;

import java.util.ArrayList;
import java.util.List;

public class Player {
    public final List<Card> hand;
    public final NetUser user;

    public Player(NetUser user) {
        this.user = user;
        hand = new ArrayList<>();
    }
}
