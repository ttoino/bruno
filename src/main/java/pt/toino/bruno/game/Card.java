package pt.toino.bruno.game;

public class Card {
    public CardColor cardColor;
    public CardValue value;

    public Card(CardColor c, CardValue v) {
        cardColor = c;
        value = v;
    }

    public Card(byte b) {
        this(CardColor.values()[(b & 112) >> 4], CardValue.values()[b & 15]);
    }

    @Override
    public String toString() {
        return cardColor.toString() + "-" + value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Card && equals((Card) obj);
    }

    public boolean equals(Card other) {
        return other != null && other.cardColor == this.cardColor && other.value == this.value;
    }

    public Card clone() {
        return new Card(this.cardColor, this.value);
    }

    public byte toByte() {
        byte r = (byte) value.num;

        r += (byte) cardColor.num << 4;

        return r;
    }

    public enum CardColor {
        RED("red", 0, "\033[0;31m"), GREEN("green", 1, "\033[0;32m"), YELLOW("yellow", 2, "\033[0;33m"), BLUE("blue", 3, "\033[0;34m"), WILD("wild", 4, "\033[0;30m");

        public String name;
        public int num;
        public String fancySymbol;

        CardColor(String n, int v, String s) {
            name = n;
            num = v;
            fancySymbol = s;
        }

        public String toString() {
            return name;
        }
    }

    public enum CardValue {
        ONE("1", 0, "1"), TWO("2", 1, "2"), THREE("3", 2, "3"), FOUR("4", 3, "4"), FIVE("5", 4, "5"), SIX("6", 5, "6"), SEVEN("7", 6, "7"), EIGHT("8", 7, "8"), NINE("9", 8, "9"),
        REVERSE("reverse", 9, "\uD83D\uDD01"), SKIP("skip", 10, "⃠"), PLUS_TWO("plus-2", 11, "+2"), ZERO("0", 12, "0"), WILD("wild", 13, "W"), PLUS_FOUR("plus-4", 14, "+4");

        public String name;
        public int num;
        public String fancySymbol;

        CardValue(String n, int v, String s) {
            name = n;
            num = v;
            fancySymbol = s;
        }

        public String toString() {
            return name;
        }
    }
}
