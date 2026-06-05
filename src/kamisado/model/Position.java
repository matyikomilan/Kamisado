package kamisado.model;

import java.io.Serializable;

/**
 * Egy koordinatat (sor, oszlop) reprezentalo osztaly.
 * A tablan levo mezok es tornyok helyzetenek azonositasara szolgal.
 */
public class Position implements Serializable{

    /** Sor index. */
    private final int row;
    /** Oszlop index. */
    private final int col;

    //konstruktor:
    /**
     * Letrehoz egy uj pozicio objektumot.
     * @param r A sor indexe (0-7).
     * @param c Az oszlop indexe (0-7).
     */
    public Position(int r, int c){
        row = r;
        col = c;
    }

    /** Visszaadja a sor indexet.
     * @return sor index. */
    public int getRow(){
        return row;
    }

    /** Visszaadja az oszlop indexet.
     * @return oszlop index. */
    public int getCol(){
        return col;
    }

    /**
     * Osszehasonlit ket poziciot.
     * Ket pozicio akkor egyenlo, ha a sor- es oszlopindexeik megegyeznek.
     */
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    // sonarqube javaslatara:
    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}