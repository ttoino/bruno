package pt.toino.bruno.net.client;

import io.netty.buffer.ByteBuf;
import pt.toino.bruno.game.Card;

public class WildColorClientMessage extends BrunoClientMessage {
    public static final char ID = 'w';

    public final Card.CardColor color;

    public WildColorClientMessage(ByteBuf bytes) {
        color = Card.CardColor.values()[bytes.readByte()];
    }

    public WildColorClientMessage(Card.CardColor c) {
        color = c;
    }

    public void encode(ByteBuf bytes) {
        bytes.writeByte(ID);
        bytes.writeByte(color.num);
    }
}
