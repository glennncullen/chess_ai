package PortableGameNotation;
import Chess.GameHandler;

import java.util.Arrays;


public class Move {
    private String pieceColour;
    private String pieceName;
    private int[] move;
    private String pgnRef;
    private String endGame;
    private boolean castling;
    private boolean pawnPromotion;
    private int promotionPieceName;
    private int comingFromX, comingFromY;


    public Move(){
        pieceColour = "";
        pieceName = "";
        move = new int[3];
        pgnRef = "";
        endGame = "";
        castling = false;
        pawnPromotion = false;
        promotionPieceName = 0;
        comingFromX = -1;
        comingFromY = -1;
    }

    public int getComingFromY() {
        return comingFromY;
    }

    public void setComingFromY(int comingFromY) {
        this.comingFromY = comingFromY;
    }

    public String printMove(){
        return pieceColour + " " + pieceName + " to " + pgnRef + "    \tMOVE: " + Arrays.toString(move) + "  \tCASTLING: " + castling + "  \tPAWNPROMOTION: " + pawnPromotion + "  \tPROMO PIECE: " + promotionPieceName + "  \tCOMINGFROM-X: " + comingFromX + "  \tCOMINGFROM-Y: " + comingFromY + "  \tENDGAME: " + endGame;
    }

    public boolean isPawnPromotion() {
        return pawnPromotion;
    }

    public void setPawnPromotion(boolean pawnPromotion) {
        this.pawnPromotion = pawnPromotion;
    }

    public int getPromotionPiece() {
        return promotionPieceName;
    }

    public void setPromotionPiece(int promotionPieceName) {
        this.promotionPieceName = promotionPieceName;
    }

    public int getComingFromX() {
        return comingFromX;
    }

    public void setComingFromX(int comingFromX) {
        this.comingFromX = comingFromX;
    }

    public String getPieceColour() {
        return pieceColour;
    }

    public void setPieceColour(String pieceColour) {
        this.pieceColour = pieceColour;
    }

    public String getPieceName() {
        return pieceName;
    }

    public void setPieceName(String pieceName) {
        this.pieceName = pieceName;
    }

    public int[] getMove() {
        return move;
    }

    public void setMove(int[] move) {
        this.move = move;
    }

    public String getPgnRef() {
        return pgnRef;
    }

    public void setPgnRef(String pgnRef) {
        this.pgnRef = pgnRef;
    }

    public String getEndGame() {
        return endGame;
    }

    public void setEndGame(String endGame) {
        this.endGame = endGame;
    }

    public boolean isCastling() {
        return castling;
    }

    public void setCastling(boolean castling) {
        this.castling = castling;
    }
}
