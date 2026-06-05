package kamisado.model;

import java.io.Serializable;

/**
 * A jatekban resztvevo ket fel (Fekete es Feher) enum reprezentacioja.
 */
public enum Player implements Serializable{
    // felveheto ertekei:
    /**Fekete jatekos*/
    BLACK,
    /**Feher jatekos */
    WHITE;

    /**
     * Visszaadja az aktualis jatekos ellenfelet.
     * @return Ha FEKETE, akkor FEHER, es forditva.
     */
    public Player getOpponent() {
        if(this == Player.BLACK){
            return Player.WHITE;
        } else{
            return Player.BLACK;
        }
    }
}