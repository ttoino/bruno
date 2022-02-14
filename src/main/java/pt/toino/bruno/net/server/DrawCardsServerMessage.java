package pt.toino.bruno.net.server;

import io.netty.buffer.ByteBuf;
import pt.toino.bruno.game.Card;

public class DrawCardsServerMessage extends BrunoServerMessage {
    public static final char ID = 'd';

    public int userIndex;
    public int cardAmount;
    public Card[] cards;

    public DrawCardsServerMessage(ByteBuf bytes) {
        userIndex = bytes.readByte();
        cardAmount = bytes.readByte();
        if (userIndex < 0) {
            cards = new Card[cardAmount];

            for (int i = 0; i < cardAmount; i++) {
                cards[i] = new Card(bytes.readByte());
            }
        }
    }

    public DrawCardsServerMessage(int i, int a, Card... c) {
        userIndex = i;
        cardAmount = a;
        cards = c;
    }

    public void encode(ByteBuf bytes) {
        bytes.writeByte(ID);
        bytes.writeByte(userIndex);
        bytes.writeByte(cardAmount);

        for (Card c : cards) {
            bytes.writeByte(c.toByte());
        }
    }

//    @Override
//    public String toString() {
//        if (userIndex == -1)
//            return "[" + ID + "] DrawCardsServerMessage: You draw " + cardAmount + " cards: " + Card.cardsToString(Arrays.asList(cards), false);
//
//        return "[" + ID + "] DrawCardsServerMessage: User " + userIndex + " draws " + cardAmount + " cards;";
//    }
}
