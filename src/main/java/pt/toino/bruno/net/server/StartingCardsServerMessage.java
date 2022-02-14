package pt.toino.bruno.net.server;

import io.netty.buffer.ByteBuf;
import pt.toino.bruno.game.Card;

public class StartingCardsServerMessage extends BrunoServerMessage {
    public static final char ID = 'f';

    public int cardAmount;
    public Card[] cards;

    public StartingCardsServerMessage(ByteBuf bytes) {
        cardAmount = bytes.readByte();
        cards = new Card[cardAmount];

        for (int i = 0; i < cardAmount; i++) {
            cards[i] = new Card(bytes.readByte());
        }
    }

    public StartingCardsServerMessage(int a, Card... c) {
        cardAmount = a;
        cards = c;
    }

    public void encode(ByteBuf bytes) {
        bytes.writeByte(ID);
        bytes.writeByte(cardAmount);

        for (Card c : cards) {
            bytes.writeByte(c.toByte());
        }
    }

//    @Override
//    public String toString() {
//        return "[" + ID + "] StartingCardsServerMessage: Game starts with " + cardAmount + " cards: " + Card.cardsToString(Arrays.asList(cards), false);
//    }
}
