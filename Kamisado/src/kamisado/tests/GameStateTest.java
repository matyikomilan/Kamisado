package kamisado.tests;

import kamisado.model.*;
import kamisado.model.GameState.GameMode;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameStateTest {

    @Test
    public void testPlayerSwitch() {
        GameState gs = new GameState(GameMode.SIMPLE);
        //fekete kezd:
        assertEquals(Player.BLACK, gs.getCurrentPlayer()); 
        
        gs.switchPlayer();
        assertEquals(Player.WHITE, gs.getCurrentPlayer());
        
        gs.switchPlayer();
        assertEquals(Player.BLACK, gs.getCurrentPlayer());
    }

    @Test
    public void testMove() {
        GameState gs = new GameState(GameMode.SIMPLE);
        Board board = gs.getBoard();
        
        //fekete kezd, van egy narancs tornya a (0,0)-n.
        //a (1,0) üres.
        Position from = new Position(0, 0);
        Position to = new Position(1, 0);
        
        gs.move(from, to);
        
        //torony atkerult-e:
        assertNull(board.getTowerAt(0, 0));
        assertNotNull(board.getTowerAt(1, 0));
        
        // jatekosvaltas:
        assertEquals("masik jatekos jon", Player.WHITE, gs.getCurrentPlayer());
        
        //lepeskenyszer frissult?
        Colour expectedColour = board.getTileColour(1, 0);
        assertEquals(expectedColour, gs.getNextMoveColour());
    }

    @Test
    public void testSumoPush() {
        GameState gs = new GameState(GameMode.STANDARD);
        Board board = gs.getBoard();
        for(int r=0; r<8; r++) for(int c=0; c<8; c++) board.setTowerAt(r, c, null);

        //fekete sumo
        // elotte feher aldozat (gyengebb, minden mas is adott)
        Tower sumo = new Tower(Player.BLACK, Colour.RED);
        sumo.upgradeSumoLevel(1);
        board.setTowerAt(3, 3, sumo);
        Tower victim = new Tower(Player.WHITE, Colour.BLUE);
        board.setTowerAt(4, 3, victim);

        // legyen torony amivel lepni tud a feka lokes utan:
        Colour landingColor = board.getTileColour(5, 3);
        board.setTowerAt(0, 0, new Tower(Player.BLACK, landingColor));


        gs.move(new Position(3, 3), new Position(4, 3));

        //minden babu a helyen-e:
        assertNotNull("aldozat eggyel hatrebb", board.getTowerAt(5, 3));
        assertNotNull("sumo az aldozat helyen", board.getTowerAt(4, 3));

        // NINCS switchplayer:
        assertEquals("sumopush --> megegyszer te jossz", Player.BLACK, gs.getCurrentPlayer());

        // a lepeskenyszer az aldozat alatti mezo szine
        Colour expected = board.getTileColour(5, 3);
        assertEquals("lepeskenyszer = aldozat alatti mezo szine ó", expected, gs.getNextMoveColour());
    }
}