package pt.toino.bruno.net.server;

import io.netty.buffer.ByteBuf;

public class PlayerExitServerMessage extends BrunoServerMessage {
    public static final char ID = 'e';

    public int userIndex;

    public PlayerExitServerMessage(ByteBuf bytes) {
        userIndex = bytes.readByte();
    }

    public PlayerExitServerMessage(int i) {
        userIndex = i;
    }

    public void encode(ByteBuf bytes) {
        bytes.writeByte(ID);
        bytes.writeByte(userIndex);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[" + ID + "] PlayerExitServerMessage: User " + userIndex + " leaves the game;");

        return s.toString();
    }
}
