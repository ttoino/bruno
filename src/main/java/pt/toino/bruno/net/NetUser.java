package pt.toino.bruno.net;

import io.netty.channel.Channel;
import pt.toino.websocketnet.User;

public class NetUser extends User {
    public String uid;

    public NetUser(int id, Channel channel, String uid) {
        super(id, channel);
        this.uid = uid;
    }
}
