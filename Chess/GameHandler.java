package Chess;

import NeuralNetwork.NNHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GameHandler {

    public static ArrayList<Piece> gamePieces;
    public static ArrayList<Tile> gameBoard;
    public static ArrayList<Piece> takenPieces;
    public static ArrayList<Piece> checkingPieces;
    public static boolean whiteTurn;
    public static int left, top, right, bottom;
    public static boolean updateCastling;
    public static King[] kings;

    public static Random random;

    public static int timerSpeed;
    public static boolean aiRandomPlayer;
    private static Piece aiRandomPiece;
    public static Piece aiNeuralPiece;
    public static boolean training;
    public static boolean aiNeuralNetwork;


    // initialise game handler statics
    public static void init(){
        gamePieces = new ArrayList<Piece>();
        gameBoard = new ArrayList<Tile>();
        takenPieces = new ArrayList<Piece>();
        checkingPieces = new ArrayList<Piece>();
        whiteTurn = true;
        left = 0;
        top = 0;
        right = 7;
        bottom = 7;
        updateCastling = false;
        kings = new King[2];

        random = new Random();

        timerSpeed = 800;
        aiRandomPlayer = false;
        aiRandomPiece = null;
        aiNeuralPiece = null;

        aiNeuralNetwork = false;

        training = false;
    }


    // change whose turn it is and update tiles and pieces
    public static void endTurn(){
        checkingPieces.clear();
        whiteTurn = !whiteTurn;
        updateCastling = false;

        for(int i = 0; i < gamePieces.size(); i++){
            gamePieces.get(i).checkPath.clear();
            gamePieces.get(i).determinePossibleMoves();
        }
        // Assign attacking pieces to tile they're attacking
        setAttackingPieces();
        for(int i = 0; i < kings.length; i++){
            kings[i].checkCheck();

            if(kings[i].getCheck()){
                for(Piece piece : gameBoard.get(kings[i].currentTileID).getAttackingPieces()) {
                    if (!piece.getPieceColour().equalsIgnoreCase(kings[i].getPieceColour())) {
                        piece.setCheckPath(kings[i]);
                        checkingPieces.add(piece);
                    }
                }
            }
        }
        // check for check
        for(int i = 0; i < kings.length; i++){
            if(kings[i].getCheck()){
                for(Piece piece : gamePieces){
                    if(piece.getPieceColour().equalsIgnoreCase(kings[i].getPieceColour())){
                        piece.removePersistentCheckMoves();
                    }
                }
            }
            kings[i].removeMovesIntoCheck();
            kings[i].checkForPinnedPieces();
            kings[i].checkForCheckMate();
            setAttackingPieces();
        }
    }


    // track pieces attacking tiles
    public static void setAttackingPieces(){
        for(Tile tile : gameBoard){
            tile.clearAttackingPieces();
        }
        for(Piece piece : gamePieces){
            if(piece instanceof Pawn) {
                for (int[] diagonalMove : ((Pawn) piece).getDiagonalMoves()) {
                    int[] spurious = {-1, -1, -1};
                    if(!Arrays.equals(diagonalMove, spurious)) {
                        gameBoard.get(diagonalMove[2]).setAsAttackingPiece(piece);
                    }
                }
            }else{
                for(int[] validMove : piece.validMoves){
                    if(validMove[2] != -1){
                        gameBoard.get(validMove[2]).setAsAttackingPiece(piece);
                    }
                }
                for(int[] validMove : piece.protectedPieces){
                    if(validMove[2] != -1){
                        gameBoard.get(validMove[2]).setAsAttackingPiece(piece);
                    }
                }
            }
        }
    }

    // get the AiRandomMove's next move
    public static int[] getAiNextMove() {
        String randomPieceColour;
        if (whiteTurn) {
            randomPieceColour = "White";
        } else {
            randomPieceColour = "Black";
        }

        int [] spurious = {-1, -1, -1};
        ArrayList<Piece> possiblePieces = new ArrayList<>();
        for(Piece piece : gamePieces){
            if(piece.pieceColour.equalsIgnoreCase(randomPieceColour) && !piece.taken){
                int spuriousCounter = 0;
                for(int i = 0; i < piece.validMoves.size(); i++){
                    if(Arrays.equals(piece.validMoves.get(i), spurious)){
                        spuriousCounter++;
                    }
                }
                if(spuriousCounter != piece.validMoves.size()){
                    possiblePieces.add(piece);
                }
            }
        }
        Piece pieceChosen = possiblePieces.get(random.nextInt(possiblePieces.size()));
        aiRandomPiece = pieceChosen;

        int [] move = pieceChosen.validMoves.get(random.nextInt(pieceChosen.validMoves.size()));;
        while(Arrays.equals(move, spurious)){
            move = pieceChosen.validMoves.get(random.nextInt(pieceChosen.validMoves.size()));;
        }
        return move;
    }

    public static Piece getAiNextPiece(){
        return aiRandomPiece;
    }

    public static ArrayList<Tile> getGameBoard() {
        return gameBoard;
    }

    public static int[] getNeuralNetworkMove(String classification){
        String pieceName = "";
        int pieceId = 0;
        int moveIndex = -1;
        int[] move = new int[3];
        StringBuffer sb = new StringBuffer();
        for(int x = 0; x < classification.length(); x++){
            if(classification.charAt(x) != ' '){
                sb.append(classification.charAt(x));
                if(x == classification.length()-1){
                    if (sb.toString().contains("move")){
                        moveIndex = Integer.parseInt(sb.substring(5, sb.length()));
                    }
                }
            }else{
                if(sb.toString().contains("pieceId")) {
                    pieceId = Integer.parseInt(sb.substring(8, sb.length()));
                    sb = new StringBuffer();
                }else if(sb.toString().contains("pieceName")) {
                    pieceName = sb.substring(10, sb.length());
                    sb = new StringBuffer();
                }else{
                    sb = new StringBuffer();
                }
            }
        }

        for(Piece piece : gamePieces){
            if(!piece.taken){
                if(piece.getClass().getSimpleName().equalsIgnoreCase(pieceName) && piece.pieceId == pieceId && piece.pieceColour.equalsIgnoreCase("White")){
                    aiNeuralPiece= piece;
                    move = piece.validMoves.get(moveIndex);
                    break;
                }
            }
        }
        return move;
    }
}
