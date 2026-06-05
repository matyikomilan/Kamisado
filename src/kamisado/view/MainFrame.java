package kamisado.view;

import javax.swing.*;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import kamisado.model.GameState;

/**
 * A jatek foablaka (JFrame).
 * Ez az osztaly felel a kulonbozo kepernyok (MenuPanel, GamePanel) kozotti valtasert,
 * a menusor (MenuBar) kezeleseert es az ablak megjeleniteseert.
 */
public class MainFrame extends JFrame {
    
    /** A jatekter panelje. */
    private GamePanel gamePanel;
    /** A menupanel. */
    private MenuPanel menuPanel;
    /** A Controllert reprezentalo Listener. */
    private ActionListener controller;

    /**
     * Letrehozza a foablakot, beallitja az ikont es megjeleniti a fomenut.
     * @param ctrlr A Controller, ami a gombok esemenyeit kezeli.
     */
    public MainFrame(ActionListener ctrlr) {
        controller = ctrlr;

        setTitle("Kamisado");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- IKON BEÁLLÍTÁSA ---
        try {
            // Betöltjük a képet a pics mappából
            Image icon = javax.imageio.ImageIO.read(new java.io.File("pics/icon.png"));
            
            // Beállítjuk az ablak ikonjának (fejléc + tálca)
            this.setIconImage(icon);
            
        } catch (java.io.IOException e) {
            // Nem omlunk össze, ha nincs meg
        }
        
        showMainMenu();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Elinditja a jateknezetet.
     * Lecsereli a fomenut a jatekterre (GamePanel).
     * @param gs A kezdeti jatekallapot.
     * @param ml Az egerelemeny-kezelo (maga a Controller).
     */
    public void showGame(GameState gs, MouseListener ml){
        gamePanel = new GamePanel(gs);
        gamePanel.addMouseListener(ml);

        setJMenuBar(setupMenuBar(controller));
        setContentPane(gamePanel);

        gamePanel.requestFocusInWindow();
        revalidate();
        pack();

        setLocationRelativeTo(null);
        repaint();
    }

    /**
     * Letrehozza es osszeallitja az ablak felso menusorat (JMenuBar).
     * Hozzaadja a menupontokat (Uj jatek, Mentes, Kilepes) es hozzarendeli
     * a Controller esemenykezelojet a gombokhoz.
     * @param controller Az esemenyeket kezelo ActionListener.
     * @return A kesz menusor.
     */
    private JMenuBar setupMenuBar(ActionListener controller) {
        JMenuBar menuBar = new JMenuBar();
        
        //fajlmenu
        JMenu fileMenu = new JMenu("Fájl");
        
        // uj jatek
        //egyszeru:
        JMenuItem newSimpleGame = new JMenuItem("Új Egyszerű Játék");
        newSimpleGame.setActionCommand("NEW_SIMPLE"); 
        newSimpleGame.addActionListener(controller);
        //standard:
        JMenuItem newStandardGame = new JMenuItem("Új Standard Játék");
        newStandardGame.setActionCommand("NEW_STANDARD"); 
        newStandardGame.addActionListener(controller);
        
        // mentes
        JMenuItem saveGame = new JMenuItem("Játék Mentése");
        saveGame.setActionCommand("SAVE_GAME");
        saveGame.addActionListener(controller);
        
        // betoltes
        JMenuItem loadGame = new JMenuItem("Játék Betöltése");
        loadGame.setActionCommand("LOAD_GAME");
        loadGame.addActionListener(controller);

        //vissza a fomenube:
        JMenuItem backToMenu = new JMenuItem("Vissza a Főmenübe");
        backToMenu.setActionCommand("BACK_TO_MENU");
        backToMenu.addActionListener(controller);
        
        // exit
        JMenuItem exit = new JMenuItem("Kilépés");
        exit.setActionCommand("EXIT");
        exit.addActionListener(controller);

        fileMenu.add(backToMenu);
        fileMenu.addSeparator();
        fileMenu.add(newSimpleGame);
        fileMenu.add(newStandardGame);
        fileMenu.addSeparator(); 
        fileMenu.add(saveGame);
        fileMenu.add(loadGame);
        fileMenu.addSeparator();
        fileMenu.add(exit);
        
        // --- Segítség Menü ---
        JMenu helpMenu = new JMenu("Segítség");
        
        // Szabályok (kötelező a specifikáció szerint)
        JMenuItem rules = new JMenuItem("Szabályok");
        rules.setActionCommand("SHOW_RULES");
        rules.addActionListener(controller);
        
        helpMenu.add(rules);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    /**
     * Megjeleniti a jatekszabalyokat egy felugro ablakban.
     * A szoveget a 'rules.txt' fajlbol olvassa be.
     */
    public void showRules(){
        String rules = loadRules();

        //kiiras:
        JOptionPane.showMessageDialog(this, rules, "Játékszabályok", JOptionPane.INFORMATION_MESSAGE);
    }

    //segedfv az elozohoz:
    /**
     * Beolvassa a jatekszabalyokat a 'rules.txt' fajlbol.
     * @return A szabalyzat szovege, vagy hibaüzenet, ha a fajl nem talalhato.
     */
    private String loadRules() {
        File file = new File("rules.txt");
        StringBuilder content = new StringBuilder();

        // Ellenőrizzük, létezik-e a fájl
        if (!file.exists()) {
            return "rules.txt nem létezik.";
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            return "Hiba történt a szabályok beolvasása közben:\n" + e.getMessage();
        }

        return content.toString();
    }

    /**
     * Visszaadja a jatekpanelt (a teszteleshez vagy a controller szamara).
     * @return A GamePanel referenciaja.
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * Megjeleniti a fomenut.
     * Betolti a MenuPanel-t es eltavolitja a menusort.
     */
    public void showMainMenu() {
        if (menuPanel == null) {
            menuPanel = new MenuPanel(controller);
        }
        setJMenuBar(null);  //itt nincs menusor
        setContentPane(menuPanel);

        pack(); 
        setLocationRelativeTo(null);


        revalidate();
        repaint();
    }

}