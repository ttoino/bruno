package pt.toino.bruno.net.server;

import io.netty.buffer.ByteBuf;
import pt.toino.bruno.game.Card;

public class WildColorServerMessage extends BrunoServerMessage {
    public static final char ID = 'w';

    public final Card.CardColor color;

    public WildColorServerMessage(ByteBuf bytes) {
        color = Card.CardColor.values()[bytes.readByte()];
    }

    public WildColorServerMessage(Card.CardColor c) {
        color = c;
    }

    public void encode(ByteBuf bytes) {
        bytes.writeByte(ID);
        bytes.writeByte(color.num);
    }
}
