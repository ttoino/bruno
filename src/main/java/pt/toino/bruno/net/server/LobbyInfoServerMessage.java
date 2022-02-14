package pt.toino.bruno.net.server;

import io.netty.buffer.ByteBuf;

//TODO
public class LobbyInfoServerMessage extends BrunoServerMessage {
    public static final char ID = 'l';

    public void encode(ByteBuf bytes) {
        bytes.writeByte(ID);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[" + ID + "] LobbyInfoServerMessage;");

        return s.toString();
    }
}
