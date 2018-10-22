package Chess;

import NeuralNetwork.NNHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Piece {

    public int x, y;
    public int currentTileID;
    public int pieceId;
    public int promotionPiece;
    public double pieceValue;
    public String pieceColour;
    public ArrayList<int[]> validMoves;
    public ArrayList<int[]> checkPath;
    public ArrayList<int[]> protectedPieces;
    public boolean taken;

    public Piece(int currentTileID, int x, int y, String pieceColour, int pieceId){
        this.currentTileID = currentTileID;
        this.x = x;
        this.y = y;
        this.pieceColour = pieceColour;
        this.pieceId = pieceId;
        validMoves = new ArrayList<>();
        checkPath = new ArrayList<>();
        protectedPieces = new ArrayList<>();
        promotionPiece = 0;

        taken = false;
    }



    public void setPromotionPiece(int pieceNum){
        promotionPiece = pieceNum;
    }

    public int getPromotionPiece() {
        return promotionPiece;
    }

    // check for pawn promotion
    public boolean pawnPromotion(){
        return false;
    }


    // check if move being made is valid
    public boolean isValidMove(int droppedAtX, int droppedAtY) {
        boolean canMove = false;
        for (int[] validMove : validMoves) {
            if (droppedAtX == validMove[0] && droppedAtY == validMove[1]) {
                GameHandler.gameBoard.get(currentTileID).setOccupied(false);
                GameHandler.gameBoard.get(currentTileID).setPieceOnTile(null);

                // if piece is taking opponent by moving
                if(GameHandler.gameBoard.get(validMove[2]).getOccupied()){
                    for(int i = 0; i < GameHandler.gamePieces.size(); i++){
                        if(GameHandler.gamePieces.get(i).currentTileID == validMove[2]){
                            GameHandler.takenPieces.add(GameHandler.gamePieces.get(i));
                            GameHandler.gamePieces.get(i).taken = true;
                            if(GameHandler.training){
                                NNHandler.removeFromPawnPromotionList(GameHandler.gamePieces.get(i).pieceColour, GameHandler.gamePieces.get(i).getClass().getSimpleName(), GameHandler.gamePieces.get(i).pieceId);
                            }
//                            GameHandler.gamePieces.remove(GameHandler.gamePieces.get(i));
                        }
                    }
                }

                // START Check for castling
                if(this instanceof King && currentTileID - 2 >= 0){
                    if(GameHandler.gameBoard.get(currentTileID - 2).getX() == validMove[0]) {
                        Piece rook = GameHandler.gameBoard.get(currentTileID - 3).getPieceOnTile();
                        GameHandler.gameBoard.get(currentTileID - 3).setOccupied(false);
                        GameHandler.gameBoard.get(currentTileID - 3).setPieceOnTile(null);
                        rook.setCurrentTileID(GameHandler.gameBoard.get(currentTileID - 1).getTileID());
                        rook.x = GameHandler.gameBoard.get(currentTileID - 1).getX();
                        rook.y = GameHandler.gameBoard.get(currentTileID - 1).getY();
                        GameHandler.gameBoard.get(currentTileID - 1).setOccupied(true);
                        GameHandler.gameBoard.get(currentTileID - 1).setPieceOnTile(rook);
                        GameHandler.updateCastling = true;
                    }
                }
                if(this instanceof King && currentTileID + 2 <= 63){
                    if(GameHandler.gameBoard.get(currentTileID + 2).getX() == validMove[0]) {
                        Piece rook = GameHandler.gameBoard.get(currentTileID + 4).getPieceOnTile();
                        GameHandler.gameBoard.get(currentTileID + 4).setOccupied(false);
                        GameHandler.gameBoard.get(currentTileID + 4).setPieceOnTile(null);
                        rook.setCurrentTileID(GameHandler.gameBoard.get(currentTileID + 1).getTileID());
                        rook.x = GameHandler.gameBoard.get(currentTileID + 1).getX();
                        rook.y = GameHandler.gameBoard.get(currentTileID + 1).getY();
                        GameHandler.gameBoard.get(currentTileID + 1).setOccupied(true);
                        GameHandler.gameBoard.get(currentTileID + 1).setPieceOnTile(rook);
                        GameHandler.updateCastling = true;
                    }
                }
                // END check for Castling

                // update Tile and Piece objects and set firstMove to false for
                // certain pieces
                currentTileID = GameHandler.gameBoard.get(validMove[2]).getTileID();
                GameHandler.gameBoard.get(validMove[2]).setOccupied(true);
                GameHandler.gameBoard.get(validMove[2]).setPieceOnTile(this);
                x = validMove[0];
                y = validMove[1];
                if(this instanceof Pawn){
                    if((this).getFirstMove()) {
                        // check for En Passant
                        if (y - ((Pawn) this).pawnDirection() * 2 == 1 || y - ((Pawn) this).pawnDirection() * 2 == 6) {
                            if (x + 1 <= 7 && GameHandler.gameBoard.get(currentTileID + 1).getX() == x + 1
                                    && GameHandler.gameBoard.get(currentTileID + 1).getOccupied()) {
                                if (!GameHandler.gameBoard.get(currentTileID + 1).getPieceOnTile().pieceColour.equalsIgnoreCase(pieceColour)
                                        && GameHandler.gameBoard.get(currentTileID + 1).getPieceOnTile() instanceof Pawn) {
                                    int[] enPassant = {
                                            x,
                                            y - ((Pawn) this).pawnDirection(),
                                            currentTileID - ((Pawn) this).pawnTileDifference()
                                    };
                                    ((Pawn) GameHandler.gameBoard.get(currentTileID + 1).getPieceOnTile()).setEnPassantPlus(enPassant);
                                }
                            }
                            if (x - 1 >= 0 && GameHandler.gameBoard.get(currentTileID - 1).getX() == x - 1
                                    && GameHandler.gameBoard.get(currentTileID - 1).getOccupied()) {
                                if (!GameHandler.gameBoard.get(currentTileID - 1).getPieceOnTile().pieceColour.equalsIgnoreCase(pieceColour)
                                        && GameHandler.gameBoard.get(currentTileID - 1).getPieceOnTile() instanceof Pawn) {
                                    int[] enPassant = {
                                            x,
                                            y - ((Pawn) this).pawnDirection(),
                                            currentTileID - ((Pawn) this).pawnTileDifference()
                                    };
                                    ((Pawn) GameHandler.gameBoard.get(currentTileID - 1).getPieceOnTile()).setEnPassantMinus(enPassant);
                                }
                            }
                        }
                    }
                    ((Pawn) this).madeFirstMove();
                }else if(this instanceof Rook){
                    ((Rook) this).madeFirstMove();
                }else if(this instanceof King){
                    ((King) this).madeFirstMove();
                }
                canMove = true;
            }
        }
        return canMove;
    }

    // add invalid moves to ArrayList
    public void addSpuriousMoves(int counter){
        int[] spuriousMove = {-1, -1, -1};
        for(int i = counter; i < 8; i++){
            validMoves.add(spuriousMove);
        }
    }

    public double getPieceValue() {
        return pieceValue;
    }

    // check valid moves for straight lines stopping at boundaries in all directions
    public void checkStraightLines(){
        // ID differences for up, down, right and left
        int[] tileIdDifference = {-8, 8, 1, -1};
        for(int g = 0; g < 4; g++) {
            for (int i = 1; i < 8; i++) {
                int[] possibleMove = new int[3];
                // make sure check is within bounds
                if (        (g == 0 && y - i >= GameHandler.top)
                        ||  (g == 1 && y + i <= GameHandler.bottom)
                        ||  (g == 2 && x + i <= GameHandler.right)
                        ||  (g == 3 && x - i >= GameHandler.left)) {
                    if (!GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getOccupied()) {
                        possibleMove[0] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getX();
                        possibleMove[1] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getY();
                        possibleMove[2] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getTileID();
                        validMoves.add(possibleMove);
                    } else if (!GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getPieceOnTile().pieceColour.equalsIgnoreCase(pieceColour)) {
                        possibleMove[0] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getX();
                        possibleMove[1] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getY();
                        possibleMove[2] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getTileID();
                        validMoves.add(possibleMove);
                        addSpuriousMoves(i+1);
                        break;
                    } else {
                        possibleMove[0] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getX();
                        possibleMove[1] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getY();
                        possibleMove[2] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getTileID();
                        protectedPieces.add(possibleMove);
                        addSpuriousMoves(i);
                        break;
                    }
                } else {
                    addSpuriousMoves(i);
                    break;
                }
            }
        }
    }


    // check for valid moves for diagonal stopping at boundaries in all directions
    public void checkDiagonals(){
        // ID differences heading toward: topleft, topright, bottomleft, bottomright
        int[] tileIdDifference = {-9, -7, 7, 9};
        for(int g = 0; g < 4; g++) {
            for (int i = 1; i < 8; i++) {
                int[] possibleMove = new int[3];
                // make sure check is within bounds
                if (        ((g == 0 && y - i >= GameHandler.top) && (g == 0 && x - i >= GameHandler.left))
                        ||  ((g == 1 && y - i >= GameHandler.top) && (g == 1 && x + i <= GameHandler.right))
                        ||  ((g == 2 && y + i <= GameHandler.bottom) && (g == 2 && x - i >= GameHandler.left))
                        ||  ((g == 3 && y + i <= GameHandler.bottom) && (g == 3 && x + i <= GameHandler.right))) {
                    if (!GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getOccupied()) {
                        possibleMove[0] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getX();
                        possibleMove[1] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getY();
                        possibleMove[2] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getTileID();
                        validMoves.add(possibleMove);
                    } else if (!GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getPieceOnTile().pieceColour.equalsIgnoreCase(pieceColour)) {
                        possibleMove[0] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getX();
                        possibleMove[1] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getY();
                        possibleMove[2] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getTileID();
                        validMoves.add(possibleMove);
                        addSpuriousMoves(i + 1);
                        break;
                    } else {
                        possibleMove[0] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getX();
                        possibleMove[1] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getY();
                        possibleMove[2] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getTileID();
                        protectedPieces.add(possibleMove);
                        addSpuriousMoves(i);
                        break;
                    }
                } else {
                    addSpuriousMoves(i);
                    break;
                }
            }
        }
    }


    public  void determinePossibleMoves(){
        validMoves.clear();
        protectedPieces.clear();
    }

    // remove moves when in check where move doesn't get out of check
    public void removePersistentCheckMoves(){
        if(this instanceof King){
            return;
        }else if(GameHandler.checkingPieces.size() < 1){
            int[] spurious = {-1, -1, -1};
            for(int i = 0; i < validMoves.size(); i++){
                validMoves.set(i, spurious);
            }
        }else{
            int[] spurious = {-1, -1, -1};
            ArrayList<int[]> keepMoves = new ArrayList<>();
            for(int[] checkingPiece : GameHandler.checkingPieces.get(0).checkPath){
                for(int i = 0; i < validMoves.size(); i++){
                    if(validMoves.get(i)[2] == checkingPiece[2]){
                        keepMoves.add(checkingPiece);
                    }
                }
                if(this instanceof Pawn){
                    for(int i = 0; i < ((Pawn) this).getDiagonalMoves().size(); i++){
                        if(((Pawn) this).getDiagonalMoves().get(i)[2] != checkingPiece[2]){
                            ((Pawn) this).getDiagonalMoves().set(i, spurious);
                        }
                    }
                    if(((Pawn) this).getEnPassantPlus().size() > 0){
                        keepMoves.add(((Pawn) this).getEnPassantPlus().get(0));
                    }else if(((Pawn) this).getEnPassantMinus().size() > 0){
                        keepMoves.add(((Pawn) this).getEnPassantMinus().get(0));
                    }
                }
            }
            if(keepMoves.size() > 0) {
                for(int x = 0; x < validMoves.size(); x++) {
                    boolean isPresent = false;
                    for (int z = 0; z < keepMoves.size(); z++) {
                        if (Arrays.equals(validMoves.get(x), keepMoves.get(z))) {
                            isPresent = true;
                            break;
                        }
                    }
                    if (!isPresent) {
                        validMoves.set(x, spurious);
                    }
                }
            }else{
                for(int i = 0; i < validMoves.size(); i++){
                    validMoves.set(i, spurious);
                }
            }
        }
    }

    // setters and getters
    public boolean getFirstMove(){
        if(this instanceof Rook){
            return ((Rook) this).getFirstMove();
        }else if(this instanceof King){
            return ((King) this).getFirstMove();
        }else if(this instanceof Pawn){
            return ((Pawn) this).getFirstMove();
        }
        return false;
    }

    public int getCurrentTileID(){
        return currentTileID;
    }

    public void setCurrentTileID(int newTileID){
        this.currentTileID = newTileID;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public String getPieceColour() {
        return pieceColour;
    }

    // set the path from this piece to the king it is checking
    public void setCheckPath(King checkedKing){
        if((this instanceof Knight) || (this instanceof Pawn)){
            int[] path = new int[3];
            path[0] = x;
            path[1] = y;
            path[2] = currentTileID;
            checkPath.add(path);
            return;
        }
        // check if path is in a straight line
        if(x == checkedKing.getX() || y == checkedKing.getY()){
            // check which direction path heads in
            int increment = 0;
            // king is above checking piece
            if(checkedKing.getY() < y){
                increment = 1;
                for(int i = checkedKing.getY() + 1; i < y + 1; i++){
                    int[] path = new int[3];
                    path[0] = x;
                    path[1] = i;
                    path[2] = (checkedKing.getCurrentTileID()) + (8 * increment);
                    checkPath.add(path);
                    increment++;
                }
            // king is below checking piece
            }else if(checkedKing.getY() > y){
                increment = -1;
                for(int i = checkedKing.getY() - 1; i > y - 1; i--){
                    int[] path = new int[3];
                    path[0] = x;
                    path[1] = i;
                    path[2] = (checkedKing.getCurrentTileID()) + (8 * increment);
                    checkPath.add(path);
                    increment--;
                }
            // king is to the left of the checking piece
            }else if(checkedKing.getX() < x){
                increment = 1;
                for(int i = checkedKing.getX() + 1; i < x + 1; i++){
                    int[] path = new int[3];
                    path[0] = i;
                    path[1] = y;
                    path[2] = (checkedKing.getCurrentTileID()) + increment;
                    checkPath.add(path);
                    increment++;
                }
            // king is to the right of the checking piece
            }else if(checkedKing.getX() > x){
                increment = -1;
                for(int i = checkedKing.getX() - 1; i > x - 1; i--){
                    int[] path = new int[3];
                    path[0] = i;
                    path[1] = y;
                    path[2] = (checkedKing.getCurrentTileID()) + (increment);
                    checkPath.add(path);
                    increment--;
                }
            }
        }else{
            int increment = 0;
            // king is to the top left of the checking piece
            if(checkedKing.getX() < x && checkedKing.getY() < y){
                increment = 9;
                for(int i = 1; i < (y - checkedKing.getY()) + 1; i++){
                    int[] path = new int[3];
                    path[0] = checkedKing.getX() + i;
                    path[1] = checkedKing.getY() + i;
                    path[2] = (checkedKing.getCurrentTileID()) + (increment * i);
                    checkPath.add(path);
                }
            // king is to the top right of the checking piece
            }else if(checkedKing.getX() > x && checkedKing.getY() < y){
                increment = 7;
                for(int i = 1; i < (y - checkedKing.getY()) + 1; i++){
                    int[] path = new int[3];
                    path[0] = checkedKing.getX() - i;
                    path[1] = checkedKing.getY() + i;
                    path[2] = (checkedKing.getCurrentTileID()) + (increment * i);
                    checkPath.add(path);
                }
            // king is to the bottom right of the checking piece
            }else if(checkedKing.getX() > x && checkedKing.getY() > y){
                increment = -9;
                for(int i = 1; i < (checkedKing.getY() - y) + 1; i++){
                    int[] path = new int[3];
                    path[0] = checkedKing.getX() - i;
                    path[1] = checkedKing.getY() - i;
                    path[2] = (checkedKing.getCurrentTileID()) + (increment * i);
                    checkPath.add(path);
                }
            // king is to the bottom left of the checking piece
            }else if(checkedKing.getX() < x && checkedKing.getY() > y){
                increment = -7;
                for(int i = 1; i < (checkedKing.getY() - y) + 1; i++){
                    int[] path = new int[3];
                    path[0] = checkedKing.getX() + i;
                    path[1] = checkedKing.getY() - i;
                    path[2] = (checkedKing.getCurrentTileID()) + (increment * i);
                    checkPath.add(path);
                }
            }
        }
    }

    public ArrayList<int[]> getCheckPath() {
        return checkPath;
    }

    public String printDetails(){
        String details = "";
        details = details + "\nTileID: " + currentTileID + "\n" + this.getClass().getSimpleName() + "\nx: " + x + "\ny: " + y + "\npieceColour: " + pieceColour + "\n---";
        return details;
    }
}
