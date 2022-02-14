package pt.toino.bruno.game.client;

import pt.toino.bruno.game.Card;
import pt.toino.bruno.game.user.LobbyUser;

import java.util.Deque;
import java.util.LinkedList;

//TODO
public class ClientGame {
    public ClientPlayer[] players;
    public Deque<Card> discard = new LinkedList<>();
    public ClientMainPlayer mainPlayer;
    public int currentPlayer = 0;
    public boolean clockwise;

    public ClientGame(LobbyUser[] users, LobbyUser mainUser, Card[] startingHand) {
        mainPlayer = new ClientMainPlayer(mainUser, startingHand);

        players = new ClientPlayer[users.length];

        for (int i = 0; i < users.length; i++) {
            players[i] = new ClientPlayer(startingHand.length, users[i]);
        }
    }

    public void playCard(int userIndex, Card card) {
        var player = players[userIndex];
        player.handSize--;
        discard.addFirst(card);

        if (player.user.equals(mainPlayer.user)) {
            mainPlayer.hand.remove(card);
        }
    }

    public void drawCards(int userIndex, int cardAmount, Card[] cards) {
        if (userIndex < 0) {
            mainPlayer.addCards(cards);
        } else {
            players[userIndex].handSize += cardAmount;
        }
    }

    public void nextTurn(int index) {
        currentPlayer = index;
    }

    public void wildColor(Card.CardColor cardColor) {
        discard.peekFirst().cardColor = cardColor;
    }
}
