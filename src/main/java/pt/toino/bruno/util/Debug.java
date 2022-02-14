package pt.toino.bruno.util;

public class Debug {
    public static boolean debug = false;

    public static void println(Object object) {
        if (debug) System.out.println(object);
    }

}
