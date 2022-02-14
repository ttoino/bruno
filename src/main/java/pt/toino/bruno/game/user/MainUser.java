package pt.toino.bruno.game.user;

public class MainUser extends BrunoUser {
    public final String token, refresh;

    public MainUser(String token, String refresh, String id) {
        super(id);
        this.token = token;
        this.refresh = refresh;
    }
}
