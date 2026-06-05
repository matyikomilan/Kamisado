package kamisado.model;

import java.awt.Color;

/**
 * A jatekban hasznalt 8 szint definialo Enum.
 * Minden elem tarolja a hozza tartozo Java AWT szinkodot a kirajzolashoz.
 */
public enum Colour{
    /** Narancssarga. */
    ORANGE(new Color(204, 85, 0)),
    /** Kek. */
    BLUE(new Color(0, 0, 128)),
    /** Lila. */
    PURPLE(new Color(75, 0, 130)),
    /** Pink. */
    PINK(new Color(139, 0, 70)),
    /** Sarga. */
    YELLOW(new Color(184, 134, 11)),
    /** Piros. */
    RED(new Color(139, 0, 0)),
    /** Zold. */
    GREEN(new Color(0, 100, 0)),
    /** Barna. */
    BROWN(new Color(60, 30, 10));

    /** A Swing kompatibilis szinobjektum. */
    private final Color color;

    /**
     * Privat konstruktor a szin definialasahoz.
     * @param c A Java AWT Color objektum.
     */
    private Colour(Color c){
        color = c;
    }

    /** @return Color objektum. */
    public Color getColour(){
        return color;
    }
}