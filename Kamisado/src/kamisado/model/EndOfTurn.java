package kamisado.model;

/**
 * A lepes kimenetelet jelzo enumeracio.
 * Hasznalata a move() fuggveny visszateresi ertekekent:
 * - WIN: A jatekos nyert (elerte a tuloldalt).
 * - BLOCKED: A jatekos beszorult (nem tud lepni), ezert a kor visszaszall.
 * - NEXT: A jatek folyik tovabb, a kovetkezo jatekos jon.
 */
public enum EndOfTurn{
    /** Gyoztes kor. */
    WIN,
    /** A jatekos blokkolva van. */
    BLOCKED,
    /** A jatek folytatodik. */
    NEXT
}