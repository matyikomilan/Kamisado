package kamisado.model;

import java.io.Serializable;

/**
 * A jatektablat reprezentalo osztaly.
 * Tarolja a tornyokat (Tower) es a mezok szineit (Colour).
 */
public class Board implements Serializable{
    /** A tabla merete (8x8). */
    public static final int SIZE = 8;
    /** A tornyokat tarolo matrix. */
    private Tower[][] grid;
    /** A mezok szineit tarolo matrix. */
    private Colour[][] tileColours;

    //konstruktor, es hozza tartozo segedfv.-k:
    /**
     * Letrehozza a tablat, inicializalja a mezok szineit a hivatalos minta alapjan,
     * es felhelyezi a babukat a kezdopozicioba.
     */
    public Board(){
        grid = new Tower[SIZE][SIZE];
        tileColours = new Colour[SIZE][SIZE];

        tileColoursInit();
        towersInit();
    }

    // a "tetszoleges meret" miatt:
    // tabla mezoi szineinek beallitasa:
    /** A hivatalos szinelrendezes mintaja. */
    private static final Colour[][] COLOURPATTERN = {
        {Colour.ORANGE, Colour.BLUE, Colour.PURPLE, Colour.PINK, Colour.YELLOW, Colour.RED, Colour.GREEN, Colour.BROWN},
        {Colour.RED, Colour.ORANGE, Colour.PINK, Colour.GREEN, Colour.BLUE, Colour.YELLOW, Colour.BROWN, Colour.PURPLE},
        {Colour.GREEN, Colour.PINK, Colour.ORANGE, Colour.RED, Colour.PURPLE, Colour.BROWN, Colour.YELLOW, Colour.BLUE},
        {Colour.PINK, Colour.PURPLE, Colour.BLUE, Colour.ORANGE, Colour.BROWN, Colour.GREEN, Colour.RED, Colour.YELLOW},
        {Colour.YELLOW, Colour.RED, Colour.GREEN, Colour.BROWN, Colour.ORANGE, Colour.BLUE, Colour.PURPLE, Colour.PINK},
        {Colour.BLUE, Colour.YELLOW, Colour.BROWN, Colour.PURPLE, Colour.RED, Colour.ORANGE, Colour.PINK, Colour.GREEN},
        {Colour.PURPLE, Colour.BROWN, Colour.YELLOW, Colour.BLUE, Colour.GREEN, Colour.PINK, Colour.ORANGE, Colour.RED},
        {Colour.BROWN, Colour.GREEN, Colour.RED, Colour.YELLOW, Colour.PINK, Colour.PURPLE, Colour.BLUE, Colour.ORANGE}
    };

    /**
     * Inicializalja a mezok szineit a megadott minta (COLOURPATTERN) alapjan.
     * Ezt a konstruktor hivja meg a tabla letrehozasakor.
     */
    private void tileColoursInit(){
        for(int i=0; i < SIZE; ++i){
            for(int j=0; j < SIZE; ++j){
                tileColours[i][j] = COLOURPATTERN[i][j];
            }
        }
    }

    /**
     * Felhelyezi a babukat a kezdo sorokba (Fekete: 0. sor, Feher: 7. sor).
     * A babuk szine megegyezik a mezo szinevel, ahova kerulnek.
     */
    private void towersInit(){
        //fekete babuk elhelyezese:
        for(int i=0; i< SIZE; ++i){
            grid[0][i] = new Tower(Player.BLACK, tileColours[0][i]);
        }

        //feher babuk elhelyezese:
        for(int i=0; i<SIZE; ++i){
            grid[7][i] = new Tower(Player.WHITE, tileColours[7][i]);
        }
    }

    //getterek:
    // A getTowerAt() fole:
    /**
     * Visszaadja a tornyot.
     * @param i Sor index.
     * @param j Oszlop index.
     * @return A torony objektum.
     */
    public Tower getTowerAt(int i, int j){
        return grid[i][j];
    }

    /**
     * Visszaadja a mezo szinet.
     * @param i Sor index.
     * @param j Oszlop index.
     * @return A mezo szine.
     */
    public Colour getTileColour(int i, int j){
        return tileColours[i][j];
    }

    /**
     * Beallit egy tornyot.
     * @param r Sor index.
     * @param c Oszlop index.
     * @param t A torony.
     */
    public void setTowerAt(int r, int c, Tower t) {
        if (r >= 0 && r < SIZE && c >= 0 && c < SIZE) {
            grid[r][c] = t;
        }
    }

    // torony mozgatasa a tablan
    /**
     * Mozgat egy tornyot.
     * @param fromi Honnan sor.
     * @param fromc Honnan oszlop.
     * @param toi Hova sor.
     * @param toc Hova oszlop.
     */
    public void moveTowerFromTo(int fromi, int fromc, int toi, int toc){
        Tower t = grid[fromi][fromc];
        grid[fromi][fromc] = null;
        grid[toi][toc] = t;
    }
}