package Chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Pawn extends Piece {

    private boolean firstMove;
    private ArrayList<int[]> diagonalMoves, enPassantMinus, enPassantPlus;

    // constructor
    public Pawn(int currentTileID, int x, int y, String pieceColour, int pieceId){
        super(currentTileID, x, y, pieceColour, pieceId);
        firstMove = true;
        enPassantMinus = new ArrayList<>();
        enPassantPlus = new ArrayList<>();
        pieceValue = 1;
        diagonalMoves = new ArrayList<>();
    }

    // check if pawn has reached opposite side of the board
    public boolean pawnPromotion() {
        if(y == 7 || y == 0){
            return true;
        }
        return false;
    }


    // get the list of available moves for pawn
    @Override
    public void determinePossibleMoves(){
        super.determinePossibleMoves();
        diagonalMoves.clear();

//        System.out.println(pieceId);

        if(!taken) {
            if (pieceColour.equalsIgnoreCase("white") && y + pawnDirection() > GameHandler.bottom) {
                return;
            } else if (pieceColour.equalsIgnoreCase("black") && y + pawnDirection() < GameHandler.top) {
                return;
            }

            int[] spurious1 = {-1, -1, -1};
            int[] spurious2 = {-1, -1, -1};
            int[] spurious3 = {-1, -1, -1};
            int[] spurious4 = {-1, -1, -1};
            if (y + pawnDirection() <= 7 && y + pawnDirection() >= 0) {
                // check if the tile in front of the pawn is occupied
                if (!GameHandler.gameBoard.get(currentTileID + pawnTileDifference()).getOccupied()) {
                    int[] possibleFirstMove = new int[3];
                    possibleFirstMove[0] = GameHandler.gameBoard.get(currentTileID + pawnTileDifference()).getX();
                    possibleFirstMove[1] = GameHandler.gameBoard.get(currentTileID + pawnTileDifference()).getY();
                    possibleFirstMove[2] = currentTileID + pawnTileDifference();
                    validMoves.add(possibleFirstMove);
                    if (firstMove) {
                        // if its the pawn's first move, check the second square
                        if (!GameHandler.gameBoard.get(currentTileID + (pawnTileDifference() * 2)).getOccupied()) {
                            int[] possibleSecondMove = new int[3];
                            possibleSecondMove[0] = GameHandler.gameBoard.get(currentTileID + (pawnTileDifference() * 2)).getX();
                            possibleSecondMove[1] = GameHandler.gameBoard.get(currentTileID + (pawnTileDifference() * 2)).getY();
                            possibleSecondMove[2] = currentTileID + (pawnTileDifference() * 2);
                            validMoves.add(possibleSecondMove);
                        } else {
                            validMoves.add(spurious2);
                        }
                    } else {
                        validMoves.add(spurious2);
                    }
                } else {
                    validMoves.add(spurious1);
                    validMoves.add(spurious2);
                }
            } else {
                validMoves.add(spurious1);
                validMoves.add(spurious2);
            }

            if (y + pawnDirection() <= 7 && y - pawnDirection() >= 0) {
                // check if a piece is able to be taken by moving diagonally and if diagonal tiles are unoccupied, add
                // to diagonalMoves ArrayList so King can't move into check
                if (x - 1 >= 0) {
                    if (GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) - 1).getX() == x - 1
                            && GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) - 1).getY() == y + pawnDirection()) {
                        if (GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) - 1).getOccupied()
                                && !GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) - 1).getPieceOnTile().getPieceColour().equalsIgnoreCase(pieceColour)) {
                            int[] possibleMove = new int[3];
                            possibleMove[0] = GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) - 1).getX();
                            possibleMove[1] = GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) - 1).getY();
                            possibleMove[2] = GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) - 1).getTileID();
                            diagonalMoves.add(possibleMove);
                            validMoves.add(possibleMove);
                        } else {
                            int[] diagonalMove = new int[3];
                            diagonalMove[0] = GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) - 1).getX();
                            diagonalMove[1] = GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) - 1).getY();
                            diagonalMove[2] = GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) - 1).getTileID();
                            diagonalMoves.add(diagonalMove);
                            validMoves.add(spurious3);
                        }
                    }
                } else {
                    validMoves.add(spurious3);
                }

                if (x + 1 <= 7) {
                    if (GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) + 1).getX() == x + 1
                            && GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) + 1).getY() == y + pawnDirection()) {
                        if (GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) + 1).getOccupied()
                                && !GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) + 1).getPieceOnTile().getPieceColour().equalsIgnoreCase(pieceColour)) {
                            int[] possibleMove = new int[3];
                            possibleMove[0] = GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) + 1).getX();
                            possibleMove[1] = GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) + 1).getY();
                            possibleMove[2] = GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) + 1).getTileID();
                            diagonalMoves.add(possibleMove);
                            validMoves.add(possibleMove);
                        } else {
                            int[] diagonalMove = new int[3];
                            diagonalMove[0] = GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) + 1).getX();
                            diagonalMove[1] = GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) + 1).getY();
                            diagonalMove[2] = GameHandler.gameBoard.get((currentTileID + pawnTileDifference()) + 1).getTileID();
                            diagonalMoves.add(diagonalMove);
                            if(GameHandler.gameBoard.get(diagonalMove[2]).getOccupied()){
                                protectedPieces.add(diagonalMove);
                            }
                            validMoves.add(spurious4);
                        }
                    }
                } else {
                    validMoves.add(spurious4);
                }
            } else {
                validMoves.add(spurious3);
                validMoves.add(spurious4);
            }


            // EN PASSANT
            if (enPassantPlus.size() > 0) {
                validMoves.set(2, enPassantPlus.get(0));
            }

            if (enPassantMinus.size() > 0) {
                validMoves.set(3, enPassantMinus.get(0));
            }
//            System.out.println(pieceId + "\tvalidMoves: " + validMoves.size());
        }else{
            for(int i = 0; i < 4; i++){
                int[] spurious = {-1, -1, -1};
                validMoves.add(spurious);
            }
//            System.out.println(pieceId + "\tvalidMoves: " + validMoves.size() + "\tTAKEN");
        }
//        for(int i = 0; i < validMoves.size(); i++){
//            System.out.println(Arrays.toString(validMoves.get(i)));
//        }
    }

    // returns the amount to change the y axis depending on colour
    public int pawnDirection(){
        if(pieceColour.equalsIgnoreCase("white")){
            return 1;
        }else{
            return -1;
        }
    }

    // returns the amount to change the id depending on colour
    int pawnTileDifference(){
        if(pieceColour.equalsIgnoreCase("white")){
            return 8;
        }else{
            return -8;
        }
    }



    // SETTERS AND GETTERS
    public void madeFirstMove(){
        firstMove = false;
    }

    public boolean getFirstMove(){
        return firstMove;
    }

    public ArrayList<int[]> getDiagonalMoves() {
        return diagonalMoves;
    }

    public void setEnPassantMinus(int[] enPassantMove) {
        enPassantMinus.add(enPassantMove);
    }

    public void setEnPassantPlus(int[] enPassantMove) {
        enPassantPlus.add(enPassantMove);
    }

    public ArrayList<int[]> getEnPassant(){
        if(enPassantPlus.size() > 0){
            return enPassantPlus;
        }else{
            return enPassantMinus;
        }
    }

    public ArrayList<int[]> getEnPassantPlus(){
        return enPassantPlus;
    }

    public ArrayList<int[]> getEnPassantMinus(){
        return enPassantMinus;
    }

    public void clearEnPassant(){
        enPassantMinus.clear();
        enPassantPlus.clear();
    }
}
