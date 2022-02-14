package pt.toino.bruno.net.server;

import io.netty.buffer.ByteBuf;

public class NextTurnServerMessage extends BrunoServerMessage {
    public static final char ID = 'n';

    public final int userIndex;

    public NextTurnServerMessage(ByteBuf bytes) {
        userIndex = bytes.readByte();
    }

    public NextTurnServerMessage(int i) {
        userIndex = i;
    }

    @Override
    public void encode(ByteBuf bytes) {
        bytes.writeByte(ID);
        bytes.writeByte(userIndex);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[" + ID + "] NextTurnServerMessage: User " + userIndex + "'s turn;");

        return s.toString();
    }
}
