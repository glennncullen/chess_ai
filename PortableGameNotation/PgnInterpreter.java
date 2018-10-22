package PortableGameNotation;
import Chess.GameHandler;
import Chess.Tile;

public class PgnInterpreter {

    Move interpretMove(String pgnMove, boolean isWhiteMove){
        Move transformedMove = new Move();
        transformedMove.setPgnRef(pgnMove);

        // set piece colour
        if(isWhiteMove){
            transformedMove.setPieceColour("White");
        }else{
            transformedMove.setPieceColour("Black");
        }

        // set result
        if(pgnMove.contains("1/2") || pgnMove.contains("1-0") || pgnMove.contains("0-1")){
            transformedMove.setEndGame(pgnMove);
            return transformedMove;
        }

        // set castling
        if(pgnMove.equalsIgnoreCase("o-o") || pgnMove.equalsIgnoreCase("o-o+")){
            transformedMove.setCastling(true);
            if(transformedMove.getPieceColour().equalsIgnoreCase("White")){
                int[] move = {1, 0, 1};
                transformedMove.setMove(move);
            }else{
                int[] move = {1, 7, 57};
                transformedMove.setMove(move);
            }
            transformedMove.setPieceName("King");
            return transformedMove;
        }else if(pgnMove.equalsIgnoreCase("o-o-o") || pgnMove.equalsIgnoreCase("o-o-o+")){
            transformedMove.setCastling(true);
            if(transformedMove.getPieceColour().equalsIgnoreCase("White")){
                int[] move = {5, 0, 5};
                transformedMove.setMove(move);
            }else{
                int[] move = {5, 7, 61};
                transformedMove.setMove(move);
            }
            transformedMove.setPieceName("King");
            return transformedMove;
        }

        // set Piece Name
        if(pgnMove.charAt(0) >= 'a' && pgnMove.charAt(0) <= 'z'){
            transformedMove.setPieceName("Pawn");
        }else{
            switch (pgnMove.charAt(0)){
                case 'K':
                    transformedMove.setPieceName("King");
                    break;
                case 'Q':
                    transformedMove.setPieceName("Queen");
                    break;
                case 'R':
                    transformedMove.setPieceName("Rook");
                    break;
                case 'B':
                    transformedMove.setPieceName("Bishop");
                    break;
                case 'N':
                    transformedMove.setPieceName("Knight");
                    break;
                default:
                    break;
            }
        }

        // set Move
        StringBuilder sb = new StringBuilder();
        int x = -1;
        int y = 0;
        if(pgnMove.charAt(1) == 'x'){
            sb.append(pgnMove.charAt(2));
            sb.append(pgnMove.charAt(3));
            if(pgnMove.charAt(0) >= 'a' && pgnMove.charAt(0) <= 'z'){
                transformedMove.setComingFromX(getXfromLetter(pgnMove.charAt(0)));
                if(transformedMove.getComingFromX() == -1){
                    transformedMove.setComingFromY(Character.getNumericValue(pgnMove.charAt(1))-1);
                }
                transformedMove.setPieceName("Pawn");
            }
        }else if(pgnMove.length() == 2) {
            sb.append(pgnMove.charAt(0));
            sb.append(pgnMove.charAt(1));
            transformedMove.setPieceName("Pawn");
        }else if(pgnMove.length() == 3 && pgnMove.contains("+")){
            sb.append(pgnMove.charAt(0));
            sb.append(pgnMove.charAt(1));
            transformedMove.setPieceName("Pawn");
        }else if((pgnMove.length() == 3 && pgnMove.charAt(0) != 'x') && !(pgnMove.contains("O-O"))){
            sb.append(pgnMove.charAt(1));
            sb.append(pgnMove.charAt(2));
            transformedMove.setComingFromX(getXfromLetter(pgnMove.charAt(0)));
//            if(transformedMove.getComingFromX() == -1){
//                transformedMove.setComingFromY(Character.getNumericValue(pgnMove.charAt(1)));
//            }
        }else if (pgnMove.length() >= 4 && pgnMove.charAt(1) != 'x'){
            if(pgnMove.contains("+") && pgnMove.length() == 4) {
                sb.append(pgnMove.charAt(1));
                sb.append(pgnMove.charAt(2));
            }else if(pgnMove.contains("x")) {
                sb.append(pgnMove.charAt(3));
                sb.append(pgnMove.charAt(4));
                transformedMove.setComingFromX(getXfromLetter(pgnMove.charAt(1)));
                if(transformedMove.getComingFromX() == -1){
                    transformedMove.setComingFromY(Character.getNumericValue(pgnMove.charAt(1))-1);
                }
            }else if(pgnMove.contains("=")){
                sb.append(pgnMove.charAt(0));
                sb.append(pgnMove.charAt(1));
            }else {
                sb.append(pgnMove.charAt(2));
                sb.append(pgnMove.charAt(3));
                transformedMove.setComingFromX(getXfromLetter(pgnMove.charAt(1)));
                if(transformedMove.getComingFromX() == -1){
                    transformedMove.setComingFromY(Character.getNumericValue(pgnMove.charAt(1))-1);
                }
            }
        }else {
            switch (pgnMove.charAt(0)) {
                case 'K':
                    sb.append(pgnMove.charAt(1));
                    sb.append(pgnMove.charAt(2));
                    break;
                case 'Q':
                    sb.append(pgnMove.charAt(1));
                    sb.append(pgnMove.charAt(2));
                    break;
                case 'R':
                    sb.append(pgnMove.charAt(1));
                    sb.append(pgnMove.charAt(2));
                    break;
                case 'B':
                    sb.append(pgnMove.charAt(1));
                    sb.append(pgnMove.charAt(2));
                    break;
                case 'N':
                    sb.append(pgnMove.charAt(1));
                    sb.append(pgnMove.charAt(2));
                    break;
                default:
                    break;
            }
        }
        if(sb.length() > 0) {
            String movexy = sb.toString();
            y = Integer.parseInt(String.valueOf(movexy.charAt(1))) - 1;
            x = getXfromLetter(movexy.charAt(0));

            for (Tile tile : GameHandler.getGameBoard()) {
                if (tile.getX() == x && tile.getY() == y) {
                    int[] move = {x, y, tile.getTileID()};
                    transformedMove.setMove(move);
                    break;
                }
            }
        }


        // Pawn Promotion
        if(pgnMove.contains("=")){
            transformedMove.setPawnPromotion(true);
            if(pgnMove.contains("Q")){
                transformedMove.setPromotionPiece(0);
            }else if(pgnMove.contains("R")){
                transformedMove.setPromotionPiece(1);
            }else if(pgnMove.contains("N")){
                transformedMove.setPromotionPiece(2);
            }else if(pgnMove.contains("B")){
                transformedMove.setPromotionPiece(3);
            }
        }


        return transformedMove;
    }


    // get X coordinate from letter
    int getXfromLetter(char x){
        int numOfX = -1;
        switch (x) {
            case 'a':
                numOfX = 7;
                break;
            case 'b':
                numOfX = 6;
                break;
            case 'c':
                numOfX = 5;
                break;
            case 'd':
                numOfX = 4;
                break;
            case 'e':
                numOfX = 3;
                break;
            case 'f':
                numOfX = 2;
                break;
            case 'g':
                numOfX = 1;
                break;
            case 'h':
                numOfX = 0;
                break;
            default:
                break;
        }
        return numOfX;
    }

}
