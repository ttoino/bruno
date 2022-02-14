package pt.toino.bruno.net.client;

import pt.toino.bruno.game.user.LobbyUser;

import java.util.HashMap;
import java.util.Map;

public class Lobby {
    public Map<Integer, LobbyUser> users = new HashMap<>();

    public void addUser(int i, LobbyUser user) {
        users.put(i, user);
    }

    public void removeUser(int i) {
        users.remove(i);
    }
}
