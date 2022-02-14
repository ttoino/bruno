package pt.toino.bruno.net.server;

import io.netty.buffer.ByteBuf;
import pt.toino.bruno.game.Card;

public class PlayCardServerMessage extends BrunoServerMessage {
    public static final char ID = 'c';

    public int userIndex;
    public Card card;

    public PlayCardServerMessage(ByteBuf bytes) {
        userIndex = bytes.readByte();
        card = new Card(bytes.readByte());
    }

    public PlayCardServerMessage(int i, Card c) {
        userIndex = i;
        card = c;
    }

    public void encode(ByteBuf bytes) {
        bytes.writeByte(ID);
        bytes.writeByte(userIndex);
        bytes.writeByte(card.toByte());
    }

//    @Override
//    public String toString() {
//        return "[" + ID + "] PlayCardServerMessage: User " + userIndex + " plays " + card.toFancyString() + ";";
//    }
}
