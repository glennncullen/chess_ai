package Chess;

import java.util.Arrays;

public class Knight extends Piece {

    public Knight(int currentTileID, int x, int y, String pieceColour, int pieceId) {
        super(currentTileID, x, y, pieceColour, pieceId);
        pieceValue = 3.2;
    }

    @Override
    public void determinePossibleMoves(){
        super.determinePossibleMoves();
        if(!taken) {
            int[] tileIdDifference = {-17, -15, -6, 10, 17, 15, 6, -10};
            for (int g = 0; g < tileIdDifference.length; g++) {
                int[] possibleMove = new int[3];
                int[] spurious = {-1, -1, -1};
                if (((g == 0) && (y - 2 >= GameHandler.top && x - 1 >= GameHandler.left))
                        || ((g == 1) && (y - 2 >= GameHandler.top && x + 1 <= GameHandler.right))
                        || ((g == 2) && (x + 2 <= GameHandler.right && y - 1 >= GameHandler.top))
                        || ((g == 3) && (x + 2 <= GameHandler.right && y + 1 <= GameHandler.bottom))
                        || ((g == 4) && (y + 2 <= GameHandler.bottom && x + 1 <= GameHandler.right))
                        || ((g == 5) && (y + 2 <= GameHandler.bottom && x - 1 >= GameHandler.left))
                        || ((g == 6) && (x - 2 >= GameHandler.left && y + 1 <= GameHandler.bottom))
                        || ((g == 7) && (x - 2 >= GameHandler.left && y - 1 >= GameHandler.top))) {
                    if (currentTileID + tileIdDifference[g] >= 0 && currentTileID + tileIdDifference[g] <= 63) {
                        if (!GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getOccupied()) {
                            possibleMove[0] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getX();
                            possibleMove[1] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getY();
                            possibleMove[2] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getTileID();
                            validMoves.add(possibleMove);
                        } else if (!GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getPieceOnTile().pieceColour.equalsIgnoreCase(pieceColour)) {
                            possibleMove[0] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getX();
                            possibleMove[1] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getY();
                            possibleMove[2] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getTileID();
                            validMoves.add(possibleMove);
                        } else {
                            possibleMove[0] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getX();
                            possibleMove[1] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getY();
                            possibleMove[2] = GameHandler.gameBoard.get(currentTileID + tileIdDifference[g]).getTileID();
                            protectedPieces.add(possibleMove);
                            validMoves.add(spurious);
                        }
                    }
                } else {
                    validMoves.add(spurious);
                }
            }
        }else{
            for(int i = 0; i < 8; i++){
                int[] spurious = {-1, -1, -1};
                validMoves.add(spurious);
            }
        }
    }

}