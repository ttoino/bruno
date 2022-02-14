package pt.toino.bruno.net.server;

import io.netty.buffer.ByteBuf;
import pt.toino.bruno.game.Card;

public class StartGameServerMessage extends BrunoServerMessage {
    public static final char ID = 'g';

    public Card[] startingHand;

    public StartGameServerMessage(Card[] cards) {
        startingHand = cards;
    }

    public StartGameServerMessage(ByteBuf bytes) {
        startingHand = new Card[7];
        for (int i = 0; i < startingHand.length; i++)
            startingHand[i] = new Card(bytes.readByte());
    }

    public void encode(ByteBuf bytes) {
        bytes.writeByte(ID);

        for (Card c : startingHand) {
            bytes.writeByte(c.toByte());
        }
    }

//    @Override
//    public String toString() {
//        return "[" + ID + "] StartGameServerMessage: " + Card.cardsToString(Arrays.asList(startingHand), true);
//    }
}
