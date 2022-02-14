package pt.toino.bruno;

import com.dosse.upnp.UPnP;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import pt.toino.bruno.console.ConsoleUserInterface;
import pt.toino.bruno.game.client.ClientGame;
import pt.toino.bruno.game.user.LobbyUser;
import pt.toino.bruno.game.user.MainUser;
import pt.toino.bruno.gui.JFXInterface;
import pt.toino.bruno.net.NetUser;
import pt.toino.bruno.net.client.*;
import pt.toino.bruno.net.server.*;
import pt.toino.bruno.util.Debug;
import pt.toino.bruno.util.Firebase;
import pt.toino.websocketnet.WebSocketNet;
import pt.toino.websocketnet.WebSocketNetHandler;

import java.util.Arrays;
import java.util.Collections;

public class Bruno implements WebSocketNetHandler<BrunoServerMessage, BrunoClientMessage, NetUser> {
    public static final int    PORT        = 35353;
    public static final String VERSION     = "V1";

    //TODO
    private int nextId = 0;

    public final BrunoUserInterface ui;

    public final WebSocketNet<BrunoServerMessage, BrunoClientMessage, NetUser> net;
    public BrunoServer server;
    public BrunoClient client;
    public MainUser user;

    public Bruno(BrunoUserInterface ui) {
        this.ui = ui;
    }

    {
        net = new WebSocketNet<>(this);

        Runtime.getRuntime().addShutdownHook(new Thread(this::dispose));
    }

    public void dispose() {
        net.closeClient();
        net.stopServer();
    }

    public void setUser(String uid, String token, String refreshToken) {
        user = new MainUser(token, refreshToken, uid);
    }

    //region Network
    @Override
    public BrunoServerMessage decodeServerMessage(ByteBuf bytes) {
        return switch (bytes.readByte()) {
            case 'c' -> new PlayCardServerMessage(bytes);
            case 'd' -> new DrawCardsServerMessage(bytes);
            case 'e' -> new PlayerExitServerMessage(bytes);
            case 'f' -> new StartingCardsServerMessage(bytes);
            case 'g' -> new StartGameServerMessage(bytes);
            case 'l' -> new LobbyInfoServerMessage();
            case 'n' -> new NextTurnServerMessage(bytes);
            case 'p' -> new PlayerInfoServerMessage(bytes);
            case 's' -> new InfoSyncServerMessage();
            case 'w' -> new WildColorServerMessage(bytes);
            default -> null;
        };
    }

    @Override
    public BrunoClientMessage decodeClientMessage(ByteBuf bytes) {
        return switch (bytes.readByte()) {
            case 'c' -> new PlayCardClientMessage(bytes);
            case 'd' -> new DrawCardsClientMessage();
            case 'h' -> new HoldCardClientMessage();
            case 'w' -> new WildColorClientMessage(bytes);
            default -> null;
        };
    }

    @Override
    public void onServerMessage(BrunoServerMessage serverMessage) {
        Debug.println("Server message: " + serverMessage.toString());

        if (serverMessage instanceof PlayCardServerMessage) {
            PlayCardServerMessage message = (PlayCardServerMessage) serverMessage;

            client.game.playCard(message.userIndex, message.card);
            ui.playedCard(message.userIndex, message.card);

        } else if (serverMessage instanceof DrawCardsServerMessage) {
            DrawCardsServerMessage message = (DrawCardsServerMessage) serverMessage;

            client.game.drawCards(message.userIndex, message.cardAmount, message.cards);
            ui.drewCards(message.userIndex, message.cardAmount, message.cards);

        } else if (serverMessage instanceof PlayerExitServerMessage) {
            client.lobby.removeUser(((PlayerExitServerMessage) serverMessage).userIndex);

        } else if (serverMessage instanceof StartGameServerMessage) {
            StartGameServerMessage startGame = (StartGameServerMessage) serverMessage;

            LobbyUser main = null;
            for (LobbyUser u : client.lobby.users.values()) {
                if (u.uid.equals(user.uid)) {
                    main = u;
                    break;
                }
            }

            client.game = new ClientGame(client.lobby.users.values().toArray(new LobbyUser[0]), main, startGame.startingHand);

        } else if (serverMessage instanceof StartingCardsServerMessage) {
            StartingCardsServerMessage message = (StartingCardsServerMessage) serverMessage;

            Collections.addAll(client.game.discard, message.cards);

            ui.gameStarted();

        } else if (serverMessage instanceof NextTurnServerMessage) {
            NextTurnServerMessage message = (NextTurnServerMessage) serverMessage;

            client.game.nextTurn(message.userIndex);
            ui.nextTurn(message.userIndex);

        } else if (serverMessage instanceof LobbyInfoServerMessage) {

        } else if (serverMessage instanceof PlayerInfoServerMessage) {
            PlayerInfoServerMessage info = (PlayerInfoServerMessage) serverMessage;

            client.lobby.addUser(info.userIndex, new LobbyUser(info.userIndex, info.uid));

        } else if (serverMessage instanceof InfoSyncServerMessage) {
            //TODO
        } else if (serverMessage instanceof WildColorServerMessage) {
            WildColorServerMessage message = (WildColorServerMessage) serverMessage;

            client.game.wildColor(message.color);
        }
    }

    @Override
    public void onClientMessage(NetUser user, BrunoClientMessage clientMessage) {
        //TODO


        if (clientMessage instanceof PlayCardClientMessage) {
            PlayCardClientMessage message = (PlayCardClientMessage) clientMessage;

            server.game.playCard(user.id, message.card);
        } else if (clientMessage instanceof DrawCardsClientMessage) {
            server.game.drawCards(user.id);
        } else if (clientMessage instanceof HoldCardClientMessage) {
            server.game.holdCard(user.id);
        } else if (clientMessage instanceof WildColorClientMessage) {
            WildColorClientMessage message = (WildColorClientMessage) clientMessage;

            server.game.wildColor(user.id, message.color);
        }
    }

    @Override
    public NetUser onUserConnect(Channel channel, WebSocketServerProtocolHandler.HandshakeComplete handshakeComplete) {
        HttpHeaders headers = handshakeComplete.requestHeaders();

        Debug.println(headers);
        Debug.println(channel.remoteAddress());

        if (!headers.containsValue("Uno", VERSION, true)) {
            channel.writeAndFlush(new CloseWebSocketFrame());
            return null;
        }

        if (!headers.contains("User")) {
            channel.writeAndFlush(new CloseWebSocketFrame());
            return null;
        }

        NetUser user = new NetUser(nextId++, channel, headers.get("User"));

        net.sendServerMessage(new PlayerInfoServerMessage(user.id, user.uid));
        net.sendServerMessage(user, new PlayerInfoServerMessage(user.id, user.uid));

        for (NetUser u : net.getUsers()) {
            net.sendServerMessage(user, new PlayerInfoServerMessage(u.id, u.uid));
        }

        net.sendServerMessage(user, new LobbyInfoServerMessage());

        return user;
    }

    @Override
    public void onUserDisconnect(NetUser user) {
        net.sendServerMessage(new PlayerExitServerMessage(user.id));
    }

    @Override
    public void onConnectToServer() {
        client = new BrunoClient();
    }

    @Override
    public void onDisconnectFromServer() {
        client = null;
    }

    @Override
    public void onServerStart() {
        server = new BrunoServer(this);

        UPnP.openPortTCP(PORT);

        String roomCode = Firebase.createLobby(user.token, UPnP.getExternalIP(), "");
        System.out.println("Room code: " + roomCode);

        server.roomCode = roomCode;
    }

    @Override
    public void onServerStop() {
        server = null;
        Firebase.closeLobby(user.token);
        UPnP.closePortTCP(PORT);
    }

    @Override
    public HttpHeaders createClientHttpHeaders() {
        HttpHeaders headers = new DefaultHttpHeaders();

        headers.add("Uno", VERSION);
        headers.add("User", user.uid);

        return headers;
    }
    //endregion

    public static void main(String[] args) {
        if (args.length == 0) args = new String[]{"ui"};

        Debug.debug = Arrays.asList(args).contains("debug");

        if (args[0].equals("ui")) JFXInterface.launch(JFXInterface.class);
        else new ConsoleUserInterface();
    }

}
