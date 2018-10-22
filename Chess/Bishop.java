package Chess;

public class Bishop extends Piece {

    public Bishop(int currentTileID, int x, int y, String pieceColour, int pieceId) {
        super(currentTileID, x, y, pieceColour, pieceId);
        pieceValue = 3.33;
    }

    @Override
    public void determinePossibleMoves() {
        super.determinePossibleMoves();
        if(!taken) {
            checkDiagonals();
        }else{
            for(int i = 0; i < 28; i++){
                int[] spurious = {-1, -1, -1};
                validMoves.add(spurious);
            }
        }
    }

}
