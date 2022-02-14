package pt.toino.bruno.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class Firebase {
    private static final JSONParser PARSER = new JSONParser();

    private static final String
            FIREBASE_KEY = "AIzaSyC79gBQiswFHUD7ysXTmbZq8UZ8lEMGri4",
            BASE_AUTH_URL = "https://identitytoolkit.googleapis.com/v1/accounts:",
            KEY_AUTH_PARAM = "?key=",
            OP_AUTH = "signInWithPassword",
            OP_CREATE_USER = "signUp",
            OP_CREATE_LOBBY = "createLobby",
            OP_JOIN_LOBBY = "joinLobby",
            OP_CLOSE_LOBBY = "closeLobby",
            STORAGE_BUCKET_URL = "https://firebasestorage.googleapis.com/v0/b/toino-jogos.appspot.com/o/",
            DATABASE_BASE_URL = "https://firestore.googleapis.com/v1/projects/toino-jogos/databases/(default)/documents/",
            FUNCTIONS_BASE_URL = "https://europe-west1-toino-jogos.cloudfunctions.net/";

    public static JSONObject createAccount(String email, String password) {
        try {
            HttpResponse response = Request.Post(BASE_AUTH_URL + OP_CREATE_USER + KEY_AUTH_PARAM + FIREBASE_KEY)
                    .bodyString("{\"email\":\""+email+"\",\"password\":\""+password+"\",\"returnSecureToken\":true}", ContentType.APPLICATION_JSON)
                    .execute().returnResponse();

            Reader s = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));

            return (JSONObject) PARSER.parse(s);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject auth(String email, String password) {
        try {
            HttpResponse response = Request.Post(BASE_AUTH_URL + OP_AUTH + KEY_AUTH_PARAM + FIREBASE_KEY)
                    .bodyString("{\"email\":\""+email+"\",\"password\":\""+password+"\",\"returnSecureToken\":true}", ContentType.APPLICATION_JSON)
                    .execute().returnResponse();

            Reader s = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
            return (JSONObject) PARSER.parse(s);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject uploadAvatar(File file, String uid, String token) {
        try {
            HttpResponse response = Request.Post(STORAGE_BUCKET_URL + uid + "%2Favatar.png")
                    .setHeader("Authorization", "Firebase " + token)
                    .bodyFile(file, ContentType.IMAGE_PNG)
                    .execute().returnResponse();

            Reader s = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
            return (JSONObject) PARSER.parse(s);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUserName(String uid) {
        try {
            HttpResponse response = Request.Get(DATABASE_BASE_URL + "users/" + uid)
                    .execute().returnResponse();

            Reader s = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
            JSONObject j = (JSONObject) PARSER.parse(s);
            return ((JSONObject) ((JSONObject) j.get("fields")).get("userName")).get("stringValue").toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject setUserName(String token, String uid, String userName) {
        try {
            HttpResponse response = Request.Patch(DATABASE_BASE_URL + "users/" + uid)
                    .setHeader("Authorization", "Firebase " + token)
                    .bodyString("{\"fields\": {\"userName\": {\"stringValue\": \"" + userName + "\"}}}", ContentType.APPLICATION_JSON)
                    .execute().returnResponse();

            Reader s = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
            return (JSONObject) PARSER.parse(s);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String createLobby(String token, String ip, String password) {
        try {
            return Request.Post(FUNCTIONS_BASE_URL + OP_CREATE_LOBBY)
                    .setHeader("Authorization", "Bearer " + token)
                    .bodyString("{\"ip\":\""+ip+"\",\"password\":\""+password+"\"}", ContentType.APPLICATION_JSON)
                    .execute().returnContent().asString(StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String joinLobby(String token, String name, String password) {
        try {
            return Request.Post(FUNCTIONS_BASE_URL + OP_JOIN_LOBBY)
                    .setHeader("Authorization", "Bearer " + token)
                    .bodyString("{\"lobbyName\":\""+name+"\",\"password\":\""+password+"\"}", ContentType.APPLICATION_JSON)
                    .execute().returnContent().asString(StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String closeLobby(String token) {
        try {
            return Request.Post(FUNCTIONS_BASE_URL + OP_CLOSE_LOBBY)
                    .setHeader("Authorization", "Bearer " + token)
                    .execute().returnContent().asString(StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getErrorType(JSONObject response) {
        JSONObject error = ((JSONObject) response.get("error"));
        if (error == null)
            return null;
        return error.get("message").toString();
    }

    public static String getAvatarUrl(String uid) {
        return STORAGE_BUCKET_URL + uid + "%2Favatar.png?alt=media";
    }
}
