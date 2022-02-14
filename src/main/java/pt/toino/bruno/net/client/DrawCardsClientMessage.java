package pt.toino.bruno.net.client;

import io.netty.buffer.ByteBuf;

public class DrawCardsClientMessage extends BrunoClientMessage {
    public static final char ID = 'd';

    public void encode(ByteBuf bytes) {
        bytes.writeByte(ID);
    }
}
