package pt.toino.bruno.net.server;

import io.netty.buffer.ByteBuf;

//TODO
public class InfoSyncServerMessage extends BrunoServerMessage {
    public static final char ID = 's';

    public void encode(ByteBuf bytes) {
        bytes.writeByte(ID);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[" + ID + "] InfoSyncServerMessage;");

        return s.toString();
    }
}
