package Chess;

import java.util.Arrays;

public class Queen extends Piece {

    public Queen(int currentTileID, int x, int y, String pieceColour, int pieceId) {
        super(currentTileID, x, y, pieceColour, pieceId);
        pieceValue = 8.8;
    }

    @Override
    public void determinePossibleMoves() {
        super.determinePossibleMoves();
        if(!taken) {
            checkStraightLines();
            checkDiagonals();
        }else{
            for(int i = 0; i < 56; i++){
                int[] spurious = {-1, -1, -1};
                validMoves.add(spurious);
            }
        }
    }

}
