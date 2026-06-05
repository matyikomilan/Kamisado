package kamisado.model;

import java.io.Serializable;
import java.util.Collections;

/**
 * A jatek aktualis allapotat tarolo osztaly.
 * Tartalmazza a tablat, a soron levo jatekost, a pontszamokat es a jatekmodot.
 * Ez az osztaly szerializalhato, igy alkalmas a jatek mentesere es betoltesere.
 */
public class GameState implements Serializable{
    /** A lehetseges jatekmodok. */
    public enum GameMode{
        /** Egyetlen menetbol allo jatek. */
        SIMPLE,
        /** Pontgyujto jatek (3 pontig), szumoval. */
        STANDARD
    }

    /** Az aktualis jatekmod (SIMPLE/STANDARD). */
    private GameMode mode;
    /** A jatektabla objektum. */
    private Board board;
    /** Az aktualisan soron levo jatekos. */
    private Player currentPlayer;
    /** A kovetkezo lepes kotelezo szine (null, ha nincs). */
    private Colour nextMoveColour;

    // standard modhoz kell meg:
    /** A Fekete jatekos pontszama. */
    private int scoreBlack;
    /** A Feher jatekos pontszama. */
    private int scoreWhite;

    //konstruktor:
    /**
     * Letrehoz egy uj jatekot a megadott modban.
     * Inicializalja a tablat, nullazza a pontokat, es beallitja a Fekete jatekost kezdokent.
     * @param m A kivalasztott jatekmod (SIMPLE vagy STANDARD).
     */
    public GameState(GameMode m){
        mode = m;
        board = new Board();
        currentPlayer = Player.BLACK;
        nextMoveColour = null;
        scoreBlack = 0;
        scoreWhite = 0;
    }

    //getterek:
    /** Visszaadja az aktualis jatekmodot.
     * @return A beallitott jatekmod (SIMPLE vagy STANDARD). */
    public GameMode getMode(){
        return mode;
    }

    /** Visszaadja a jatektablat.
     * @return Board objektum referenciaja. */
    public Board getBoard(){
        return board;
    }

    /** Visszaadja a soron levo jatekost.
     * @return A soron levo jatekos (BLACK vagy WHITE).. */
    public Player getCurrentPlayer(){
        return currentPlayer;
    }

    /** * Visszaadja a kotelezo szint a kovetkezo lepeshez.
     * @return Colour, vagy null, ha barmelyik toronnyal lehet lepni.
     */
    public Colour getNextMoveColour(){
        return nextMoveColour;
    }

    /** Visszaadja a Fekete jatekos pontszamat.
     * @return Pontszam. */
    public int getScoreBlack(){
        return scoreBlack;
    }

    /** Visszaadja a Feher jatekos pontszamat.
     * @return Pontszam. */
    public int getScoreWhite(){
        return scoreWhite;
    }

    // jatekmenetet frissito tagfvk
    /**
     * Atvaltja a soron levo jatekost a masik felre.
     * (Fekete -> Feher, vagy Feher -> Fekete).
     */
    public void switchPlayer(){
        if(currentPlayer == Player.BLACK){
            currentPlayer = Player.WHITE;
        } else{
            currentPlayer = Player.BLACK;
        }
    }

    /**
     * Beallitja a szint, amely meghatarozza, melyik toronnyal kell lepni legkozelebb.
     * @param c A kotelezo szin (lehet null is a jatek elejen).
     */
    public void setNextMoveColour(Colour c){
        nextMoveColour = c;
    }

    /**
     * Noveli a megadott jatekos pontszamat.
     * Csak Standard modban van hatasa.
     * @param p A jatekos, aki a pontot kapja.
     * @param n A kapott pontok szama.
     */
    public void addScore(Player p, int n){
        if(p == Player.BLACK){
            scoreBlack += n;
        } else{
            scoreWhite += n;
        }
    }

    //lepes vegrehajtasa:
    /**
     * Vegrehajt egy lepest a tablan, es kezeli a jatekmenet logikajat.
     * Ketfele mozgast kulonboztet meg:
     * 1. Normal lepes: Egyszeru athelyezes es jatekosvaltas.
     * 2. Szumo lokes: Ellenfel eltolasa, nincs jatekosvaltas (lancreakcio).
     * Kezeli a blokkolast (Lepeskenyszer atadasa) is.
     * @param fromp A lepest indito mezo koordinataja.
     * @param top A celmezo koordinataja (vagy az ellenfel pozicioja lokesnel).
     * @return EndOfTurn enum, ami jelzi a kor kimenetelet (WIN, BLOCKED, NEXT).
     */
    public EndOfTurn move(Position fromp, Position top){
        // SIMA lepes VAGY SUMO LOKES????
        Tower targetT = board.getTowerAt(top.getRow(), top.getCol());
        //sima lepes
        if(targetT == null){
            board.moveTowerFromTo(fromp.getRow() ,fromp.getCol(), top.getRow(), top.getCol());

            // gyozelemcheck:
            if(GameLogic.winCheck(currentPlayer, top.getRow())){
                if(mode == GameMode.STANDARD){
                    addScore(currentPlayer, 1);
                    board.getTowerAt(top.getRow(), top.getCol()).upgradeSumoLevel(1);
                }
                return EndOfTurn.WIN;
            }
            setNextMoveColour(board.getTileColour(top.getRow(), top.getCol()));
            switchPlayer();
        } else{     //SUMO LOKES:
            if(GameLogic.canSumoPush(board, fromp, top)){
                
                // nem volt jo az: if fekete --> +1 else -1, ezert nesze sokkal bonyolultabban hatha:
                int dRow = top.getRow() - fromp.getRow();
                int dCol = top.getCol() - fromp.getCol();
                int landr = top.getRow() + dRow;
                int landc = top.getCol() + dCol;

            // aldozat hatralep:
            board.moveTowerFromTo(top.getRow(), top.getCol(), landr, landc);

            //helyere a SUMO:
            board.moveTowerFromTo(fromp.getRow(), fromp.getCol(), top.getRow(), top.getCol());

            //kovi szin amire a lokott lokodott:
            setNextMoveColour(board.getTileColour(landr, landc));
            }
        }

        // HA blokkolva lett az ellenfel, akkor BLOCKED:
        if(GameLogic.blockCheck(this)){

            Position blockedPos = GameLogic.getTowerPos(this);

            Colour underBlockedColour = null;

            // blokkolttoronyhelyzete --> alatta levo mezo szine az uj nextmovecolour:
            if(blockedPos != null){
                underBlockedColour = board.getTileColour(blockedPos.getRow(), blockedPos.getCol());
            }

            switchPlayer();

            setNextMoveColour(underBlockedColour);
            
            return EndOfTurn.BLOCKED;
        }
        return EndOfTurn.NEXT; 
    }

    /**
     * Elokesziti a kovetkezo kort a Standard modban (pontszerzes utan).
     * - Menti a babuk jelenlegi tipusait es szintjeit.
     * - Ujrarendezi a tablat a szabalyok szerint (bazistol valo tavolsag alapjan).
     * - A vesztes jatekos kezd.
     * @param fillFromRight Meghatarozza a feltoltes iranyat (Balrol vagy Jobbrol).
     */
    public void nextRound(boolean fillFromRight) {

        // segedosztaly: torony + pozicioja
        class TowerInfo {
            Tower t;
            int r;
            int c;
            TowerInfo(Tower t, int r, int c) { this.t = t; this.r = r; this.c = c; }
        }
        
        java.util.List<TowerInfo> bInfos = new java.util.ArrayList<>();
        java.util.List<TowerInfo> wInfos = new java.util.ArrayList<>();

        for(int r=0; r<Board.SIZE; r++){
            for(int c=0; c<Board.SIZE; c++){
                Tower t = board.getTowerAt(r, c);
                if(t != null) {
                    if(t.getPlayer() == Player.BLACK) bInfos.add(new TowerInfo(t, r, c));
                    else wInfos.add(new TowerInfo(t, r, c));
                }
            }
        }

        //SORBARENDEZES: bazistol tavolodva: soron belul balrol jobbra:

        // fekete rendezes:
        Collections.sort(bInfos, (a, b) -> {
            if (a.r != b.r) return Integer.compare(a.r, b.r); //sor szerint NOVEKVO (bazistol el)
            return Integer.compare(a.c, b.c); // oszlop szerint novekvo (balrol jobbra)
        });

        //feher rendezese:
        Collections.sort(wInfos, (a, b) -> {
            if (a.r != b.r) return Integer.compare(b.r, a.r); //sor szerint CSOKKENO (bazistol el)
            return Integer.compare(b.c, a.c);       // "balrol jobbra" --> itt? 7 -> 0
        });

        // TABLA SETUP: tablatisztitas + tornyok felhelyezese bonyolultan:
        //tisztitas:
        for(int r=0; r<Board.SIZE; r++){
            for(int c=0; c<Board.SIZE; c++){
                board.setTowerAt(r, c, null); 
            }
        }

        //felhelyezes:
        for (int i = 0; i < Board.SIZE; i++) {
            int bCol = fillFromRight ? (7 - i) : i;
            if (i < bInfos.size()) {
                board.setTowerAt(0, bCol, bInfos.get(i).t);
            }

            int wCol = fillFromRight ? i : (7 - i);
            if (i < wInfos.size()) {
                board.setTowerAt(7, wCol, wInfos.get(i).t);
            }
        }

        // vesztes kezd:
        this.currentPlayer = this.currentPlayer.getOpponent();
        this.nextMoveColour = null;
    }
}