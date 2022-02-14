package pt.toino.bruno.game.user;


public class LobbyUser extends BrunoUser {
    public int index;

    public LobbyUser(int i, String id) {
        super(id);
        index = i;
    }
}
