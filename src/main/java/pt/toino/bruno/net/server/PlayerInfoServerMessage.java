package pt.toino.bruno.net.server;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class PlayerInfoServerMessage extends BrunoServerMessage {
    public static final char ID = 'p';

    public int userIndex;
    public String uid;

    public PlayerInfoServerMessage(ByteBuf bytes) {
        userIndex = bytes.readByte();
        uid = bytes.toString(StandardCharsets.UTF_8);
    }

    public PlayerInfoServerMessage(int i, String s) {
        userIndex = i;
        uid = s;
    }

    public void encode(ByteBuf bytes) {
        bytes.writeByte(ID);
        bytes.writeByte(userIndex);
        bytes.writeCharSequence(uid, StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[" + ID + "] PlayerInfoServerMessage: User " + userIndex + " with uid " + uid + " joined the game;");

        return s.toString();
    }
}
