package Chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class King extends Piece {

    private boolean firstMove, check, checkMate, staleMate;

    King(int currentTileID, int x, int y, String pieceColour, int pieceId) {
        super(currentTileID, x, y, pieceColour, pieceId);
        firstMove = true;
        check = false;
        checkMate = false;
        staleMate = false;
        pieceValue = 20;
    }


    @Override
    public void determinePossibleMoves(){
        super.determinePossibleMoves();

        for(int i = 0; i < 9; i++){
            int[] spurious = {-1, -1, -1};
            validMoves.add(spurious);
        }

        int[] tileIdDifference = {-9, -8, -7, 1, 9, 8, 7, -1};
        for(int g = 0; g < tileIdDifference.length; g++) {
            int[] possibleMove = new int[3];
            if (        ((g == 0) && (y - 1 >= GameHandler.top && x - 1 >= GameHandler.left))
                    ||  ((g == 1) && (y - 1 >= GameHandler.top))
                    ||  ((g == 2) && (x + 1 <= GameHandler.right && y - 1 >= GameHandler.top))
                    ||  ((g == 3) && (x + 1 <= GameHandler.right))
                    ||  ((g == 4) && (y + 1 <= GameHandler.bottom && x + 1 <= GameHandler.right))
                    ||  ((g == 5) && (y + 1 <= GameHandler.bottom))
                    ||  ((g == 6) && (x - 1 >= GameHandler.left && y + 1 <= GameHandler.bottom))
                    ||  ((g == 7) && (x - 1 >= GameHandler.left))){
                if(currentTileID + tileIdDifference[g] >= 0 && currentTileID + tileIdDifference[g] <= 63) {
                    if (!GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getOccupied()) {
                        possibleMove[0] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getX();
                        possibleMove[1] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getY();
                        possibleMove[2] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getTileID();
                        validMoves.set(g, possibleMove);
                    } else if (!GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getPieceOnTile().pieceColour.equalsIgnoreCase(pieceColour)) {
                        possibleMove[0] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getX();
                        possibleMove[1] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getY();
                        possibleMove[2] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getTileID();
                        validMoves.set(g, possibleMove);
                    }
                }
            }
        }

        // Check castling
        if(firstMove && GameHandler.gameBoard.get(currentTileID - 3).getPieceOnTile() != null) {
            if (GameHandler.gameBoard.get(currentTileID - 3).getPieceOnTile().getFirstMove()
                    && !GameHandler.gameBoard.get(currentTileID - 2).getOccupied()
                    && !GameHandler.gameBoard.get(currentTileID - 1).getOccupied()) {
                int[] possibleMove = new int[3];
                possibleMove[0] = GameHandler.gameBoard.get(currentTileID - 2).getX();
                possibleMove[1] = GameHandler.gameBoard.get(currentTileID - 2).getY();
                possibleMove[2] = GameHandler.gameBoard.get(currentTileID - 2).getTileID();
                validMoves.add(possibleMove);
            }
        }
        if(firstMove && GameHandler.gameBoard.get(currentTileID +4).getPieceOnTile() != null) {
            if ( GameHandler.gameBoard.get(currentTileID + 4).getPieceOnTile().getFirstMove()
                    && !GameHandler.gameBoard.get(currentTileID + 3).getOccupied()
                    && !GameHandler.gameBoard.get(currentTileID + 2).getOccupied()
                    && !GameHandler.gameBoard.get(currentTileID + 1).getOccupied()) {
                int[] possibleMove = new int[3];
                possibleMove[0] = GameHandler.gameBoard.get(currentTileID + 2).getX();
                possibleMove[1] = GameHandler.gameBoard.get(currentTileID + 2).getY();
                possibleMove[2] = GameHandler.gameBoard.get(currentTileID + 2).getTileID();
                validMoves.add(possibleMove);
            }
        }
    }


    // remove moves that allow king to move into check
    void removeMovesIntoCheck() {
        ArrayList<int[]> movesToBeRemoved = new ArrayList<>();
        for (int i = 0; i < validMoves.size(); i++) {
            int[] spurious = {-1, -1, -1};
            if(!Arrays.equals(validMoves.get(i), spurious)) {
                int[] kingMove = validMoves.get(i);
                int tileID = kingMove[2];
                for (Piece attackingPiece : GameHandler.gameBoard.get(tileID).getAttackingPieces()) {
                    if (!attackingPiece.getPieceColour().equalsIgnoreCase(pieceColour)) {
                        if (!movesToBeRemoved.contains(kingMove)) {
                            movesToBeRemoved.add(kingMove);
                        }
                    }
                }
            }
        }

        // if checked by rook, queen, or bishop, make sure the king cannot move away from piece
        // if the king will still be in check
        if(check){
            for(Piece checkingPiece : GameHandler.checkingPieces){
                if(!(checkingPiece instanceof Knight) && !(checkingPiece instanceof Pawn)){
                    int[] coordinates = checkingPiece.getCheckPath().get(0);
                    int checkId = -1;
                    if(coordinates[0] == x-1 && coordinates[1] == y-1){
                        checkId = coordinates[2] + 18;
                    }else if(coordinates[0] == x && coordinates[1] == y-1){
                        checkId = coordinates[2] + 16;
                    }else if(coordinates[0] == x+1 && coordinates[1] == y-1){
                        checkId = coordinates[2] + 14;
                    }else if(coordinates[0] == x+1 && coordinates[1] == y){
                        checkId = coordinates[2] - 2;
                    }else if(coordinates[0] == x+1 && coordinates[1] == y+1){
                        checkId = coordinates[2] - 18;
                    }else if(coordinates[0] == x && coordinates[1] == y+1){
                        checkId = coordinates[2] - 16;
                    }else if(coordinates[0] == x-1 && coordinates[1] == y+1){
                        checkId = coordinates[2] - 14;
                    }else if(coordinates[0] == x-1 && coordinates[1] == y){
                        checkId = coordinates[2] + 2;
                    }
                    for(int i = 0; i < validMoves.size(); i++){
                        if(validMoves.get(i)[2] == checkId){
                            movesToBeRemoved.add(validMoves.get(i));
                        }
                    }
                }
            }
        }

//        validMoves.removeAll(movesToBeRemoved);

        for(int i = 0; i < movesToBeRemoved.size(); i++){
            for(int y = 0; y < validMoves.size(); y++){
                if(Arrays.equals(movesToBeRemoved.get(i), validMoves.get(y))){
                    int[] spurious = {-1, -1, -1};
                    validMoves.set(y, spurious);
                }
            }
        }
    }


    // remove moves for pinned pieces
    void checkForPinnedPieces(){
        boolean nextPieceIsAlly = false;
        Piece ally = null;
        ArrayList<int[]> trackedMoves = new ArrayList<>();

        // check all pieces in a straight line
        int[] tileIdDifference = {-8, 8, 1, -1};
        for(int g = 0; g < 4; g++) {
            nextPieceIsAlly = false;
            ally = null;
            ArrayList<int[]> keepMoves = new ArrayList<>();
            for (int i = 1; i < 8; i++) {
                int[]trackMove = new int[3];
                // make sure check is within bounds
                if (       (g == 0 && y - i >= GameHandler.top)
                        || (g == 1 && y + i <= GameHandler.bottom)
                        || (g == 2 && x + i <= GameHandler.right)
                        || (g == 3 && x - i >= GameHandler.left)) {
                    if (GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getOccupied() && !nextPieceIsAlly) {
                        if (GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getPieceOnTile().pieceColour.equalsIgnoreCase(pieceColour)) {
                            nextPieceIsAlly = true;
                            ally = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getPieceOnTile();
                        } else {
                            break;
                        }
                    } else if (GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getOccupied() && nextPieceIsAlly) {
                        if(!GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getPieceOnTile().pieceColour.equalsIgnoreCase(pieceColour)){
                            if(GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getPieceOnTile() instanceof Queen || GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getPieceOnTile() instanceof Rook) {
                                if((ally instanceof Queen) || (ally instanceof Rook)){
                                    trackMove[0] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getX();
                                    trackMove[1] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getY();
                                    trackMove[2] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getTileID();
                                    keepMoves.add(trackMove);

                                    for(int x = 0; x < ally.validMoves.size(); x++) {
                                        int[] spurious = {-1, -1, -1};
                                        boolean isPresent = false;
                                        for (int z = 0; z < keepMoves.size(); z++) {
                                            if (Arrays.equals(ally.validMoves.get(x), keepMoves.get(z))) {
                                                isPresent = true;
                                                break;
                                            }
                                        }
                                        if (!isPresent) {
                                            ally.validMoves.set(x, spurious);
                                        }
                                    }
                                }else if(ally instanceof Pawn){
                                    trackMove[0] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getX();
                                    trackMove[1] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getY();
                                    trackMove[2] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getTileID();
                                    keepMoves.add(trackMove);
                                    for(int x = 0; x < ally.validMoves.size(); x++) {
                                        int[] spurious = {-1, -1, -1};
                                        boolean isPresent = false;
                                        for (int z = 0; z < keepMoves.size(); z++) {
                                            if (Arrays.equals(ally.validMoves.get(x), keepMoves.get(z))) {
                                                isPresent = true;
                                                break;
                                            }
                                        }
                                        if (!isPresent) {
                                            ally.validMoves.set(x, spurious);
                                        }
                                    }
                                }else if(ally instanceof Knight){
                                    int[] spurious = {-1, -1, -1};
                                    for (int x = 0; x < ally.validMoves.size(); x++) {
                                        ally.validMoves.set(x, spurious);
                                    }
                                }
                            }
                            break;
                        }else{
                            break;
                        }
                    }else{
                        trackMove[0] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getX();
                        trackMove[1] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getY();
                        trackMove[2] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g] * i).getTileID();
                        keepMoves.add(trackMove);
                    }
                // break loop if out of bounds
                } else {
                    break;
                }
            }
        }



        // check for pinned pieces in a diagonal
        int[] diagonalTileIdDifference = {-9, -7, 7, 9};
        for(int g = 0; g < 4; g++) {
            nextPieceIsAlly = false;
            ally = null;
            ArrayList<int[]> keepMoves = new ArrayList<>();
            for (int i = 1; i < 9; i++) {
                int[]trackMove = new int[3];
                // make sure check is within bounds
                if (        ((g == 0 && y - i >= GameHandler.top) && (g == 0 && x - i >= GameHandler.left))
                        ||  ((g == 1 && y - i >= GameHandler.top) && (g == 1 && x + i <= GameHandler.right))
                        ||  ((g == 2 && y + i <= GameHandler.bottom) && (g == 2 && x - i >= GameHandler.left))
                        ||  ((g == 3 && y + i <= GameHandler.bottom) && (g == 3 && x + i <= GameHandler.right))) {
                    if (GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getOccupied() && !nextPieceIsAlly) {
                        if(GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getPieceOnTile().pieceColour.equalsIgnoreCase(pieceColour)){
                            nextPieceIsAlly = true;
                            ally = GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getPieceOnTile();
                        }else{
                            break;
                        }
                    } else if (GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getOccupied() && nextPieceIsAlly) {
                        if(!GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getPieceOnTile().pieceColour.equalsIgnoreCase(pieceColour)){
                            if(GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getPieceOnTile() instanceof Queen || GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getPieceOnTile() instanceof Bishop){
                                if((ally instanceof Queen) || (ally instanceof Bishop)){
                                    trackMove[0] = GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getX();
                                    trackMove[1] = GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getY();
                                    trackMove[2] = GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getTileID();
                                    keepMoves.add(trackMove);
                                    for(int x = 0; x < ally.validMoves.size(); x++) {
                                        int[] spurious = {-1, -1, -1};
                                        boolean isPresent = false;
                                        for (int z = 0; z < keepMoves.size(); z++) {
                                            if (Arrays.equals(ally.validMoves.get(x), keepMoves.get(z))) {
                                                isPresent = true;
                                                break;
                                            }
                                        }
                                        if (!isPresent) {
                                            ally.validMoves.set(x, spurious);
                                        }
                                    }
                                }
                                if(ally instanceof Pawn) {
                                    trackMove[0] = GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getX();
                                    trackMove[1] = GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getY();
                                    trackMove[2] = GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getTileID();
                                    keepMoves.add(trackMove);
                                    if (trackMove[2] == ally.currentTileID + diagonalTileIdDifference[g]) {
                                        for(int x = 0; x < ally.validMoves.size(); x++) {
                                            int[] spurious = {-1, -1, -1};
                                            boolean isPresent = false;
                                            for (int z = 0; z < keepMoves.size(); z++) {
                                                if (Arrays.equals(ally.validMoves.get(x), keepMoves.get(z))) {
                                                    isPresent = true;
                                                    break;
                                                }
                                            }
                                            if (!isPresent) {
                                                ally.validMoves.set(x, spurious);
                                            }
                                        }
                                    }
                                }else if(ally instanceof Knight){
                                    int[] spurious = {-1, -1, -1};
                                    for (int x = 0; x < ally.validMoves.size(); x++) {
                                        ally.validMoves.set(x, spurious);
                                    }
                                }
                            }
                            break;
                        }else{
                            break;
                        }
                    }else{
                        trackMove[0] = GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getX();
                        trackMove[1] = GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getY();
                        trackMove[2] = GameHandler.gameBoard.get(currentTileID + diagonalTileIdDifference[g] * i).getTileID();
                        keepMoves.add(trackMove);
                    }
                } else {
                    break;
                }
            }
        }

    }

    // check if King is in check
    void checkCheck(){
        Tile kingsTile = GameHandler.gameBoard.get(currentTileID);
        for(Piece piece : kingsTile.getAttackingPieces()){
            if(!piece.getPieceColour().equalsIgnoreCase(pieceColour)){
                check = true;
                return;
            }
        }
        check = false;
    }


    // check if king is in checkMate
    void checkForCheckMate(){
        checkMate = false;
        staleMate = false;
        int [] spurious = {-1, -1, -1};
        int pieceCounter = 0;
        int spuriousPieceCounter = 0;
        for(Piece piece : GameHandler.gamePieces){
            if(piece.pieceColour.equalsIgnoreCase(pieceColour) && !piece.taken){
                pieceCounter++;
                int spuriousCounter = 0;
                for(int i = 0; i < piece.validMoves.size(); i++){
                    if(Arrays.equals(piece.validMoves.get(i), spurious)){
                        spuriousCounter++;
                    }
                }
                if(spuriousCounter == piece.validMoves.size()){
                    spuriousPieceCounter++;
                }
            }
        }

        if(pieceCounter == spuriousPieceCounter){
            if(check){
                checkMate = true;
            }else{
                staleMate = true;
            }
        }
    }


    // SETTERS AND GETTERS
    public void madeFirstMove(){
        firstMove = false;
    }

    public boolean getFirstMove(){
        return firstMove;
    }

    public boolean getCheck(){
        return check;
    }

    public boolean getCheckMate(){
        return checkMate;
    }

    public boolean getStaleMate(){
        return staleMate;
    }
}