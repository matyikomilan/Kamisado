package kamisado.view;

import javax.imageio.ImageIO;
import javax.swing.*;

import kamisado.model.Colour;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

/**
* A fomenut reprezentalo panel.
*/
public class MenuPanel extends JPanel {
    /** Aa hatterkep */
    private Image backgroundImage;
    
    /**
    * Konstruktor.
    * @param controller A gombok esemenykezeloje.
    */
    public MenuPanel(ActionListener controller) {

        //hatter betoltese:
        int imgWidth = 800; // ha nem sikerult betolteni
        int imgHeight = 600;

        try {
            backgroundImage = ImageIO.read(new File("pics/editedbackground.png"));
        } catch (IOException e) {
            // Ha nincs kép, legyen sötétszürke a háttér (fallback)
            setBackground(Color.DARK_GRAY);
        }

        // Így a pack() hívásnál az ablak pont akkora lesz, mint a kép => Nincs torzulás
        this.setPreferredSize(new Dimension(imgWidth, imgHeight));

        setLayout(new GridBagLayout()); // Középre igazítás

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 30, 10, 30); // terkoz
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // cim
        OutlinedLabel title = new OutlinedLabel("KAMISADO");

        GridBagConstraints titleGbc = (GridBagConstraints) gbc.clone();
        titleGbc.insets = new Insets(10, 0, 60, 0); // Nagyobb hely a cím alatt
        titleGbc.fill = GridBagConstraints.CENTER;
        add(title, titleGbc);

        //gombok:
        addButton("Új Egyszerű Játék", "NEW_SIMPLE", controller, gbc);
        addButton("Új Standard Játék", "NEW_STANDARD", controller, gbc);
        addButton("Játék Betöltése", "LOAD_GAME", controller, gbc);
        addButton("Szabályok", "SHOW_RULES", controller, gbc);
        addButton("Kilépés", "EXIT", controller, gbc);
    }

    //hatterkep kirajzolasahoz:
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // A kép kirajzolása a panel teljes méretére nyújtva
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
    * Gomb hozzaadasa.
    * @param text Szoveg.
    * @param command Parancs.
    * @param listener Kezelo.
    * @param gbc Layout constraint.
    */
    private void addButton(String text, String command, ActionListener listener, GridBagConstraints gbc) {
        JButton btn = new JButton(text);
        // szepitgetes:_______________________________________________
        //hatterszin
        btn.setBackground(new Color(0, 0, 0));
        btn.setForeground(Color.WHITE);
        //szepitgetes vege_________________________________

        btn.setActionCommand(command);
        btn.addActionListener(listener);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 18));
        btn.setPreferredSize(new Dimension(250, 50));
        btn.setFocusPainted(false);
        add(btn, gbc);
    }

    // segedcuccok a "KAMISADO" szep kiirasahoz:
    /**
     * A fomenuben a "KAMISADO" szoveg kiirasat segito osztaly */
    private static class OutlinedLabel extends JComponent {
        /** A felirat szovege. */
        private String text;
        /** A betutipus. */
        private Font font;
        /** A betuk szinei. */
        private Color[] letterColors;

        /** Konstruktor.
         * @param text Szoveg. */
        public OutlinedLabel(String text) {
            this.text = text;
            this.font = new Font("SansSerif", Font.BOLD, 72); 
            this.setPreferredSize(new Dimension(600, 120)); // Kicsit nagyobbra vettem a dupla keret miatt

            // "KAMISADO" cimszoveg karakterenkent random szinnel:
            letterColors = new Color[text.length()];
            java.util.Random rand = new java.util.Random();
            
            // Lekérjük az összes lehet séges színt a modelből
            Colour[] gameColours = Colour.values();

            for (int i = 0; i < text.length(); i++) {
                // Véletlenszerűen választunk egyet a játék színei közül
                letterColors[i] = gameColours[rand.nextInt(gameColours.length)].getColour();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            
            // Élsimítás a szép ívekért
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE); // Pontosabb vonalvastagság

            // Szöveg alakzatának (Glyphs) létrehozása
            GlyphVector gv = font.createGlyphVector(g2d.getFontRenderContext(), text);
            Rectangle bounds = gv.getOutline().getBounds();
            
            // Középre igazítás transzformációja
            double startX = (getWidth() - bounds.getWidth()) / 2 - bounds.getX();
            double startY = (getHeight() - bounds.getHeight()) / 2 - bounds.getY();
            AffineTransform transform = AffineTransform.getTranslateInstance(startX, startY);

            // --- BETŰNKÉNTI RAJZOLÁS 3 RÉTEGBEN ---
            for (int i = 0; i < gv.getNumGlyphs(); i++) {
                Shape glyph = gv.getGlyphOutline(i);
                Shape centeredGlyph = transform.createTransformedShape(glyph);

                // 1. RÉTEG: KÜLSŐ KERET (Vastag Fekete)
                // Ez lesz a legszélesebb, ez adja a külső kontúrt
                g2d.setStroke(new BasicStroke(12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setColor(Color.BLACK);
                g2d.draw(centeredGlyph);

                // 2. RÉTEG: BELSŐ KERET (Közepes Fehér)
                // Ez választja el a feketét a színtől
                g2d.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setColor(Color.WHITE);
                g2d.draw(centeredGlyph);

                // 3. RÉTEG: KITÖLTÉS (A választott szín)
                // Ez megy legfelülre
                g2d.setColor(letterColors[i]);
                g2d.fill(centeredGlyph);
            }
        }
    }
}