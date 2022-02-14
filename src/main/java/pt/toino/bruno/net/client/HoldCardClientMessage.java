package pt.toino.bruno.net.client;

import io.netty.buffer.ByteBuf;

public class HoldCardClientMessage extends BrunoClientMessage {
    public static final char ID = 'h';

    public void encode(ByteBuf bytes) {
        bytes.writeByte(ID);
    }
}
