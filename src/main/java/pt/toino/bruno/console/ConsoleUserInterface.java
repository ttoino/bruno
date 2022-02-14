package pt.toino.bruno.console;

import org.json.simple.JSONObject;
import pt.toino.bruno.Bruno;
import pt.toino.bruno.BrunoUserInterface;
import pt.toino.bruno.game.Card;
import pt.toino.bruno.game.Game;
import pt.toino.bruno.game.client.ClientGame;
import pt.toino.bruno.net.client.DrawCardsClientMessage;
import pt.toino.bruno.net.client.HoldCardClientMessage;
import pt.toino.bruno.net.client.PlayCardClientMessage;
import pt.toino.bruno.net.client.WildColorClientMessage;
import pt.toino.bruno.util.Firebase;

import java.util.Arrays;
import java.util.Scanner;

public class ConsoleUserInterface implements BrunoUserInterface {
    public boolean color;

    private final Bruno bruno;
    private Game serverGame;
    private ClientGame clientGame;

    public ConsoleUserInterface() {
        bruno = new Bruno(this);

        System.out.println("\u001b[31m---- Bruno ----\u001b[0m");
        System.out.println("Use \u001B[36mhelp\u001B[0m to see available commands");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                input(scanner.nextLine().split(" "));
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Not enough arguments?");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void input(String... in) {
        if (clientGame == null && bruno.client != null) clientGame = bruno.client.game;

        System.out.println(switch (in[0]) {
            //COMMON COMMANDS
            //region HELP
            case "help":
                yield """
                        \u001B[36mhelp\u001B[0m: Prints this list
                        \u001B[36mlogin <email> <password>\u001B[0m: Logs in
                        \u001B[36mstartServer\u001B[0m: Starts the server
                        \u001B[36mjoin <code>\u001B[0m: Joins the lobby with the code
                        \u001B[36mstop\u001B[0m: Exits the application

                        \u001B[36m---CLIENT COMMANDS---\u001B[0m
                        \u001B[36mdraw\u001B[0m: Draws cards
                        \u001B[36mplay <card index>\u001B[0m: Plays card at the index
                        \u001B[36mhand\u001B[0m: Prints your hand
                        \u001B[36mdiscard\u001B[0m: Prints the discard pile
                        \u001B[36mlastCard\u001B[0m: Prints the last card played
                        \u001B[36mdirection\u001B[0m: Prints the direction of the game

                        \u001B[36m---SERVER COMMANDS---\u001B[0m
                        \u001B[36mstartGame\u001B[0m: Starts the game
                        \u001B[36mserverLastCard\u001B[0m: Prints the last card played
                        \u001B[36mserverDiscard\u001B[0m: Prints the discard pile
                        \u001B[36mserverDirection\u001B[0m: Prints the direction of the game
                        \u001B[36mserverHand <player index>\u001B[0m: Prints the hand of the player at the position
                        """;
                //endregion
            case "login":
                JSONObject r = Firebase.auth(in[1], in[2]);
                String error = Firebase.getErrorType(r);

                if (error == null) {
                    bruno.setUser(r.get("localId").toString(), r.get("idToken").toString(), r.get("refreshToken").toString());
                    yield "Logged in as " + bruno.user.userName;
                } else yield error;
            case "startServer":
                bruno.net.startServer(Bruno.PORT);
                yield "Starting server";
            case "join":
                if (in.length == 1)
                    bruno.net.connectClient("localhost", Bruno.PORT);
                else
                    bruno.net.connectClient(Firebase.joinLobby(bruno.user.token, in[1], ""), Bruno.PORT);
                yield "Joined the lobby";
            case "startGame":
                bruno.server.startGame();
                serverGame = bruno.server.game;
                yield "Game started";
            case "stop":
                System.exit(0);

                //CLIENT COMMANDS
            case "draw":
                bruno.net.sendClientMessage(new DrawCardsClientMessage());
                yield "Trying to draw cards";
            case "play":
                Card card = clientGame.mainPlayer.hand.get(Integer.parseInt(in[1]));
                bruno.net.sendClientMessage(new PlayCardClientMessage(card));
                yield "Trying to play " + cardToFancyString(card);
            case "hold":
                bruno.net.sendClientMessage(new HoldCardClientMessage());
                yield "Trying to hold drawn card";
            case "color":
                bruno.net.sendClientMessage(new WildColorClientMessage(Card.CardColor.valueOf(in[1])));
                yield "Setting wild color";
            case "hand":
                yield cardsToString(clientGame.mainPlayer.hand, true);
            case "discard":
                yield cardsToString(clientGame.discard, false);
            case "lastCard":
                yield cardToFancyString(clientGame.discard.peekFirst());
            case "direction":
                yield clientGame.clockwise ? "clockwise" : "counterclockwise";

                //SERVER COMMANDS
            case "serverLastCard":
                yield cardToFancyString(serverGame.lastCardPlayed);
            case "serverDiscard":
                yield cardsToString(serverGame.discard, false);
            case "serverDirection":
                yield serverGame.clockwise ? "clockwise" : "counterclockwise";
            case "serverHand":
                yield cardsToString(serverGame.players[Integer.parseInt(in[1])].hand, true);
            default:
                yield "Unrecognized command, use \u001B[36mhelp\u001B[0m to see all commands";
        });
    }

    @Override
    public void gameStarted() {
        System.out.println("The game has started! Use \u001B[36mhand\u001B[0m to see your hand and \u001B[36mlastCard\u001B[0m to see the last card played!");
    }

    @Override
    public void nextTurn(int index) {
        System.out.println("It's " + clientGame.players[index].user.userName + "'s turn!");
    }

    @Override
    public void playedCard(int index, Card card) {
        System.out.println(clientGame.players[index].user.userName + " played a " + cardToFancyString(card));
    }

    //TODO: Wild color

    @Override
    public void drewCards(int index, int amount, Card[] cards) {
        if (index < 0)
            System.out.println("You draw " + cardsToString(Arrays.asList(cards), false));
        else
            System.out.println(clientGame.players[index].user.userName + " drew " + amount + " cards.");
    }

    public String cardsToString(Iterable<Card> cards, boolean index) {
        StringBuilder s = new StringBuilder();
        int i = 0;
        for (Card c : cards) {
            s.append(cardToFancyString(c));
            if (index) s.append(color ? toSubscriptIndex(i) : " " + i);
            s.append(" ");
            ++i;
        }
        return s.toString();
    }

    private String toSubscriptIndex(int i) {
        StringBuilder s = new StringBuilder();
        String.valueOf(i).chars().map(operand -> (char) ('\u2080' + Character.getNumericValue(operand))).forEach(c -> s.append((char) c));
        return s.toString();
    }

    public String cardToFancyString(Card card) {
        if (!color) return card.toString();
        return card.cardColor.fancySymbol + card.value.fancySymbol + "\033[0m";
    }
}
