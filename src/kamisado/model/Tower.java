package kamisado.model;

import java.io.Serializable;

/**
 * Egy jatekostornyot reprezentalo osztaly.
 * Tarolja a torony tulajdonosat, szinet es a szumo szintjet (erosseget).
 */
public class Tower implements Serializable{
    /**Melyik jatekoshoz tartozik a torony. */
    private final Player player;
    /**A torony szine */
    private final Colour colour;
    /**A torony sumo-szintje */
    private int sumoLevel;

    // konstruktor:
    /**
     * Letrehoz egy uj tornyot alapertelmezett (0) szumo szinttel.
     * @param p A torony tulajdonosa (FEKETE vagy FEHER).
     * @param c A torony szine (ami alapjan mozoghat).
     */
    public Tower(Player p, Colour c){
        player = p;
        colour = c;
        sumoLevel = 0;
    }

    //getterfv.-k:
    /** Visszaadja a torony tulajdonosat.
     * @return A jatekos. */
    public Player getPlayer(){
        return player;
    }

    /** Visszaadja a torony szinet.
     * @return A torony szine. */
    public Colour getColour(){
        return colour;
    }

    /** Visszaadja a torony aktualis szumo szintjet (0 = sima, 1+ = szumo).
     * @return Szumo szint */
    public int getSumoLevel(){
        return sumoLevel;
    }

    // szumoszintet emel:
    /**
     * Noveli a torony szumo szintjet (pl. pontszerzes utan).
     * @param n A novekedes merteke.
     */
    public void upgradeSumoLevel(int n){
        sumoLevel += n;
    }
    
}