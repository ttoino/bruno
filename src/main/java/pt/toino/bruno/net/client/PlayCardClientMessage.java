package pt.toino.bruno.net.client;

import io.netty.buffer.ByteBuf;
import pt.toino.bruno.game.Card;

public class PlayCardClientMessage extends BrunoClientMessage {
    public static final char ID = 'c';

    public final Card card;

    public PlayCardClientMessage(ByteBuf bytes) {
        card = new Card(bytes.readByte());
    }

    public PlayCardClientMessage(Card c) {
        card = c;
    }

    public void encode(ByteBuf bytes) {
        bytes.writeByte(ID);
        bytes.writeByte(card.toByte());
    }
}
