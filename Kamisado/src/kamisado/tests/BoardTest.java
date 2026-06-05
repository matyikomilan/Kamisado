package kamisado.tests;

import kamisado.model.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class BoardTest {

    @Test
    public void testBoardInitialization() {
        Board board = new Board();
        
        // fekete bazison fekete babuk:
        assertNotNull("0,0-n van babu", board.getTowerAt(0, 0));
        assertEquals("fekete a babu", Player.BLACK, board.getTowerAt(0, 0).getPlayer());
        
        // feher babun feher babuk:
        assertNotNull("7,7-n van babu", board.getTowerAt(7, 7));
        assertEquals("feher a babu", Player.WHITE, board.getTowerAt(7, 7).getPlayer());
        
        // 2 kozott nincsenek babuk (pl 3,3-n)
        assertNull("a palya kozepen nincs babu", board.getTowerAt(3, 3));
    }

    @Test
    public void testTileColours() {
        Board board = new Board();
        // "A táblát úgy kell letenni, hogy:
        //      mindkét játékos saját bázisának jobb oldali mezője
        //      (azaz a tábla jobb alsó sarka) narancssárga, a bal oldali mezője barna legyen."
        assertEquals(Colour.ORANGE, board.getTileColour(0, 0));
    }

    @Test
    public void testMoveTowerFromTo() {
        Board board = new Board();
        Tower t = board.getTowerAt(0, 0);
        
        //mozgatjuk (0,0)-rol (1,0)-ra
        board.moveTowerFromTo(0, 0, 1, 0);
        
        assertNull("regi helyen nincs ott a torony", board.getTowerAt(0, 0));
        assertNotNull("uj helyen ott a torony", board.getTowerAt(1, 0));
        assertEquals("az uj helyen ott a torony", t, board.getTowerAt(1, 0));
    }


}