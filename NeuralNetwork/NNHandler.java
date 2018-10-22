package NeuralNetwork;

import Chess.*;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class NNHandler {

    public static NumberFormat formatter = new DecimalFormat("0.000000000000000000");

    public static int inputNum;
    public static int hiddenNum;
    public static int outputNum;

    public static double[][] inputToHiddenSynapseMatrix;
    public static double[][] hiddenToOutputSynapseMatrix;
    public static String[] outputClassification;

    public static ArrayList<String> pawnPromotionList;
    public static ArrayList<String> pawnPromotionClassification;

    public static double[] inputLayer;
    public static double[] hiddenLayer;
    public static double[] outputLayer;
    public static double[] pawnPromotionLayer;

    public static double learningRate;

    public static boolean whiteTurn;
    public static String currentMove;
    public static String moveToMake;


    public static void init(){
        inputToHiddenSynapseMatrix = new double[inputNum][hiddenNum];
        hiddenToOutputSynapseMatrix = new double[hiddenNum][outputNum];
        setInputToHiddenSynapseMatrix();
        setHiddenToOutputSynapseMatrix();
        setOutputClassification();
        pawnPromotionList = new ArrayList<>();
        pawnPromotionClassification = new ArrayList<>();
        inputLayer = new double[inputNum];
        hiddenLayer = new double[hiddenNum];
        outputLayer = new double[outputNum];
        learningRate = 0.18;
        whiteTurn = true;
        moveToMake = "";
    }


    public static void reset(){
        pawnPromotionList = new ArrayList<>();
        pawnPromotionClassification = new ArrayList<>();
        whiteTurn = true;
        moveToMake = "";
    }


    public static void calculateInputLayer(){
        inputLayer = new double[inputNum];
        int index= 0;
        for(int i = inputLayer.length; i > 0; i--){
            if(i == 1){
                if(whiteTurn){
                    inputLayer[index] = 1;
                }else{
                    inputLayer[i-1] = 1;
                }
                break;
            }
            Tile currentTile = GameHandler.gameBoard.get(index);
            double total = 0;
            total = total + currentTile.getTileValue();
            if(currentTile.getOccupied()){
                if(whiteTurn && !currentTile.getPieceOnTile().getPieceColour().equalsIgnoreCase("White")){
                    total = total + (currentTile.getPieceOnTile().pieceValue * 2);
                }else if(!whiteTurn && currentTile.getPieceOnTile().getPieceColour().equalsIgnoreCase("White")){
                    total = total + (currentTile.getPieceOnTile().pieceValue * 2);
                }else{
                    total = total + currentTile.getPieceOnTile().pieceValue;
                }
            }
            if(currentTile.getAttackingPieces().size() > 0){
                for(Piece piece : currentTile.getAttackingPieces()){
                    if(whiteTurn && piece.getPieceColour().equalsIgnoreCase("White")){
                        total = total + piece.pieceValue;
                        if(piece.protectedPieces.size() > 0){
                            for(int x = 0; x < piece.protectedPieces.size(); x++){
//                                System.out.println(piece.getClass().getSimpleName() + "\t" + x + "\t" + piece.protectedPieces.get(x)[2]);
                                total = total + GameHandler.gameBoard.get(piece.protectedPieces.get(x)[2]).getPieceOnTile().pieceValue;
                            }
                        }
                    }else if(!whiteTurn && !piece.getPieceColour().equalsIgnoreCase("White")){
                        total = total + piece.pieceValue;
                        if(piece.protectedPieces.size() > 0){
                            for(int x = 0; x < piece.protectedPieces.size(); x++){
                                total = total + GameHandler.gameBoard.get(piece.protectedPieces.get(x)[2]).getPieceOnTile().pieceValue;
                            }
                        }
                    }
                    if(whiteTurn && piece.getPieceColour().equalsIgnoreCase("Black")){
                        total = total + (piece.pieceValue * 1.5);
                        if(piece.protectedPieces.size() > 0){
                            for(int x = 0; x < piece.protectedPieces.size(); x++){
                                total = total + GameHandler.gameBoard.get(piece.protectedPieces.get(x)[2]).getPieceOnTile().pieceValue;
                            }
                        }
                    }else if(!whiteTurn && !piece.getPieceColour().equalsIgnoreCase("Black")){
                        total = total + (piece.pieceValue * 1.5);
                        if(piece.protectedPieces.size() > 0){
                            for(int x = 0; x < piece.protectedPieces.size(); x++){
                                total = total + GameHandler.gameBoard.get(piece.protectedPieces.get(x)[2]).getPieceOnTile().pieceValue;
                            }
                        }
                    }
                }
            }
            if(!whiteTurn){
                inputLayer[i-1] = total / 100;
            }else if (whiteTurn){
                inputLayer[index] = total / 100;
            }
            index++;
        }
    }


    public static void calculateHiddenLayer(){
        hiddenLayer = new double[hiddenNum];
        for(int z = 0; z < hiddenLayer.length; z++) {
            double total = 0;
            for (int i = 0; i < inputLayer.length; i++) {
                total = total + (inputLayer[i] * inputToHiddenSynapseMatrix[i][z]);
            }
            if(z == hiddenLayer.length-1){
                hiddenLayer[z] = 1;
            }else {
                hiddenLayer[z] = total;
            }
//            System.out.println("Hidden at " + z + ":\t" + hiddenLayer[z]);
        }
    }


    public static void applyActivationFunction(){
        for(int i = 0; i < hiddenLayer.length; i++){
            hiddenLayer[i] = Math.max(-0.01, hiddenLayer[i]);
//            System.out.println("Activation at " + i + ":\t" + hiddenLayer[i]);
        }
    }


    public static void calculateOutputLayer(boolean pawnPromotion){
        pawnPromotionClassification.clear();
        outputLayer = new double[outputNum];
        String currentPieceColour = "";
        if(whiteTurn){
            currentPieceColour = "White";
        }else{
            currentPieceColour = "Black";
        }
        ArrayList<Integer> positions = new ArrayList<>();
        ArrayList<Double> softMax = new ArrayList<>();
        for(int z = 0; z < outputLayer.length; z++){
            int[] piece = getClassification(outputClassification[z]);
            Piece targetPiece = null;
            for(Piece p : GameHandler.gamePieces){
                if(p.getPieceColour().equalsIgnoreCase(currentPieceColour) && p.pieceId == piece[0]){
                    targetPiece = p;
                    break;
                }
            }
            int[] spurious = {-1, -1, -1};
//            System.out.println(z + "\t" + outputClassification[z] + "\tvalidMoves: " + targetPiece.validMoves.size());
            if(Arrays.equals(targetPiece.validMoves.get(piece[1]), spurious)){
                outputLayer[z] = -10000;
            }
        }

        for(int z = 0; z < outputLayer.length; z++){
            double total = 0;
            for(int i = 0; i < hiddenLayer.length; i++){
                if(outputLayer[z] != -10000){
                    total = total + (hiddenLayer[i] * hiddenToOutputSynapseMatrix[i][z]);
                }
            }
            if(outputLayer[z] != -10000) {
                outputLayer[z] = total;
                positions.add(z);
                softMax.add(outputLayer[z]);
//                System.out.println("Out at " + z + "  \t" + outputLayer[z]);
            }
        }

        int counter = 0;
        double[] transform = softMax(softMax);
        for(int i = 0; i < outputLayer.length; i++){
            if(positions.contains(i)){
                outputLayer[i] = transform[counter];
                counter++;
            }
        }

        if(pawnPromotionList.size() > 0 && !pawnPromotion) {
            for (int i = 0; i < pawnPromotionList.size(); i++) {
                if (getClassificationColour(i, pawnPromotionList).equalsIgnoreCase(currentPieceColour)) {
                    setPawnPromotionClassification(getClassificationColour(i, pawnPromotionList), getClassificationPromotionId(i, pawnPromotionList), getClassificationPieceId(i, pawnPromotionList));
                }
            }
            pawnPromotionLayer = new double[pawnPromotionClassification.size()];
            for (int i = 0; i < pawnPromotionClassification.size(); i++) {
                int[] piece = getClassification(pawnPromotionClassification.get(i));
                Piece targetPiece = null;
                for (Piece p : GameHandler.gamePieces) {
                    if (p.getPieceColour().equalsIgnoreCase(currentPieceColour) && p.pieceId == piece[0] && !p.taken) {
                        targetPiece = p;
                        break;
                    }
                }
                int[] spurious = {-1, -1, -1};
                if (Arrays.equals(targetPiece.validMoves.get(piece[1]), spurious)) {
                    pawnPromotionLayer[i] = -10000;
                }
            }


            int index = 0;
            for (int x = 0; x < pawnPromotionList.size(); x++) {
                if(getClassificationColour(x, pawnPromotionList).equalsIgnoreCase(currentPieceColour)) {
                    int start = 0;
                    int stop = 0;
                    switch (getClassificationPieceName(x, pawnPromotionList)) {
                        case "Queen":
                            start = 73;
                            stop = 129;
                            break;
                        case "Bishop":
                            start = 129;
                            stop = 157;
                            break;
                        case "Knight":
                            start = 157;
                            stop = 165;
                            break;
                        case "Rook":
                            start = 165;
                            stop = 193;
                            break;
                        default:
                            break;
                    }
                    for (int z = start; z < stop; z++) {
                        double total = 0;
                        for (int y = 0; y < hiddenLayer.length; y++) {
                            if (pawnPromotionLayer[index] != -10000) {
                                total = total + (hiddenLayer[y] * hiddenToOutputSynapseMatrix[y][z]);
                                pawnPromotionLayer[index] = total;
                                break;
                            } else {
                                break;
                            }
                        }
                        index++;
                    }
                }
            }
        }

        boolean usingPromotedPiece = false;
        double max = 0;
        int chosenMoveindex = -1;
        for(int i = 0; i < outputLayer.length; i++){
            if(outputLayer[i] != -10000){
                if(outputLayer[i] > max){
                    max = outputLayer[i];
                    chosenMoveindex = i;
                }
//                System.out.println("Output Layer at " + i + ":\t" + outputLayer[i] + "\t\tOverall Maximum:\t" + max);
            }
        }

        if(pawnPromotionClassification.size() > 0 && !pawnPromotion) {
            for (int i = 0; i < pawnPromotionLayer.length; i++) {
                if (pawnPromotionLayer[i] != -10000) {
                    if (pawnPromotionLayer[i] > max) {
                        max = pawnPromotionLayer[i];
                        usingPromotedPiece = true;
                        for (int x = 0; x < outputClassification.length; x++) {
                            if (pawnPromotionClassification.get(i).contains(outputClassification[x])) {
                                chosenMoveindex = x;
                                break;
                            }
                        }
                    }
                }
            }
        }


        int targetMoveIndex = -1;
        if(GameHandler.training) {
            for (int i = 0; i < outputClassification.length; i++) {
                if (currentMove.equalsIgnoreCase(outputClassification[i])) {
                    targetMoveIndex = i;
                    break;
                }
            }

            if (targetMoveIndex == -1) {
                for (int x = 0; x < pawnPromotionClassification.size(); x++) {
                    if (pawnPromotionClassification.get(x).contains(currentMove)) {
                        targetMoveIndex = x;
                        break;
                    }
                }
            }
        }

        if(targetMoveIndex != -1 && !usingPromotedPiece && GameHandler.training && whiteTurn){
            backPropagation(targetMoveIndex, chosenMoveindex);
        }else if(GameHandler.aiNeuralNetwork){
            if(usingPromotedPiece){
                moveToMake = pawnPromotionClassification.get(chosenMoveindex);
            }else{
                moveToMake = outputClassification[chosenMoveindex];
            }
        }
    }


    public static double[] softMax(ArrayList<Double> array){
        double[] softMax = new double[array.size()];
        double sum = 0;
        for(int i = 0; i < softMax.length; i++){
            if(!Double.isInfinite(Math.exp(array.get(i)))){
                sum = Math.exp(array.get(i)) + sum;
            }
//            System.out.println("test:\t" + sum);
        }

        double total = 0;
        for(int i = 0; i < softMax.length; i++){
            if(!Double.isInfinite(Math.exp(array.get(i)) / sum)){
                softMax[i] = Math.exp(array.get(i)) / sum;
            }else{
                softMax[i] = 0;
            }
//            System.out.println("Output SoftMax at: " + i + "\t" + softMax[i]);
            total = total + softMax[i];
        }
//        System.out.println("Output SoftMax TOTAL: \t" + total);
        return softMax;
    }


    public static void backPropagation(int targetMoveIndex, int chosenMoveIndex){
//        from hidden to output:
//        learningRate * (error * inputFromHiddenNode) * (outputFromOutputNode * (1 - outputFromOutputNode))
//        from input to hidden:
//        learningRate * (error * inputFromInputNode) * (outputFromHiddenNode * (1 - outputFromHiddenNode))
//
//        Weight change from hidden to output:
        boolean correctMove = false;
        double avgError = 0;
        double avgCounter = 0;
        for(int i = 0; i < hiddenToOutputSynapseMatrix.length; i++){
            for(int x = 0; x < hiddenToOutputSynapseMatrix[i].length; x++){
                double target;
                double error;
                if(targetMoveIndex == x && chosenMoveIndex == x && outputLayer[x] != -10000){
                    target = 1;
                    error = target - outputLayer[x];
                    avgError += error;
                    avgCounter++;
                }else if(targetMoveIndex == x && outputLayer[x] != -10000) {
                    target = 1;
                    error = target - outputLayer[x];
                    avgError += error;
                    avgCounter++;
                }else if(chosenMoveIndex == x && outputLayer[x] != -10000){
                    target = 0;
                    error = target - outputLayer[x];
                    avgError += error;
                    avgCounter++;
                }else{
                    target = 0;
                    error = target - outputLayer[x];
                }
                if(outputLayer[x] != -10000){
                    if(!Double.isNaN(learningRate * (error * hiddenLayer[i]) * (outputLayer[x] * (1 - outputLayer[x]))) && !Double.isInfinite(learningRate * (error * hiddenLayer[i]) * (outputLayer[x] * (1 - outputLayer[x])))){
                        hiddenToOutputSynapseMatrix[i][x] += learningRate * (error * hiddenLayer[i]) * (outputLayer[x] * (1 - outputLayer[x]));
                    }else{
                        System.out.println("CAN'T UPDATE HID TO OUT");
                    }
                }
            }
        }

        avgError = avgError / avgCounter;
        if(Double.isInfinite(avgError)){
            avgError = -0.001;
        }

        //    Weight change from input to hidden:
        for(int i = 0; i < inputToHiddenSynapseMatrix.length; i++){
            for(int x = 0; x < inputToHiddenSynapseMatrix[i].length; x++){
                if(!Double.isNaN(learningRate * (avgError * inputLayer[i])) && !Double.isInfinite(learningRate * (avgError * inputLayer[i]))){
                    if(hiddenLayer[x] > 0) {
//                        System.out.println(learningRate + " * (" + avgError + " * " + inputLayer[i] + ") = " + learningRate * (avgError * inputLayer[i]));
                        inputToHiddenSynapseMatrix[i][x] += learningRate * (avgError * inputLayer[i]);
//                        * (hiddenLayer[x] * (1 - hiddenLayer[x]));
                    }
                }else{
                    System.out.println("CAN'T UPDATE IN TO HID");
                }
            }
        }
    }


    public static void setHiddenToOutputSynapseMatrix() {
        try(BufferedReader br = new BufferedReader(new FileReader("HiddenToOutputSynapseWeights.txt"))){
            int hiddenCoutner = 0;
            String line = br.readLine();
            while(line != null){
                int outputCounter = 0;
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < line.length(); i++){
                    if(line.charAt(i) != ' ' && line.charAt(i) != '{' && line.charAt(i) != '}' && line.charAt(i) != ','){
                        sb.append(line.charAt(i));
                    }else{
                        if(sb.toString().length() > 3) {
                            hiddenToOutputSynapseMatrix[hiddenCoutner][outputCounter] = Double.parseDouble(sb.toString());
                            outputCounter++;
                        }
                        sb = new StringBuilder();
                    }
                }
                hiddenCoutner++;
                line = br.readLine();
            }
        }catch(IOException e){
            System.out.println(e);
        }
    }


    public static void setInputToHiddenSynapseMatrix(){
        try(BufferedReader br = new BufferedReader(new FileReader("InputToHiddenSynapseWeights.txt"))){
            int inputCounter = 0;
            String line = br.readLine();
            while(line != null){
                int hiddenCounter = 0;
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < line.length(); i++){
                    if(line.charAt(i) != ' ' && line.charAt(i) != '{' && line.charAt(i) != '}' && line.charAt(i) != ','){
                        sb.append(line.charAt(i));
                    }else{
                        if(sb.toString().length() > 3) {
                            inputToHiddenSynapseMatrix[inputCounter][hiddenCounter] = Double.parseDouble(sb.toString());
                            hiddenCounter++;
                        }
                        sb = new StringBuilder();
                    }
                }
                inputCounter++;
                line = br.readLine();
            }
        }catch(IOException e){
            System.out.println(e);
        }
    }


    public static void setOutputClassification(){
        String[] temp = {
                "pieceName=Rook pieceId=1 move=0",
                "pieceName=Rook pieceId=1 move=1",
                "pieceName=Rook pieceId=1 move=2",
                "pieceName=Rook pieceId=1 move=3",
                "pieceName=Rook pieceId=1 move=4",
                "pieceName=Rook pieceId=1 move=5",
                "pieceName=Rook pieceId=1 move=6",
                "pieceName=Rook pieceId=1 move=7",
                "pieceName=Rook pieceId=1 move=8",
                "pieceName=Rook pieceId=1 move=9",
                "pieceName=Rook pieceId=1 move=10",
                "pieceName=Rook pieceId=1 move=11",
                "pieceName=Rook pieceId=1 move=12",
                "pieceName=Rook pieceId=1 move=13",
                "pieceName=Rook pieceId=1 move=14",
                "pieceName=Rook pieceId=1 move=15",
                "pieceName=Rook pieceId=1 move=16",
                "pieceName=Rook pieceId=1 move=17",
                "pieceName=Rook pieceId=1 move=18",
                "pieceName=Rook pieceId=1 move=19",
                "pieceName=Rook pieceId=1 move=20",
                "pieceName=Rook pieceId=1 move=21",
                "pieceName=Rook pieceId=1 move=22",
                "pieceName=Rook pieceId=1 move=23",
                "pieceName=Rook pieceId=1 move=24",
                "pieceName=Rook pieceId=1 move=25",
                "pieceName=Rook pieceId=1 move=26",
                "pieceName=Rook pieceId=1 move=27",
                "pieceName=Knight pieceId=2 move=0",
                "pieceName=Knight pieceId=2 move=1",
                "pieceName=Knight pieceId=2 move=2",
                "pieceName=Knight pieceId=2 move=3",
                "pieceName=Knight pieceId=2 move=4",
                "pieceName=Knight pieceId=2 move=5",
                "pieceName=Knight pieceId=2 move=6",
                "pieceName=Knight pieceId=2 move=7",
                "pieceName=Bishop pieceId=3 move=0",
                "pieceName=Bishop pieceId=3 move=1",
                "pieceName=Bishop pieceId=3 move=2",
                "pieceName=Bishop pieceId=3 move=3",
                "pieceName=Bishop pieceId=3 move=4",
                "pieceName=Bishop pieceId=3 move=5",
                "pieceName=Bishop pieceId=3 move=6",
                "pieceName=Bishop pieceId=3 move=7",
                "pieceName=Bishop pieceId=3 move=8",
                "pieceName=Bishop pieceId=3 move=9",
                "pieceName=Bishop pieceId=3 move=10",
                "pieceName=Bishop pieceId=3 move=11",
                "pieceName=Bishop pieceId=3 move=12",
                "pieceName=Bishop pieceId=3 move=13",
                "pieceName=Bishop pieceId=3 move=14",
                "pieceName=Bishop pieceId=3 move=15",
                "pieceName=Bishop pieceId=3 move=16",
                "pieceName=Bishop pieceId=3 move=17",
                "pieceName=Bishop pieceId=3 move=18",
                "pieceName=Bishop pieceId=3 move=19",
                "pieceName=Bishop pieceId=3 move=20",
                "pieceName=Bishop pieceId=3 move=21",
                "pieceName=Bishop pieceId=3 move=22",
                "pieceName=Bishop pieceId=3 move=23",
                "pieceName=Bishop pieceId=3 move=24",
                "pieceName=Bishop pieceId=3 move=25",
                "pieceName=Bishop pieceId=3 move=26",
                "pieceName=Bishop pieceId=3 move=27",
                "pieceName=King pieceId=4 move=0",
                "pieceName=King pieceId=4 move=1",
                "pieceName=King pieceId=4 move=2",
                "pieceName=King pieceId=4 move=3",
                "pieceName=King pieceId=4 move=4",
                "pieceName=King pieceId=4 move=5",
                "pieceName=King pieceId=4 move=6",
                "pieceName=King pieceId=4 move=7",
                "pieceName=King pieceId=4 move=8",
                "pieceName=Queen pieceId=5 move=0",
                "pieceName=Queen pieceId=5 move=1",
                "pieceName=Queen pieceId=5 move=2",
                "pieceName=Queen pieceId=5 move=3",
                "pieceName=Queen pieceId=5 move=4",
                "pieceName=Queen pieceId=5 move=5",
                "pieceName=Queen pieceId=5 move=6",
                "pieceName=Queen pieceId=5 move=7",
                "pieceName=Queen pieceId=5 move=8",
                "pieceName=Queen pieceId=5 move=9",
                "pieceName=Queen pieceId=5 move=10",
                "pieceName=Queen pieceId=5 move=11",
                "pieceName=Queen pieceId=5 move=12",
                "pieceName=Queen pieceId=5 move=13",
                "pieceName=Queen pieceId=5 move=14",
                "pieceName=Queen pieceId=5 move=15",
                "pieceName=Queen pieceId=5 move=16",
                "pieceName=Queen pieceId=5 move=17",
                "pieceName=Queen pieceId=5 move=18",
                "pieceName=Queen pieceId=5 move=19",
                "pieceName=Queen pieceId=5 move=20",
                "pieceName=Queen pieceId=5 move=21",
                "pieceName=Queen pieceId=5 move=22",
                "pieceName=Queen pieceId=5 move=23",
                "pieceName=Queen pieceId=5 move=24",
                "pieceName=Queen pieceId=5 move=25",
                "pieceName=Queen pieceId=5 move=26",
                "pieceName=Queen pieceId=5 move=27",
                "pieceName=Queen pieceId=5 move=28",
                "pieceName=Queen pieceId=5 move=29",
                "pieceName=Queen pieceId=5 move=30",
                "pieceName=Queen pieceId=5 move=31",
                "pieceName=Queen pieceId=5 move=32",
                "pieceName=Queen pieceId=5 move=33",
                "pieceName=Queen pieceId=5 move=34",
                "pieceName=Queen pieceId=5 move=35",
                "pieceName=Queen pieceId=5 move=36",
                "pieceName=Queen pieceId=5 move=37",
                "pieceName=Queen pieceId=5 move=38",
                "pieceName=Queen pieceId=5 move=39",
                "pieceName=Queen pieceId=5 move=40",
                "pieceName=Queen pieceId=5 move=41",
                "pieceName=Queen pieceId=5 move=42",
                "pieceName=Queen pieceId=5 move=43",
                "pieceName=Queen pieceId=5 move=44",
                "pieceName=Queen pieceId=5 move=45",
                "pieceName=Queen pieceId=5 move=46",
                "pieceName=Queen pieceId=5 move=47",
                "pieceName=Queen pieceId=5 move=48",
                "pieceName=Queen pieceId=5 move=49",
                "pieceName=Queen pieceId=5 move=50",
                "pieceName=Queen pieceId=5 move=51",
                "pieceName=Queen pieceId=5 move=52",
                "pieceName=Queen pieceId=5 move=53",
                "pieceName=Queen pieceId=5 move=54",
                "pieceName=Queen pieceId=5 move=55",
                "pieceName=Bishop pieceId=6 move=0",
                "pieceName=Bishop pieceId=6 move=1",
                "pieceName=Bishop pieceId=6 move=2",
                "pieceName=Bishop pieceId=6 move=3",
                "pieceName=Bishop pieceId=6 move=4",
                "pieceName=Bishop pieceId=6 move=5",
                "pieceName=Bishop pieceId=6 move=6",
                "pieceName=Bishop pieceId=6 move=7",
                "pieceName=Bishop pieceId=6 move=8",
                "pieceName=Bishop pieceId=6 move=9",
                "pieceName=Bishop pieceId=6 move=10",
                "pieceName=Bishop pieceId=6 move=11",
                "pieceName=Bishop pieceId=6 move=12",
                "pieceName=Bishop pieceId=6 move=13",
                "pieceName=Bishop pieceId=6 move=14",
                "pieceName=Bishop pieceId=6 move=15",
                "pieceName=Bishop pieceId=6 move=16",
                "pieceName=Bishop pieceId=6 move=17",
                "pieceName=Bishop pieceId=6 move=18",
                "pieceName=Bishop pieceId=6 move=19",
                "pieceName=Bishop pieceId=6 move=20",
                "pieceName=Bishop pieceId=6 move=21",
                "pieceName=Bishop pieceId=6 move=22",
                "pieceName=Bishop pieceId=6 move=23",
                "pieceName=Bishop pieceId=6 move=24",
                "pieceName=Bishop pieceId=6 move=25",
                "pieceName=Bishop pieceId=6 move=26",
                "pieceName=Bishop pieceId=6 move=27",
                "pieceName=Knight pieceId=7 move=0",
                "pieceName=Knight pieceId=7 move=1",
                "pieceName=Knight pieceId=7 move=2",
                "pieceName=Knight pieceId=7 move=3",
                "pieceName=Knight pieceId=7 move=4",
                "pieceName=Knight pieceId=7 move=5",
                "pieceName=Knight pieceId=7 move=6",
                "pieceName=Knight pieceId=7 move=7",
                "pieceName=Rook pieceId=8 move=0",
                "pieceName=Rook pieceId=8 move=1",
                "pieceName=Rook pieceId=8 move=2",
                "pieceName=Rook pieceId=8 move=3",
                "pieceName=Rook pieceId=8 move=4",
                "pieceName=Rook pieceId=8 move=5",
                "pieceName=Rook pieceId=8 move=6",
                "pieceName=Rook pieceId=8 move=7",
                "pieceName=Rook pieceId=8 move=8",
                "pieceName=Rook pieceId=8 move=9",
                "pieceName=Rook pieceId=8 move=10",
                "pieceName=Rook pieceId=8 move=11",
                "pieceName=Rook pieceId=8 move=12",
                "pieceName=Rook pieceId=8 move=13",
                "pieceName=Rook pieceId=8 move=14",
                "pieceName=Rook pieceId=8 move=15",
                "pieceName=Rook pieceId=8 move=16",
                "pieceName=Rook pieceId=8 move=17",
                "pieceName=Rook pieceId=8 move=18",
                "pieceName=Rook pieceId=8 move=19",
                "pieceName=Rook pieceId=8 move=20",
                "pieceName=Rook pieceId=8 move=21",
                "pieceName=Rook pieceId=8 move=22",
                "pieceName=Rook pieceId=8 move=23",
                "pieceName=Rook pieceId=8 move=24",
                "pieceName=Rook pieceId=8 move=25",
                "pieceName=Rook pieceId=8 move=26",
                "pieceName=Rook pieceId=8 move=27",
                "pieceName=Pawn pieceId=9 move=0",
                "pieceName=Pawn pieceId=9 move=1",
                "pieceName=Pawn pieceId=9 move=2",
                "pieceName=Pawn pieceId=9 move=3",
                "pieceName=Pawn pieceId=10 move=0",
                "pieceName=Pawn pieceId=10 move=1",
                "pieceName=Pawn pieceId=10 move=2",
                "pieceName=Pawn pieceId=10 move=3",
                "pieceName=Pawn pieceId=11 move=0",
                "pieceName=Pawn pieceId=11 move=1",
                "pieceName=Pawn pieceId=11 move=2",
                "pieceName=Pawn pieceId=11 move=3",
                "pieceName=Pawn pieceId=12 move=0",
                "pieceName=Pawn pieceId=12 move=1",
                "pieceName=Pawn pieceId=12 move=2",
                "pieceName=Pawn pieceId=12 move=3",
                "pieceName=Pawn pieceId=13 move=0",
                "pieceName=Pawn pieceId=13 move=1",
                "pieceName=Pawn pieceId=13 move=2",
                "pieceName=Pawn pieceId=13 move=3",
                "pieceName=Pawn pieceId=14 move=0",
                "pieceName=Pawn pieceId=14 move=1",
                "pieceName=Pawn pieceId=14 move=2",
                "pieceName=Pawn pieceId=14 move=3",
                "pieceName=Pawn pieceId=15 move=0",
                "pieceName=Pawn pieceId=15 move=1",
                "pieceName=Pawn pieceId=15 move=2",
                "pieceName=Pawn pieceId=15 move=3",
                "pieceName=Pawn pieceId=16 move=0",
                "pieceName=Pawn pieceId=16 move=1",
                "pieceName=Pawn pieceId=16 move=2",
                "pieceName=Pawn pieceId=16 move=3"
        };
        outputClassification = temp;
    }


    public static void test(){
        for(int z = 0; z < inputLayer.length; z++){
            inputLayer[z] = Math.random();
        }
    }

    // position 0 is pieceID : position 1 is moveId
    public static int[] getClassification(String classification){
        int[] values = new int[2];
        StringBuffer sb = new StringBuffer();
        for(int x = 0; x < classification.length(); x++){
            if(classification.charAt(x) != ' '){
                sb.append(classification.charAt(x));
                if(x == classification.length()-1){
                    if (sb.toString().contains("move")){
                        values[1] = Integer.parseInt(sb.substring(5, sb.length()));
                    }
                }
            }else{
                if(sb.toString().contains("pieceId")) {
                    values[0] = Integer.parseInt(sb.substring(8, sb.length()));
                    sb = new StringBuffer();
                }else{
                    sb = new StringBuffer();
                }
            }
        }

        return values;
    }


    public static String getClassificationColour(int index, ArrayList<String> array){
        String colour = "";
        StringBuffer sb = new StringBuffer();
        for(int x = 0; x < array.get(index).length(); x++){
            if(array.get(index).charAt(x) != ' '){
                sb.append(array.get(index).charAt(x));
            }else{
                if(sb.toString().contains("pieceColour")) {
                    colour = sb.substring(12, sb.length());
                    break;
                }else{
                    sb = new StringBuffer();
                }
            }
        }
        return colour;
    }


    public static String getClassificationPieceName(int index, ArrayList<String> array){
        String name = "";
        StringBuffer sb = new StringBuffer();
        for(int x = 0; x < array.get(index).length(); x++){
            if(array.get(index).charAt(x) != ' '){
                sb.append(array.get(index).charAt(x));
            }else{
                if(sb.toString().contains("pieceName")) {
                    name = sb.substring(10, sb.length());
                    break;
                }else{
                    sb = new StringBuffer();
                }
            }
        }
        return name;
    }


    public static int getClassificationPieceId(int index, ArrayList<String> array){
        int id = -1;
        StringBuffer sb = new StringBuffer();
        for(int x = 0; x < array.get(index).length(); x++){
            if(array.get(index).charAt(x) != ' '){
                sb.append(array.get(index).charAt(x));
                if(x == array.get(index).length()-1){
                    if(sb.toString().contains("pieceId")) {
                        id = Integer.parseInt(sb.substring(8, sb.length()));
                        break;
                    }
                }
            }else{
                if(sb.toString().contains("pieceId")) {
                    id = Integer.parseInt(sb.substring(8, sb.length()));
                    break;
                }else{
                    sb = new StringBuffer();
                }
            }
        }
        return id;
    }


    public static int getClassificationPromotionId(int index, ArrayList<String> array){
        String name = "";
        StringBuffer sb = new StringBuffer();
        for(int x = 0; x < array.get(index).length(); x++){
            if(array.get(index).charAt(x) != ' '){
                sb.append(array.get(index).charAt(x));
            }else{
                if(sb.toString().contains("pieceName")) {
                    name = sb.substring(10, sb.length());
                    break;
                }else{
                    sb = new StringBuffer();
                }
            }
        }
        switch (name){
            case "Queen":
                return 0;
            case "Rook":
                return 1;
            case "Knight":
                return 2;
            case "Bishop":
                return 3;
            default:
                break;
        }
        return -1;
    }


    public static void addToPawnPromotionList(String pieceColour, String pieceName, int pieceId){
        pawnPromotionList.add("pieceColour=" + pieceColour + " pieceName=" + pieceName + " pieceId=" + pieceId);
    }


    public static void removeFromPawnPromotionList(String pieceColour, String pieceName, int pieceId){
        String pieceClassification = "pieceColour=" + pieceColour + " pieceName=" + pieceName + " pieceId=" + pieceId;
        for(int i = 0; i < pawnPromotionList.size(); i++){
            if(pawnPromotionList.get(i).equalsIgnoreCase(pieceClassification)){
                pawnPromotionList.remove(i);
                break;
            }
        }
    }


    public static void setPawnPromotionClassification(String pieceColour, int promotionPiece, int pieceId){
        String pieceName = "";
        int moveAmountCounter = 0;
        switch (promotionPiece){
            case 0:
                pieceName = "Queen";
                moveAmountCounter = 56;
                break;
            case 1:
                pieceName = "Rook";
                moveAmountCounter = 28;
                break;
            case 2:
                pieceName = "Knight";
                moveAmountCounter = 28;
                break;
            case 3:
                pieceName = "Bishop";
                moveAmountCounter = 8;
                break;
            default:
                break;
        }
        for(int i = 0; i < moveAmountCounter; i++){
            pawnPromotionClassification.add("pieceColour=" + pieceColour + " pieceName=" + pieceName + " pieceId=" + pieceId + " move=" + i);
        }
    }


    public static void printUpdatedInputToHiddenWeightMatrix(){
        System.out.print("{");
        for(int i = 0; i < inputNum; i++){
            System.out.print("{");
            for(int j = 0; j < hiddenNum; j++){
                if(j != hiddenNum-1){
                    System.out.print(inputToHiddenSynapseMatrix[i][j] + ", ");
                }else{
                    System.out.print(inputToHiddenSynapseMatrix[i][j]);
                }
            }
            if(i != inputNum-1){
                System.out.print("},\n");
            }else {
                System.out.print("}");
            }
        }
        System.out.print("};\n");
    }


    public static void printUpdatedHiddenToOutputWeightMatrix(){
        System.out.print("{");
        for(int i = 0; i < hiddenNum; i++){
            System.out.print("{");
            for(int j = 0; j < outputNum; j++){
                if(j != outputNum-1){
                    System.out.print(hiddenToOutputSynapseMatrix[i][j] + ", ");
                }else{
                    System.out.print(hiddenToOutputSynapseMatrix[i][j]);
                }
            }
            if(i != hiddenNum-1){
                System.out.print("},\n");
            }else {
                System.out.print("}");
            }
        }
        System.out.print("};");
    }


    public static void generateWeightMatrix(int rows, int cols){
        System.out.print("{");
        for(int i = 0; i < rows; i++){
            System.out.print("{");
            for(int j = 0; j < cols; j++){
                if(j != cols-1){
                    System.out.print((Math.random() * 2 -1) + ", ");
                }else{
                    System.out.print((Math.random() * 2 -1));
                }
            }
            if(i != rows-1){
                System.out.print("},\n");
            }else {
                System.out.print("}");
            }
        }
        System.out.print("};");
    }


    public static void weightsToFile(String fileName, double[][] array, int rows, int cols){
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), "utf-8")))
        {
            StringBuffer sb = new StringBuffer();
            sb.append("{");
            for(int i = 0; i < rows; i++){
                sb.append("{");
                for(int j = 0; j < cols; j++){
                    if(j != cols-1){
                        sb.append(array[i][j] + ", ");
                    }else{
                        sb.append(array[i][j]);
                    }
                }
                if(i != rows-1){
                    sb.append("},\n");
                }else {
                    sb.append("}");
                }
            }
            sb.append("};");
            writer.write(sb.toString());
            writer.flush();
//            System.out.println("SUCCESSFUL WRITE TO " + fileName + ":\t" + sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
