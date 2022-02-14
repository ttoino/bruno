package pt.toino.bruno;

import pt.toino.bruno.game.Card;

public interface BrunoUserInterface {

    void gameStarted();

    void nextTurn(int index);

    void playedCard(int index, Card card);
    void drewCards(int index, int amount, Card[] cards);

}
