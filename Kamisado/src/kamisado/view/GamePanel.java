package kamisado.view;

import kamisado.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * A jatekter grafikus megjeleniteseert felelos panel.
 * Kirajzolja a racsot, a szines mezoket, a tornyokat es
 * a vizualis segitsegeket (kijeloles, lehetseges lepesek).
 */
public class GamePanel extends JPanel{
    // konstans meretek:
    /** Csempe merete. */
    private static final int TILE_SIZE = 60;
    /** Tabla merete pixelben. */
    private static final int BOARD_PIXEL_SIZE = TILE_SIZE * Board.SIZE;
    /** Margo. */
    private static final int BOARD_PADDING = 40;
    /** Teljes szelesseg. */
    private static final int TOTAL_WIDTH = (BOARD_PIXEL_SIZE * 2) + BOARD_PADDING * 3;
    /** Teljes magassag. */
    private static final int TOTAL_HEIGHT = BOARD_PIXEL_SIZE + BOARD_PADDING * 2;

    // tobbi attr.
    /** Jatek allapot. */
    private GameState gameState;

    //kijelolesekhez:
    /** Honnan lep. */
    private Position fromPos;
    /** Valid celmezok listaja a kirajzolashoz. */
    private ArrayList<Position> validTiles;
    /** Mozgathato tornyok listaja a kirajzolashoz. */
    private ArrayList<Position> validTowers;

    //konstruktr:
    /**
     * Inicializalja a jatekteret.
     * Beallitja a hatterszint (fekete) es a panel idealis mereteit.
     * @param gs A kezdeti jatekallapot, amit kirajzol.
     */
    public GamePanel(GameState gs){
        gameState = gs;
        validTiles = new ArrayList<>();
        validTowers = new ArrayList<>();
        setPreferredSize(new Dimension(TOTAL_WIDTH, TOTAL_HEIGHT));
        setBackground(Color.BLACK);
    }

    //:
    /**
     * Frissiti a panelen megjelenitendo jatekallapotot.
     * @param gs Az uj jatekallapot.
     */
    public void setGameState(GameState gs){
        gameState = gs;
        repaint();
    }

    /**
     * Frissiti a vizualis kijeloleseket (sarga keret, zold pöttyok).
     * @param p A kivalasztott torony pozicioja.
     * @param validMoves A kivalasztott torony lehetseges lepesei.
     * @param moveables Azok a tornyok, amelyekkel a jatekos lephet.
     */
    public void updateSelection(Position p, java.util.List<Position> validMoves, java.util.List<Position> moveables){
        fromPos = p;
        validTiles = (ArrayList<Position>) validMoves;
        validTowers = (ArrayList<Position>) moveables;
        repaint();
    }

    /**
     * A Swing altal hivott rajzolo metodus.
     * Ez felel a teljes jatekter rendereleseert minden kepfrissiteskor.
     * @param g A grafikus kontextus.
     */
    @Override
    protected void paintComponent(Graphics g){

        //atkasztolas mert Graphics bena
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        //elsimitas
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 2 tabla rajzolasa
        drawBoard(g2d, BOARD_PADDING, Player.BLACK);
        drawBoard(g2d, TOTAL_HEIGHT, Player.WHITE);

        //aktiv jatekos tablajanak kijelolese:
        highlightActiveBoard(g2d);

        //ha standard --> pontszamok kiirasa:
        drawScores(g2d);
    }

    //tablat megrajzolo fv:
    /**
     * Kirajzolja a jatektablat a megadott offsettel.
     * Vegigmegy a racson, kirajzolja a szines mezoket, a kijeloleseket
     * es a mezokon levo tornyokat.
     * @param g A grafikus kontextus.
     * @param offset Az Y tengely eltolasa (felso vagy also tabla).
     * @param ap (= "active player") A tabla tulajdonosa (fekete vagy feher jatekos szemszoge).
     */
    public void drawBoard(Graphics2D g, int offset, Player ap){
        
        for(int i=0; i<Board.SIZE; ++i){
            for(int j=0; j<Board.SIZE; ++j){
                int row = (ap == Player.WHITE) ? i : (Board.SIZE - (i+1));
                int col = j;

                // 
                int tileX = offset + col * TILE_SIZE;
                int tileY = BOARD_PADDING + row * TILE_SIZE;
                
                // 1. Mező rajzolása
                Colour tileColour = gameState.getBoard().getTileColour(i, j);
                g.setColor(tileColour.getColour()); 
                g.fillRect(tileX, tileY, TILE_SIZE, TILE_SIZE);

                
                Position currentPos = new Position(i, j);

                // A) valaszthato torony (sarga vastag keret)
                if (validTowers != null && validTowers.contains(currentPos)) {
                    g.setColor(Color.YELLOW);
                    g.setStroke(new BasicStroke(3));
                    g.drawRect(tileX + 2, tileY + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                    g.setStroke(new BasicStroke(1));
                }

                // B) kivalasztott torony ( atlatszo feher kitoltes)
                if (fromPos != null && fromPos.equals(currentPos)) {
                    g.setColor(new Color(255, 255, 255, 128)); // atlatszo feher
                    g.fillRect(tileX, tileY, TILE_SIZE, TILE_SIZE);
                }


                if (validTiles != null && validTiles.contains(currentPos)) {
                    // lehetseges celmazo kijelolese zold karikaval:
                    g.setColor(new Color(0, 255, 0, 100)); // attetszes
                    g.fillOval(tileX + 5, tileY + 5, TILE_SIZE - 10, TILE_SIZE - 10);
                }

                //Babu kirajzolasa:
                Tower tower = gameState.getBoard().getTowerAt(i, j);
                if (tower != null) {
                    drawTower(g, tower, tileX, tileY);
                }

            }
        }
    }

    // torony kirajzolasa:
    /**
     * Kirajzol egyetlen tornyot a megadott pixel koordinatakra.
     * A torony egy szines kor, korulotte kerettel.
     * Ha szumo torony, akkor extra jelolest es szintszamot is rajzol.
     * @param g2d A grafikus kontextus.
     * @param tower A kirajzolando torony objektum.
     * @param tileX A mezo bal felso sarkanak X koordinataja.
     * @param tileY A mezo bal felso sarkanak Y koordinataja.
     */ 
    private void drawTower(Graphics2D g2d, Tower tower, int tileX, int tileY) {
        int padding = 8;
        int towerSize = TILE_SIZE - (padding * 2);
        
        // Alap (Külső kör)
        Color baseColor = (tower.getPlayer() == Player.BLACK) ? Color.BLACK : Color.WHITE;
        g2d.setColor(baseColor);
        g2d.fillOval(tileX + padding, tileY + padding, towerSize, towerSize);
        g2d.setColor(Color.GRAY); // vekony keret
        g2d.drawOval(tileX + padding, tileY + padding, towerSize, towerSize);

        // Színjelölés (Belső kör)
        g2d.setColor(tower.getColour().getColour());
        int innerSize = towerSize / 2;
        g2d.fillOval(tileX + padding + (towerSize-innerSize)/2, tileY + padding + (towerSize-innerSize)/2, innerSize, innerSize);
        
        // Sumo jelzés
        if (tower.getSumoLevel() > 0) {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(tileX + padding - 2, tileY + padding - 2, towerSize + 4, towerSize + 4);
            g2d.setStroke(new BasicStroke(1));
            // szam:
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 20));
            String levelStr = String.valueOf(tower.getSumoLevel());     //igazitas kb kozepre
            g2d.drawString(levelStr, tileX + TILE_SIZE/2 - 6, tileY + TILE_SIZE/2 + 8);
        }
    }
    
    /**
     * Vastag zold kerettel jeloli ki az aktualisan soron levo jatekos tablajat.
     * @param g2d A grafikus kontextus.
     */
    private void highlightActiveBoard(Graphics2D g2d) {
        Player active = gameState.getCurrentPlayer();
        int x = (active == Player.BLACK) ? BOARD_PADDING : (BOARD_PADDING * 2 + BOARD_PIXEL_SIZE);
        
        g2d.setColor(Color.GREEN);
        g2d.setStroke(new BasicStroke(5));
        g2d.drawRect(x - 5, BOARD_PADDING - 5, BOARD_PIXEL_SIZE + 10, BOARD_PIXEL_SIZE + 10);
        g2d.setStroke(new BasicStroke(1));
    }

    /**
     * Kirajzolja az aktualis pontszamokat a kepernyo aljara (csak Standard modban).
     * @param g2d A grafikus kontextus.
     */
    private void drawScores(Graphics2D g2d) {
        if (gameState.getMode() != GameState.GameMode.STANDARD) return;

        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        // Fekete pontjai (Bal oldal)
        g2d.setColor(Color.WHITE);
        g2d.drawString("Fekete pont: " + gameState.getScoreBlack(), BOARD_PADDING, TOTAL_HEIGHT - 5);

        // Fehér pontjai (Jobb oldal)
        g2d.setColor(Color.WHITE); // Vagy sötétszürke, hogy látszódjon
        g2d.drawString("Fehér pont: " + gameState.getScoreWhite(), TOTAL_WIDTH - 150, TOTAL_HEIGHT - 5);
    }

}