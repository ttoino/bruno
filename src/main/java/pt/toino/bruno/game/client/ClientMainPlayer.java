package pt.toino.bruno.game.client;

import pt.toino.bruno.game.Card;
import pt.toino.bruno.game.user.LobbyUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClientMainPlayer {
    public List<Card> hand;
    public LobbyUser user;

    public ClientMainPlayer(LobbyUser user, Card[] startingHand) {
        this.user = user;
        hand = new ArrayList<>();
        Collections.addAll(hand, startingHand);
        sortCards();
    }

    public void addCards(Card[] cards) {
        Collections.addAll(hand, cards);
        sortCards();
    }

    private void sortCards() {
        hand.sort(Comparator.comparingInt(Card::toByte));
    }
}
