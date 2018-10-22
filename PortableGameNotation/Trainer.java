package PortableGameNotation;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

public class Trainer {

    private Stack<String> games;
    private Stack<Move> gameInProgress;
    private PgnReader reader;
    private PgnInterpreter interpreter;

    public Trainer(){
        reader = new PgnReader("/Users/glennncullen/Google Drive/College/Year 4 - Semester 1/Introduction to AI/CA Documents/ChessProject/Chess/Carlsen.pgn");
        games = reader.pgnFileToStack();
        gameInProgress = new Stack<>();
        interpreter = new PgnInterpreter();
    }

    public void setNextGame(){
        boolean isWhiteMove = true;
        Stack<Move> nextGame = new Stack<>();
        StringBuilder sb = new StringBuilder();
        String currentGame = games.pop();
        System.out.println(currentGame);
        for(int i = 0; i < currentGame.length(); i++){
            if(currentGame.charAt(i) != ' '){
                sb.append(currentGame.charAt(i));
            }else{
                if(sb.toString().contains(".")){
                    sb = new StringBuilder();
                }else if(sb.length() != 0 && !(sb.toString().contains("1/2") || sb.toString().contains("1-0") || sb.toString().contains("0-1"))){
//                    System.out.println(i + "\t" + sb.toString());
                    nextGame.push(interpreter.interpretMove(sb.toString(), isWhiteMove));
                    sb = new StringBuilder();
                    isWhiteMove = !isWhiteMove;
                }
            }
        }
        Collections.reverse(nextGame);
        gameInProgress = nextGame;
    }


    public Stack<Move> getGameInProgress() {
        setNextGame();
        return gameInProgress;
    }

    public boolean gamesLeft(){
        if(games.isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    public int numGamesLeft(){
        return games.size();
    }

    public void printNextGame(){
        Iterator iterator = gameInProgress.iterator();
        while(iterator.hasNext()){
            Move currentMove = gameInProgress.pop();
        }
    }

    public void printStackContents(){
        Iterator iterator = games.iterator();
        while(iterator.hasNext()){

        }
    }

}
