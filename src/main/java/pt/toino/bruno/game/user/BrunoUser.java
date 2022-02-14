package pt.toino.bruno.game.user;

import pt.toino.bruno.util.Firebase;

public class BrunoUser {
    public final String userName, uid, photoUrl;

    public BrunoUser(String id) {
        uid = id;
        userName = Firebase.getUserName(uid);
        photoUrl = Firebase.getAvatarUrl(uid);
    }
}
