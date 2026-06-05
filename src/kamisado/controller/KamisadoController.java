package kamisado.controller;
import kamisado.model.GameState.GameMode;
import kamisado.model.*;
import kamisado.view.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * A Controller reteg a Kamisado jatekban (MVC architektura).
 * Ez az osztaly kezeli a felhasznaloi interakciokat (egerkattintas, menugombok),
 * vezerli a jatekmenetet es frissiti a grafikus feluletet a modell alapjan.
 */
public class KamisadoController extends MouseAdapter implements ActionListener {

    /** Az aktualis jatekallapot. */
    private GameState gameState;
    /** A foablak referenciaja. */
    private MainFrame mainFrame;
    /** A kivalasztott torony pozicioja. */
    private Position selectedFromPos; 

    //meretek (egyeznie kell a GamePanel-lel)
    /** Egy mezo merete pixelben. */
    private static final int TILE_SIZE = 60;
    /** A tabla koruli marga. */
    private static final int PADDING = 40;
    /** A tabla merete pixelben. */
    private static final int BOARD_SIZE_PX = TILE_SIZE * 8;

    //konstruktor:
    /**
     * Letrehozza a Controllert es elinditja a foablakot (MainFrame).
     */
    public KamisadoController() {
        mainFrame = new MainFrame(this);
    }
    
    /**
     * Elindit egy uj jatekot a megadott modban.
     * Letrehozza az uj GameState-et es frissiti a feluletet.
     * @param mode A jatekmod (SIMPLE vagy STANDARD).
     */
    private void startNewGame(GameMode mode) {
        this.gameState = new GameState(mode);
        this.selectedFromPos = null;

        mainFrame.showGame(gameState, this);
        
        updateView();
    }

    //view mindig jo kijelöléseket kapja
    /**
     * Szinkronizalja a grafikus feluletet (View) a logikaval (Model).
     * 1. Lekeri a mozgathato tornyokat (sarga keret).
     * 2. Ha van kivalasztott torony, lekeri a celmezoket (zold potty).
     * 3. Atadja ezeket az adatokat a GamePanel-nek ujrarajzolasra.
     */
    private void updateView() {
        // 1. Lekerdezzuk, kikkel lephetunk (sarga keretek)
        List<Position> moveables = GameLogic.getMoveableTowers(gameState);
        
        // 2. Ha van kivalasztott torony, lekerdezzuk hova lephet (zold pottyok)
        List<Position> validMoves = null;
        if (selectedFromPos != null) {
            Tower t = gameState.getBoard().getTowerAt(selectedFromPos.getRow(), selectedFromPos.getCol());
            if (t != null) {
                validMoves = GameLogic.getValidTiles(gameState.getBoard(), selectedFromPos, t.getSumoLevel() > 0);
            }
        }
        
        // 3. atkuldjuk az adatokat a panelnek
        if (mainFrame != null) {
            mainFrame.getGamePanel().setGameState(gameState); // allapot
            mainFrame.getGamePanel().updateSelection(selectedFromPos, validMoves, moveables); // kijelolesek
        }
    }

    /**
     * Kezeli a tablan torteno kattintasokat.
     * Atszamitja a pixeles koordinatakat logikai koordinatakra (Position),
     * es meghivja a megfelelo logikai lepest.
     * @param e Az egerelemeny.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameState == null) return;

        int x = e.getX();
        int y = e.getY();

        // 1. Melyik táblára kattintottunk?
        boolean clickedLeft = (x >= PADDING && x < PADDING + BOARD_SIZE_PX);
        boolean clickedRight = (x >= PADDING * 2 + BOARD_SIZE_PX && x < PADDING * 2 + BOARD_SIZE_PX * 2);
        
        if (!clickedLeft && !clickedRight) return; // Kívül kattintott
        
        // 2. Relatív koordináták kiszámolása
        int boardX = clickedLeft ? (x - PADDING) : (x - (PADDING * 2 + BOARD_SIZE_PX));
        int boardY = y - PADDING;
        
        int c = boardX / TILE_SIZE;
        int rVisual = boardY / TILE_SIZE;
        
        if (c < 0 || c >= 8 || rVisual < 0 || rVisual >= 8) return;

        // 3. Perspektíva fordítása (Visual -> Logical)
        // Ha a Bal oldalit (Fekete nézet) kattintottuk: Row 7 lent van. Logic Row 0. -> (7 - r_visual)
        // Ha a Jobb oldalit (Fehér nézet) kattintottuk: Row 0 fent van. Logic Row 0. -> (r_visual)
        
        int rLogical;
        if (clickedLeft) {
            rLogical = 8 - 1 - rVisual; // Fekete nézet (fordított)
        } else {
            rLogical = rVisual;         // Fehér nézet (normál)
        }

        Position clickedPos = new Position(rLogical, c);
        handleLogic(clickedPos);
    }

    /**
     * A kattintas logikai feldolgozasa.
     * Ket fazisa van:
     * 1. Kivalasztas: Ha meg nincs kivalasztva torony, vagy a jatekos masikra kattint.
     * 2. Mozgatas: Ha mar van kivalasztva, es valid celmezore kattint.
     * @param clickedPos A logikai pozicio, ahova a felhasznalo kattintott.
     */
    private void handleLogic(Position clickedPos) {
        List<Position> moveables = GameLogic.getMoveableTowers(gameState);

        // A) Ha még nincs kiválasztva semmi, VAGY a felhasználó másik saját bábura kattint
        if (selectedFromPos == null || moveables.contains(clickedPos)) {
            if (moveables.contains(clickedPos)) {
                selectedFromPos = clickedPos;
                //torony kivalasztva
                updateView(); // Frissítés -> Megjelennek a zöld pöttyök
            }
        } 
        // B) Ha már van kiválasztva, és érvényes célmezőre kattint
        else {
            Tower t = gameState.getBoard().getTowerAt(selectedFromPos.getRow(), selectedFromPos.getCol());
            List<Position> validMoves = GameLogic.getValidTiles(gameState.getBoard(), selectedFromPos, t.getSumoLevel() > 0);
            
            if (validMoves.contains(clickedPos)) {
                // lepes:
                EndOfTurn result = gameState.move(selectedFromPos, clickedPos);
                
                selectedFromPos = null; // Kijelölés törlése
                updateView(); // Frissítés -> Átkerül a bábu, új jelölések

                //!!!
                handleEndOfTurn(result);
            }
        }
    }

    /**
     * Kezeli a menugombok megnyomasat (Uj jatek, Mentes, Betoltes).
     * @param e Az akcioesemeny.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("NEW_SIMPLE")) startNewGame(GameMode.SIMPLE);
        else if (cmd.equals("EXIT")) System.exit(0);
        else if (cmd.equals("NEW_STANDARD")) startNewGame(GameMode.STANDARD);
        else if (cmd.equals("SAVE_GAME"))  saveGame();
        else if (cmd.equals("LOAD_GAME"))  loadGame();
        else if (cmd.equals("SHOW_RULES")) mainFrame.showRules();
        else if(cmd.equals("BACK_TO_MENU")){
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Biztosan vissza szeretne lépni a főmenübe? A nem mentett állás elveszik.", "Főmenü", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION){
                this.gameState = null;
                mainFrame.showMainMenu();
            }
        }
    }

    /**
     * Kezeli a kor veget a lepes eredmenye alapjan.
     * - WIN: Kezeli a gyozelmet, pontszamitast es az uj kor inditasat.
     * - BLOCKED: Megjeleniti a lepeskimaradas uzenetet.
     * @param result A move() fuggveny visszateresi erteke (WIN, BLOCKED, NEXT).
     */
    private void handleEndOfTurn(EndOfTurn result) {
        Player winner = gameState.getCurrentPlayer(); 

        if (result == EndOfTurn.WIN) {
            String msg = "A kört nyerte: " + winner + "!";

            // STANDARD mode
            if (gameState.getMode() == GameMode.STANDARD) {
                // megvana 3 pont?
                int score = (winner == Player.BLACK) ? gameState.getScoreBlack() : gameState.getScoreWhite();

                if (score >= 3) {
                    JOptionPane.showMessageDialog(mainFrame, "JÁTSZMA VÉGE! " + winner + " nyert 3 ponttal!", "Győzelem", JOptionPane.INFORMATION_MESSAGE);
                    startNewGame(GameMode.STANDARD); // Új meccs indul
                } else {
                    JOptionPane.showMessageDialog(mainFrame, msg + "\nÁllás: Fekete " + gameState.getScoreBlack() + " - Fehér " + gameState.getScoreWhite() + "\nKövetkező kör...", "Kör vége", JOptionPane.INFORMATION_MESSAGE);

                    String[] options = {"Balról", "Jobbról"};
                    int choice = JOptionPane.showOptionDialog(mainFrame,  winner + " játékos (Védő)! Honnan töltsük fel a bázist?", "Kör előkészítése", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    
                    boolean fillFromRight = (choice == 1); 
                    
                    gameState.nextRound(fillFromRight); 
                    
                    updateView();
                }
            } 
            // SIMPLE MÓD LOGIKA
            else {
                JOptionPane.showMessageDialog(mainFrame, "Játék Vége! " + winner + " nyert!", "Győzelem", JOptionPane.INFORMATION_MESSAGE);
                startNewGame(GameMode.SIMPLE);
            }

        } else if (result == EndOfTurn.BLOCKED) {
             JOptionPane.showMessageDialog(mainFrame, "Lépéskimaradás! A sor visszaszáll az előző játékoshoz.", "Blokkolás", JOptionPane.WARNING_MESSAGE);
        }
    }

    // svae/load:
    // save:
    /**
     * Elmenti az aktualis jatekallapotot egy fajlba.
     * Megnyit egy dialogusablakot a fajlnev megadasahoz, majd szerializalja a GameState-et.
     * A fajlokat a 'saves' mappaba menti .kam kiterjesztessel.
     */
    private void saveGame() {
        //saves mappa
        File saveDir = new File("saves");
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }

        //mentes neve:
        String name = JOptionPane.showInputDialog(mainFrame, "Add meg a mentés nevét:", "Játék Mentése", JOptionPane.QUESTION_MESSAGE);
        
        // visszalepes
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        //.kam kiterjesztes, mentes, tomorites (szerializalas)
        if (!name.toLowerCase().endsWith(".kam")) {
            name += ".kam";
        }
        File file = new File(saveDir, name);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(gameState);
            JOptionPane.showMessageDialog(mainFrame, "Sikeres mentés: " + name, "Mentés", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Hiba a mentés során!\n" + ex.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    //load:
    /**
     * Betolt egy korabban elmentett jatekot.
     * Kilistazza a 'saves' mappa tartalmat, es a kivalasztott fajlbol
     * deszerializalja a GameState objektumot.
     */
    private void loadGame() {
        File saveDir = new File("saves");
        
        //megvane a saves
        if (!saveDir.exists() || !saveDir.isDirectory()) {
            JOptionPane.showMessageDialog(mainFrame, "Nincs 'saves' mappa vagy üres.", "Betöltés", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // .kam fajlok egy tombbe:
        File[] files = saveDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".kam"));
        
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(mainFrame, "Nincsenek mentett játékok.", "Betöltés", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // fajlNEVEK
        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }

        // valasztomenu:
        String selectedName = (String) JOptionPane.showInputDialog(
            mainFrame, 
            "Válassz egy mentést:", 
            "Játék Betöltése", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            fileNames, 
            fileNames[0]
        );

        //valasztas -->
        if (selectedName != null) {
            File file = new File(saveDir, selectedName);
            
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                // Deszerializálás
                this.gameState = (GameState) in.readObject();

                // jatek megniytasa:
                mainFrame.showGame(gameState, this);
                
                // UI Reset és Frissítés
                this.selectedFromPos = null;
                updateView(); 
                
                JOptionPane.showMessageDialog(mainFrame, "Játék betöltve: " + selectedName, "Betöltés", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Hiba a betöltés során! A fájl sérült.\n" + ex.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * A program belepesi pontja.
     * Elinditja a Controllert a Swing Event Dispatch Thread-en.
     * @param args Parancssori argumentumok (nem hasznalt).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(KamisadoController::new);
    }
}