package kamisado.model;

import java.util.ArrayList;
import java.util.List;


// ebben az osztalyban minden static, mert ezek szabalyok, es igy nem kell mindig objektumkent letrehozni
/**
 * A jatek szabalyrendszeret tartalmazo statikus segedosztaly.
 * Felelos a lepesek ervenyessegenek vizsgalataert, a szumo lokes szabalyaiert
 * es a gyozelmi feltetelek ellenorzeseert.
 * * @author Matyiko Milan
 * @version 1.0
 */
public class GameLogic{
    // lepesek iranyai (fekete/feher jatekosnak)
    /** Fekete lepesiranyai. */
    private static final int[][] blackDir = {
        {1, 0},  // elore
        {1, -1}, // atlosan jobbre(fekaszemszogbol)
        {1, 1}   // atlosan balra (fekaszemszogbol)
    };
    /** Feher lepesiranyai. */
    private static final int[][] whiteDir = {
        {-1, 0},    //elore
        {-1, -1},   // atlosan balra
        {-1, 1}     // atlosan jobbra
    };

    // azon mezok kivalasztasa, ahova a babu lephet
    /**
     * Visszaadja azon mezok listajat, ahova az adott torony szabalyosan lephet.
     * Figyelembe veszi a palya hatarait, a blokkolo babukat es a szumo kepesseget.
     * @param board Az aktualis jatektabla.
     * @param fromPos A lepni kivano torony pozicioja.
     * @param sumo Jelzi, hogy a torony rendelkezik-e szumo kepesseggel (szint > 0).
     * @return A szabalyos celmezok listaja (Position objektumokkent).
     */
    public static List<Position> getValidTiles(Board board, Position fromPos, boolean sumo){
        ArrayList<Position> validTiles = new ArrayList<>();

        int fromr = fromPos.getRow();
        int fromc = fromPos.getCol();

        Tower currentTower = board.getTowerAt(fromr, fromc);
        if(currentTower == null){
            return validTiles;  //itt meg ures ugye
        }

        Player activePlayer = currentTower.getPlayer();

        int maxSteps = sumo ? 5 : Board.SIZE;

        // mezok kivalasztasa, amikre a babu lephet:
        //minden iranyt megvizsgalunk
        //a megfelelo jatekoshoz
        int[][] apdir;      // (active player directions)
        if(activePlayer == Player.WHITE){
            apdir = whiteDir;
        } else{
            apdir = blackDir;
        }

        for(int[] dir : apdir){
            // dir szetvalasztasa sor/oszlop
            int rowDir = dir[0];
            int colDir = dir[1];

            for(int i=1; i<=maxSteps; ++i){
                int tor = fromr + rowDir * i;
                int toc = fromc + colDir * i;

                // "palyan kivulre lepne"
                if(tor >= Board.SIZE || tor < 0 || toc >= Board.SIZE || toc < 0){
                    break;
                }

                //mar all ott babu
                if(board.getTowerAt(tor, toc) != null){
                    if(sumo && canSumoPush(board, fromPos, new Position(tor, toc))){
                        validTiles.add(new Position(tor, toc));
                    }
                    break;
                }

                //ha eddig nem bukott meg semmin, akkor ide lephet
                validTiles.add(new Position(tor, toc));
            }
        }
        //minden lehetseges mezo megvizsgalva, jok hozzaadva, lista visszaadasa: 
        return validTiles;
    }

    //mozgathato tornyok kivalasztasa (ha van lepeskenyszer/ha nincs) 
    /**
     * Meghatarozza az adott jatekallapotban mozgathato tornyokat.
     * Ket esetet kezel:
     * 1. Lepeskenyszer van (nextMoveColour != null): Csak a megfelelo szinu torony adhato vissza.
     * 2. Nincs lepeskenyszer (pl. jatek eleje): Barmelyik sajat torony visszaadhato, ami tud lepni.
     * @param gamestate Az aktualis jatekallapot.
     * @return Azon tornyok pozicioinak listaja, amelyekkel a jatekos lephet.
     */
    public static List<Position> getMoveableTowers(GameState gamestate){
        ArrayList<Position> moveableTowers = new ArrayList<>();
        
        //HA lepeskenyszer van:
        if(gamestate.getNextMoveColour() != null){
            //kezdeti erteknek:
            Position requiredPos = null;

            // egesz tabla vizsgalata:
            for(int i=0; i<Board.SIZE; ++i){
                for(int j=0; j<Board.SIZE; ++j){
                    Tower t = gamestate.getBoard().getTowerAt(i, j);

                    // torony megkeresese: aze aki jon, passzol a szin
                    if(t!= null && t.getPlayer() == gamestate.getCurrentPlayer()
                    && t.getColour() == gamestate.getNextMoveColour()){
                        requiredPos = new Position(i, j);
                        break;
                    }
                }
                if(requiredPos != null){
                    break;
                }
            }
            // azert tesszuk ezt a feltetelt, hogy ne dobjon kivetelt
            if(requiredPos != null){
                Tower t = gamestate.getBoard().getTowerAt(requiredPos.getRow(), requiredPos.getCol());

                //az ervenyes lepeslehetosegeket berakjuk a moveavleTowers-be
                if(!getValidTiles(gamestate.getBoard(), requiredPos, t.getSumoLevel()>0).isEmpty()){
                    moveableTowers.add(requiredPos);
                }
                return moveableTowers;
            }

        // HA NINCS lepeskenyszer:
        } else{
            //egesz tabla vizsgalata:
            for(int i=0; i<Board.SIZE; ++i){
                for(int j=0; j<Board.SIZE; ++j){
                    //ha itt all torony es az a lepo jatekose ÉS van hova lepnie:
                    Tower t = gamestate.getBoard().getTowerAt(i, j);
                    if( t!=null && t.getPlayer() == gamestate.getCurrentPlayer()
                    && !getValidTiles(gamestate.getBoard(), new Position(i, j), t.getSumoLevel()>0).isEmpty()){
                        moveableTowers.add(new Position(i, j));
                    }
                }
            }
            
        }

        return moveableTowers;
    }

    // gyozott-e vmelyik fel
    /**
     * Ellenorzi, hogy egy adott jatekos elerte-e a gyozelmi feltetelt.
     * @param p A jatekos (FEKETE vagy FEHER).
     * @param r A sor indexe, ahova a torony erkezett.
     * @return true, ha a jatekos elerte az ellenfel alapvonalat (Fekete: 7, Feher: 0).
     */
    public static boolean winCheck(Player p, int r){
        if(p == Player.BLACK){
            return r == Board.SIZE-1;
        } else{
            return r == 0;
        }
    }

    //blokkcheck:
    /**
     * Ellenorzi, hogy az aktualis jatekos blokkolva van-e (nem tud lepni).
     * Ez a "patthelyzet" vagy lepeskimaradas alapja.
     * @param gamestate Az aktualis jatekallapot.
     * @return true, ha a jatekosnak nincs ervenyes lepese.
     */
    public static boolean blockCheck(GameState gamestate){
        // mozgathato tornyok:
        List<Position> moveableTowers = getMoveableTowers(gamestate);

        return moveableTowers.isEmpty();
        //ez pont az amire kivancsiak vagyunk, mert ez 2 esetben lesz ures:
        // HA NINCS lépéskényszer --> egyetlen toronnyal sem tud lepni a currentPlayer
        // VAGY lépéskényszer van --> nem tud lepni a kenyszeritett torony
    }

    // torony helyzete:
    /**
     * Megkeresi azt a tornyot, amellyel a jatekosnak kotelezo lepnie.
     * Ez a fuggveny keresi meg a tablan a sajat szinu, es 'nextMoveColour' szinu tornyot.
     * @param gamestate Az aktualis jatekallapot.
     * @return A kotelezo torony pozicioja, vagy null, ha nincs ilyen.
     */
    public static Position getTowerPos(GameState gamestate){
        // egesz tabla vizsgalata:
        for(int i=0; i<Board.SIZE; ++i){
            for(int j=0; j<Board.SIZE; ++j){
                Tower t = gamestate.getBoard().getTowerAt(i, j);
                // a most lepo jatekos babuja + kovetkezo szin szine van
                if (t != null && t.getPlayer() == gamestate.getCurrentPlayer() && t.getColour() == gamestate.getNextMoveColour()) {
                return new Position(i, j);
                }
            }
        }
        return null;
    }

    //sumo tud-e lokni:
    /**
     * Eldonti, hogy egy Szumo torony el tudja-e lokni az ellenfel tornyat.
     * Szabalyok:
     * - Csak egyenes vonalban lehet lokni.
     * - Csak az ellenfel tornyat lehet lokni.
     * - A tamadonak erosebbnek vagy egyenlonek kell lennie (sumoLevel).
     * - Az aldozat mogott ures mezonek kell lennie a palyan belul.
     * @param board A jatektabla.
     * @param sumPos A tamado (Szumo) torony pozicioja.
     * @param vicPos A megtamadott (Aldozat) torony pozicioja.
     * @return true, ha a lokes szabalyos es vegrehajthato.
     */
    public static boolean canSumoPush(Board board, Position sumPos, Position vicPos) {
        int rSumo = sumPos.getRow();
        int cSumo = sumPos.getCol();
        int rVictim = vicPos.getRow();
        int cVictim = vicPos.getCol();

        Tower sumoTower = board.getTowerAt(rSumo, cSumo);
        Tower victimTower = board.getTowerAt(rVictim, cVictim);
        
        // egymassal szemben allnak (nem atlosan)
        if (cSumo != cVictim) {
            return false;
        }

        // nem ellenseg --> NEM lokhet
        if (victimTower.getPlayer() == sumoTower.getPlayer()) return false;

        // nem gyengebb az aldozat --> NEM lokhet
        if (victimTower.getSumoLevel() >= sumoTower.getSumoLevel()) return false;

        // aldozat mogott VAN HELY??
        int pushDir = rVictim - rSumo;  // fekete vagy a feher lok??
        // egybol szemben all vele????
        if(pushDir != -1 && pushDir != 1){
            return false;
        }
        int rLanding = rVictim + pushDir;
        int cLanding = cVictim;

        if (rLanding < 0 || rLanding >= Board.SIZE || cLanding < 0 || cLanding >= Board.SIZE) return false;

        // ha eddig minden jo es nincs az aldozat mogott babu, akk durranhat:
        return board.getTowerAt(rLanding, cLanding) == null;
    }

}