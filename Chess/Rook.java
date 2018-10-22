package Chess;

public class Rook extends Piece{

    private boolean firstMove;

    Rook(int currentTileID, int x, int y, String pieceColour, int pieceId) {
        super(currentTileID, x, y, pieceColour, pieceId);
        pieceValue = 5.1;
        firstMove = true;
    }

    // CODE HERE FOR CASTLING


    @Override
    public void determinePossibleMoves() {
        super.determinePossibleMoves();
        if(!taken) {
            checkStraightLines();
        }else{
            for(int i = 0; i < 28; i++){
                int[] spurious = {-1, -1, -1};
                validMoves.add(spurious);
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

}