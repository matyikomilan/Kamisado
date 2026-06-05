package kamisado.tests;

import kamisado.model.*;
import org.junit.Test;
import java.util.List; 
import static org.junit.Assert.*;

public class GameLogicTest {

    @Test
    public void testWinCheck() {
        // fekete nyer ha a tornya a 7. sorba jut (ha a 6.ba jut akk meg nem nyer):
        assertTrue(GameLogic.winCheck(Player.BLACK, 7));
        assertFalse(GameLogic.winCheck(Player.BLACK, 6));

        // feher nyer, ha a tornya a 0. sorba jut (ha az 1.be jut akk meg nem nyer):
        assertTrue(GameLogic.winCheck(Player.WHITE, 0));
        assertFalse(GameLogic.winCheck(Player.WHITE, 1));
    }

    @Test
    public void testGetValidTiles() {
        Board board = new Board();
        clearBoard(board);

        // fekete torony (2,2)-n:
        Tower t = new Tower(Player.BLACK, Colour.RED);
        board.setTowerAt(2, 2, t);
        
        List<Position> moves = GameLogic.getValidTiles(board, new Position(2, 2), false);
        
        // elore, atlosan jobbra balra lephet 1-1-1-et:
        assertTrue(moves.contains(new Position(3, 2)));
        assertTrue(moves.contains(new Position(3, 1)));
        assertTrue(moves.contains(new Position(3, 3)));
    }

    @Test
    public void testCanSumoPushSuccess() {
        Board board = new Board();
        clearBoard(board);

        //fekete Sumo (3,3)-n
        //feher torny (4,3)-n.
        Tower sumo = new Tower(Player.BLACK, Colour.RED);
        sumo.upgradeSumoLevel(1);
        board.setTowerAt(3, 3, sumo);

        Tower victim = new Tower(Player.WHITE, Colour.BLUE);
        board.setTowerAt(4, 3, victim);

        // tudnia kell lokni, mert minden adott:
        boolean canPush = GameLogic.canSumoPush(board, new Position(3, 3), new Position(4, 3));
        assertTrue("A Szumónak tudnia kell lökni a gyengébbet üres helyre", canPush);
    }

    @Test
    public void testCanSumoPushFailEqualStrength() {
        Board board = new Board();
        clearBoard(board);

        //egymassal szemben 2 ugyanolyaneros sumo (ellenfeles tornoyk):
        Tower t1 = new Tower(Player.BLACK, Colour.RED);
        board.setTowerAt(3, 3, t1);
        Tower t2 = new Tower(Player.WHITE, Colour.BLUE);
        board.setTowerAt(4, 3, t2);

        boolean canPush = GameLogic.canSumoPush(board, new Position(3, 3), new Position(4, 3));
        assertFalse("nem gyengebb nala a lokni kivant ellenseges sumotorony", canPush);
    }

    @Test
    public void testCanSumoPushFailDiagonal() {
        Board board = new Board();
        clearBoard(board);

        //leponel gyengebb torony tole atlosan:
        Tower sumo = new Tower(Player.BLACK, Colour.RED);
        sumo.upgradeSumoLevel(1);
        board.setTowerAt(3, 3, sumo);

        Tower victim = new Tower(Player.WHITE, Colour.BLUE);
        board.setTowerAt(4, 4, victim);

        boolean canPush = GameLogic.canSumoPush(board, new Position(3, 3), new Position(4, 4));
        assertFalse("atlosan nem lehet sumolokni", canPush);
    }

    @Test
    public void testCanSumoPushFailBlockedBehind() {
        Board board = new Board();
        clearBoard(board);

        // fekete sumo
        // vele szemben: feher torony
        // mogotte van egy torony
        Tower sumo = new Tower(Player.BLACK, Colour.RED);
        sumo.upgradeSumoLevel(1);
        board.setTowerAt(3, 3, sumo);

        Tower victim = new Tower(Player.WHITE, Colour.BLUE);
        board.setTowerAt(4, 3, victim);

        Tower wall = new Tower(Player.BLACK, Colour.GREEN);
        board.setTowerAt(5, 3, wall);

        boolean canPush = GameLogic.canSumoPush(board, new Position(3, 3), new Position(4, 3));
        assertFalse("nincs hova lokni az aldozatot, vannak mogotte", canPush);
    }

    // segedfuggveny a tabla torlesehez
    private void clearBoard(Board board) {
        for(int r=0; r<8; r++) {
            for(int c=0; c<8; c++) {
                board.setTowerAt(r, c, null);
            }
        }
    }
}