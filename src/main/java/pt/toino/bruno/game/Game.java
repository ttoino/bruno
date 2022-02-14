package pt.toino.bruno.game;

import pt.toino.bruno.net.NetUser;
import pt.toino.bruno.net.client.BrunoClientMessage;
import pt.toino.bruno.net.server.*;
import pt.toino.websocketnet.WebSocketNet;

import java.util.*;

public class Game {
    private final WebSocketNet<BrunoServerMessage, BrunoClientMessage, NetUser> net;

    public TurnState turnState = TurnState.START;

    public final Deque<Card> deck = new LinkedList<>();
    public final Deque<Card> discard = new LinkedList<>();
    public Card lastCardPlayed;

    public int currentPlayer = 0;
    public Player[] players;
    public boolean clockwise = true;

    public Game(WebSocketNet<BrunoServerMessage, BrunoClientMessage, NetUser> net) {
        this.net = net;

        populateDeck();
        shuffleDeck();

        Set<NetUser> users = net.getUsers();

        players = new Player[users.size()];

        int i = 0;
        for (NetUser u : users) {
            Player player = new Player(u);

            for (int j = 0; j < 7; j++) {
                player.hand.add(drawCard());
            }

            net.sendServerMessage(player.user, new StartGameServerMessage(player.hand.toArray(new Card[0])));

            players[i] = player;
            i++;
        }

        lastCardPlayed = drawCard();
        discard.addFirst(lastCardPlayed);

        //TODO
        net.sendServerMessage(new StartingCardsServerMessage(1, lastCardPlayed));
    }

    //PUBLIC METHODS
    public void playCard(int id, Card card) {
        if (!canBePlayed(card) || !isCurrentPlayer(id) || turnState == TurnState.PLAYED) return;

        List<Card> hand = players[currentPlayer].hand;
        if (!hand.contains(card)) return;
        if (turnState == TurnState.DREW && !card.equals(hand.get(hand.size()-1))) return;

        hand.remove(card);
        net.sendServerMessage(new PlayCardServerMessage(id, card));

        discard.addFirst(card);
        lastCardPlayed = card.clone();
        turnState = TurnState.PLAYED;

        if (specialCardEffects()) nextTurn();
    }

    public void drawCards(int id) {
        if (!isCurrentPlayer(id) || turnState != TurnState.START) return;

        Player player = players[currentPlayer];

        List<Card> cards = new ArrayList<>();

        Card card;
        do {
            card = drawCard();
            player.hand.add(card);
            cards.add(card);
        } while (!canBePlayed(card));

        int amount = cards.size();
        net.sendServerMessage(new DrawCardsServerMessage(id, amount));
        net.sendServerMessage(player.user, new DrawCardsServerMessage(-1, amount, cards.toArray(new Card[0])));
        turnState = TurnState.DREW;
    }

    public void holdCard(int id) {
        if (!isCurrentPlayer(id) || turnState != TurnState.DREW) return;

        nextTurn();
    }

    public void wildColor(int id, Card.CardColor color) {
        if (!isCurrentPlayer(id) || turnState != TurnState.PLAYED || color == Card.CardColor.WILD) return;

        lastCardPlayed.cardColor = color;

        if (specialCardEffects()) nextTurn();
    }

    //PRIVATE HELPER METHODS
    private boolean specialCardEffects() {
        if (lastCardPlayed.cardColor == Card.CardColor.WILD) return false;

        switch (lastCardPlayed.value) {
            case PLUS_TWO:
                nextPlayer();
                draw(2);
                break;
            case PLUS_FOUR:
                nextPlayer();
                draw(4);
                break;
            case REVERSE:
                clockwise = !clockwise;
                break;
            case SKIP:
                nextPlayer();
                break;
        }

        return true;
    }

    private void draw(int amount) {
        Player player = players[currentPlayer];

        Card[] cards = new Card[amount];

        for (int i = 0; i < amount; i++) {
            cards[i] = drawCard();
            player.hand.add(cards[i]);
        }

        net.sendServerMessage(new DrawCardsServerMessage(player.user.id, amount));
        net.sendServerMessage(player.user, new DrawCardsServerMessage(-1, amount, cards));
    }

    private Card drawCard() {
        Card card = deck.removeFirst();

        if (deck.size() <= 0) {
            refillDeck();
            shuffleDeck();
        }

        return card;
    }

    private void refillDeck() {
        deck.addAll(discard);
        discard.removeAll(discard);
    }

    private boolean canBePlayed(Card card) {
        return card != null && (card.cardColor == lastCardPlayed.cardColor || card.value == lastCardPlayed.value || card.cardColor == Card.CardColor.WILD);
    }

    private void nextTurn() {
        nextPlayer();

        net.sendServerMessage(new NextTurnServerMessage(players[currentPlayer].user.id));
        turnState = TurnState.START;
    }

    private void nextPlayer() {
        if (clockwise) ++currentPlayer;
        else --currentPlayer;

        if (currentPlayer < 0) currentPlayer = players.length-1;
        if (currentPlayer >= players.length) currentPlayer = 0;
    }

    private boolean isCurrentPlayer(int id) {
        return players[currentPlayer].user.id == id;
    }

    private void shuffleDeck() {
        Collections.shuffle((LinkedList<Card>) deck);
    }

    private void populateDeck() {
        for (int i = 0; i < 108; i++) {
            Card.CardColor c = Card.CardColor.WILD;
            Card.CardValue v = Card.CardValue.WILD;
            int j = i;

            if (i < 25) {
                c = Card.CardColor.RED;
            } else if (i < 50) {
                c = Card.CardColor.GREEN;
                j -= 25;
            } else if (i < 75) {
                c = Card.CardColor.YELLOW;
                j -= 50;
            } else if (i < 100) {
                c = Card.CardColor.BLUE;
                j -= 75;
            }

            if (j < 25) {
                int k = (int) Math.floor(j*.5f);
                v = Card.CardValue.values()[k];
            } else if (j >= 104) {
                v = Card.CardValue.PLUS_FOUR;
            }

            deck.add(new Card(c, v));
        }
    }

    public enum TurnState {
        START, DREW, PLAYED
    }
}
