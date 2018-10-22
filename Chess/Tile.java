package Chess;

import java.util.ArrayList;

public class Tile {

    private int tileID;
    private int x, y;
    private boolean occupied;
    private Piece pieceOnTile;
    private ArrayList<Piece> attackingPieces;
    private double tileValue;

    public Tile(int tileID, int x, int y, boolean occupied, Piece pieceOnTile, double tileValue){
        this.tileID = tileID;
        this.x = x;
        this.y = y;
        this.occupied = occupied;
        this.pieceOnTile = pieceOnTile;
        attackingPieces = new ArrayList<>();
        this.tileValue = tileValue;
    }

    public double getTileValue() {
        return tileValue;
    }

    // getters and setters
    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getTileID(){
        return tileID;
    }

    public boolean getOccupied(){
        return occupied;
    }

    public void setOccupied(boolean occupied){
        this.occupied = occupied;
    }

    public Piece getPieceOnTile(){
        return pieceOnTile;
    }

    public void setPieceOnTile(Piece piece){
        this.pieceOnTile = piece;
    }

    public void setAsAttackingPiece(Piece piece){
        attackingPieces.add(piece);
    }

    public ArrayList<Piece> getAttackingPieces() {
        return attackingPieces;
    }

    public void clearAttackingPieces(){
        attackingPieces.clear();
    }


    // DETAILS
    public String printDetails(){
        String details = "\ntileID: " + tileID + "\nx: " + x + "\ny: " + y + "\noccupied: " + occupied + "\npieceOnTile: " + pieceOnTile + "\n";
        if(occupied){
            details = details + "pieceId: " + pieceOnTile.pieceId;
        }
        if(pieceOnTile instanceof King){
            details = details + "\nCheck: " + ((King) pieceOnTile).getCheck();
        }
        details = details + "\nATTACKING THIS TILE: " + attackingPieces.size();
        for(Piece piece : attackingPieces){
            details = details + piece.printDetails();
        }
        return details;
    }
}