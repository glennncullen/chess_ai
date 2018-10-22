package Chess;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Stack;
import javax.swing.*;

import NeuralNetwork.*;
import PortableGameNotation.*;


/*
	This class can be used as a starting point for creating your Chess game project. The only piece that 
	has been coded is a white pawn...a lot done, more to do!
*/
 
public class ChessProject extends JFrame implements MouseListener, MouseMotionListener {
	JLayeredPane layeredPane;
	JPanel chessBoard;
	JLabel chessPiece;
	int xAdjustment;
	int yAdjustment;
	int movedFromX;
	int movedFromY;
	int initialX;
	int initialY;
	JPanel panels;
	JLabel pieces;
	JButton exitButton, stopButton, startButton, newGameButton;
	Timer timer;
	Trainer trainer;
	boolean firstRun;


	public ChessProject(){
		firstRun = true;
		resetBoard();
    }


	//
	//  RESET BOARD
	//
	public void resetBoard() {
    	if(!firstRun){
			chessBoard.removeAll();
			layeredPane.removeAll();
		}

		Dimension boardSize = new Dimension(600, 650);

		GameHandler.init();

		layeredPane = new JLayeredPane();
		getContentPane().add(layeredPane);
		layeredPane.setPreferredSize(new Dimension(boardSize));
		layeredPane.addMouseListener(this);
		layeredPane.addMouseMotionListener(this);

		//Add a chess board to the Layered Pane
		chessBoard = new JPanel();
		layeredPane.add(chessBoard, JLayeredPane.DEFAULT_LAYER);
		chessBoard.setLayout( new GridLayout(8, 8) );
		chessBoard.setPreferredSize( boardSize );
		chessBoard.setBounds(0, 0, boardSize.width, boardSize.height-50);

		// new game button
		if(firstRun){
			newGameButton = new JButton("New Game");
		}else{
			newGameButton.revalidate();
		}
		newGameButton.setBounds(40, 610,100,30);
		layeredPane.add(newGameButton);
		newGameButton.setEnabled(true);
		newGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetBoard();
			}
		});

		// exit button
		exitButton = new JButton("Exit");
		exitButton.setBounds(180, 610, 100, 30);
		layeredPane.add(exitButton);
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		// start Button
		startButton = new JButton("Start");
		startButton.setBounds(320, 610, 100, 30);
		layeredPane.add(startButton);
		startButton.setEnabled(false);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameHandler.aiRandomPlayer = true;
				stopButton.setEnabled(true);
				timer.start();
				startButton.setEnabled(false);
			}
		});

		// stop Button
		stopButton = new JButton("Stop");
		stopButton.setBounds(460, 610, 100, 30);
		layeredPane.add(stopButton);
		stopButton.setEnabled(false);
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameHandler.aiRandomPlayer = false;
				startButton.setEnabled(true);
				timer.stop();
				stopButton.setEnabled(false);
			}
		});

		// Choose game type
		if(firstRun) {
			String[] choices = {"Exit", "Random AI V Player", "Neural Network V Player"};
			int gameChoice = JOptionPane.showOptionDialog(
					chessBoard,
					"Choose Players",
					"Start Game",
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE,
					null,
					choices,
					null);

			if (gameChoice == 1) {
				GameHandler.aiRandomPlayer = true;
			}
//			else if (gameChoice == 3) {
//				GameHandler.training = true;
//			}
			else if (gameChoice == 0) {
				System.exit(0);
			} else if (gameChoice == JOptionPane.CLOSED_OPTION) {
				System.exit(0);
			}else if(gameChoice == 2){
				GameHandler.aiNeuralNetwork = true;
			}
		}

		// Colour the Squares
		for (int i = 0; i < 64; i++) {
			JPanel square = new JPanel(new BorderLayout());
			chessBoard.add(square);

			int row = (i / 8) % 2;
			if (row == 0)
				square.setBackground(i % 2 == 0 ? Color.white : Color.gray);
			else
				square.setBackground(i % 2 == 0 ? Color.gray : Color.white);
		}

		// Setting up the Initial Chess board.
		for (int i = 8; i < 16; i++) {
			pieces = new JLabel(new ImageIcon("Chess/WhitePawn.png"));
			panels = (JPanel) chessBoard.getComponent(i);
			panels.add(pieces);
			Pawn newPiece = new Pawn(i, i - 8, 1, "White", i+1);
			GameHandler.gamePieces.add(newPiece);
		}
		pieces = new JLabel(new ImageIcon("Chess/WhiteRook.png"));
		panels = (JPanel) chessBoard.getComponent(0);
		panels.add(pieces);
		GameHandler.gamePieces.add(new Rook(0, 0, 0, "White", 1));

		pieces = new JLabel(new ImageIcon("Chess/WhiteKnight.png"));
		panels = (JPanel) chessBoard.getComponent(1);
		panels.add(pieces);
		GameHandler.gamePieces.add(new Knight(1, 1, 0, "White", 2));

		pieces = new JLabel(new ImageIcon("Chess/WhiteKnight.png"));
		new ImageIcon("");
		panels = (JPanel) chessBoard.getComponent(6);
		panels.add(pieces);
		GameHandler.gamePieces.add(new Knight(6, 6, 0, "White", 7));

		pieces = new JLabel(new ImageIcon("Chess/WhiteBishup.png"));
		panels = (JPanel) chessBoard.getComponent(2);
		panels.add(pieces);
		GameHandler.gamePieces.add(new Bishop(2, 2, 0, "White", 3));

		pieces = new JLabel(new ImageIcon("Chess/WhiteBishup.png"));
		panels = (JPanel) chessBoard.getComponent(5);
		panels.add(pieces);
		GameHandler.gamePieces.add(new Bishop(5, 5, 0, "White", 6));

		pieces = new JLabel(new ImageIcon("Chess/WhiteKing.png"));
		panels = (JPanel) chessBoard.getComponent(3);
		panels.add(pieces);
		King whiteKing = new King(3, 3, 0, "White", 4);
		GameHandler.gamePieces.add(whiteKing);
		GameHandler.kings[0] = whiteKing;

		pieces = new JLabel(new ImageIcon("Chess/WhiteQueen.png"));
		panels = (JPanel) chessBoard.getComponent(4);
		panels.add(pieces);
		GameHandler.gamePieces.add(new Queen(4, 4, 0, "White", 5));

		pieces = new JLabel(new ImageIcon("Chess/WhiteRook.png"));
		panels = (JPanel) chessBoard.getComponent(7);
		panels.add(pieces);
		GameHandler.gamePieces.add(new Rook(7, 7, 0, "White", 8));

		for (int i = 48; i < 56; i++) {
			pieces = new JLabel(new ImageIcon("Chess/BlackPawn.png"));
			panels = (JPanel) chessBoard.getComponent(i);
			panels.add(pieces);
			Pawn newPiece = new Pawn(i, i - 48, 6, "Black", (i+1)-40);
			GameHandler.gamePieces.add(newPiece);
		}

		pieces = new JLabel(new ImageIcon("Chess/BlackRook.png"));
		panels = (JPanel) chessBoard.getComponent(56);
		panels.add(pieces);
		GameHandler.gamePieces.add(new Rook(56, 0, 7, "Black", 1));

		pieces = new JLabel(new ImageIcon("Chess/BlackKnight.png"));
		panels = (JPanel) chessBoard.getComponent(57);
		panels.add(pieces);
		GameHandler.gamePieces.add(new Knight(57, 1, 7, "Black", 2));

		pieces = new JLabel(new ImageIcon("Chess/BlackKnight.png"));
		panels = (JPanel) chessBoard.getComponent(62);
		panels.add(pieces);
		GameHandler.gamePieces.add(new Knight(62, 6, 7, "Black", 7));

		pieces = new JLabel(new ImageIcon("Chess/BlackBishup.png"));
		panels = (JPanel) chessBoard.getComponent(58);
		panels.add(pieces);
		GameHandler.gamePieces.add(new Bishop(58, 2, 7, "Black", 3));

		pieces = new JLabel(new ImageIcon("Chess/BlackBishup.png"));
		panels = (JPanel) chessBoard.getComponent(61);
		panels.add(pieces);
		GameHandler.gamePieces.add(new Bishop(61, 5, 7, "Black", 6));

		pieces = new JLabel(new ImageIcon("Chess/BlackKing.png"));
		panels = (JPanel) chessBoard.getComponent(59);
		panels.add(pieces);
		King blackKing = new King(59, 3, 7, "Black", 4);
		GameHandler.gamePieces.add(blackKing);
		GameHandler.kings[1] = blackKing;

		pieces = new JLabel(new ImageIcon("Chess/BlackQueen.png"));
		panels = (JPanel) chessBoard.getComponent(60);
		panels.add(pieces);
		GameHandler.gamePieces.add(new Queen(60, 4, 7, "Black", 5));

		pieces = new JLabel(new ImageIcon("Chess/BlackRook.png"));
		panels = (JPanel) chessBoard.getComponent(63);
		panels.add(pieces);
		GameHandler.gamePieces.add(new Rook(63, 7, 7, "Black", 8));

		// create Tile objects for each square
		int tileID = 0;
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				boolean occupied = false;
				Piece piece = null;
				for (int i = 0; i < GameHandler.gamePieces.size(); i++) {
					if (GameHandler.gamePieces.get(i).getX() == x && GameHandler.gamePieces.get(i).getY() == y) {
						occupied = true;
						piece = GameHandler.gamePieces.get(i);
					}
				}
				double tileValue = 1;
				if(tileID == 27 || tileID == 28 || tileID == 35 || tileID == 36){
					tileValue = 4;
				}else if(tileID == 26 || tileID == 34 || tileID == 29 || tileID == 37){
					tileValue = 3;
				}else if(tileID == 25 || tileID == 33 || tileID == 30 || tileID == 38){
					tileValue = 2;
				}
				GameHandler.gameBoard.add(new Tile(tileID, x, y, occupied, piece, tileValue));
				// make background of occupied squares green
				if (occupied) {
					JPanel square = (JPanel) chessBoard.getComponent(tileID);
					square.setBackground(Color.green);
				}
				tileID++;
			}
		}
		for (int i = 0; i < GameHandler.gamePieces.size(); i++) {
			GameHandler.gamePieces.get(i).determinePossibleMoves();
		}
		GameHandler.setAttackingPieces();

		if(GameHandler.training && firstRun) {
			trainer = new Trainer();
			NNHandler.outputNum = 225;
			NNHandler.hiddenNum = 45;
			NNHandler.inputNum = 65;
			NNHandler.init();
		}else if(trainer != null && !firstRun){
			GameHandler.training = true;
			revalidate();
			repaint();
			startTraining();
		}else if(GameHandler.aiNeuralNetwork && firstRun){
			NNHandler.outputNum = 225;
			NNHandler.hiddenNum = 45;
			NNHandler.inputNum = 65;
			NNHandler.init();
			NNHandler.calculateInputLayer();
			NNHandler.calculateHiddenLayer();
			NNHandler.applyActivationFunction();
			NNHandler.calculateOutputLayer(false);
		}
		if(firstRun){
			firstRun = !firstRun;
		}else {
			revalidate();
			repaint();
		}

	}
	//
	// END RESET BOARD
	//


    // Start the random AI
	public void randomAiTurn(){
		timer = new Timer(GameHandler.timerSpeed, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				if(GameHandler.whiteTurn){
					chessPiece = null;
					int[] moveToMake = GameHandler.getAiNextMove();
					Piece movingPiece = GameHandler.getAiNextPiece();
					Component c = chessBoard.findComponentAt(moveToMake[0] * 75, moveToMake[1] * 75);
					chessPiece = (JLabel) chessBoard.findComponentAt(movingPiece.getX() * 75, movingPiece.getY() * 75);
					chessPiece.setLocation(movingPiece.getX() * 75, movingPiece.getY() * 75);
					chessPiece.setSize(chessPiece.getWidth(), chessPiece.getHeight());
					chessPiece.setVisible(false);
					movingPiece.isValidMove(movingPiece.getX(), movingPiece.getY());
					makeMove(movingPiece, moveToMake[0], moveToMake[1], movingPiece.getClass().getSimpleName(), movingPiece.pieceColour, c);
				}
				timer.start();
			}
		});
		timer.start();
	}


	public void neuralNetworkAiTurn(){
		timer = new Timer(GameHandler.timerSpeed, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				if(GameHandler.whiteTurn && NNHandler.whiteTurn){
					NNHandler.calculateInputLayer();
					NNHandler.calculateHiddenLayer();
					NNHandler.applyActivationFunction();
					NNHandler.calculateOutputLayer(false);
					chessPiece = null;
					int[] moveToMake = GameHandler.getNeuralNetworkMove(NNHandler.moveToMake);
					Piece movingPiece = GameHandler.aiNeuralPiece;
					Component c = chessBoard.findComponentAt(moveToMake[0] * 75, moveToMake[1] * 75);
					chessPiece = (JLabel) chessBoard.findComponentAt(movingPiece.getX() * 75, movingPiece.getY() * 75);
					chessPiece.setLocation(movingPiece.getX() * 75, movingPiece.getY() * 75);
					chessPiece.setSize(chessPiece.getWidth(), chessPiece.getHeight());
					chessPiece.setVisible(false);
					movingPiece.isValidMove(movingPiece.getX(), movingPiece.getY());
					makeMove(movingPiece, moveToMake[0], moveToMake[1], movingPiece.getClass().getSimpleName(), movingPiece.pieceColour, c);
				}
				timer.start();
			}
		});
		timer.start();
	}

	// "pieceName=Rook pieceId=1 move=0"

	// Start the Trainer
	public void startTraining(){
		final Stack<Move> gameInProgress = trainer.getGameInProgress();
		stopButton.setEnabled(true);

		while(!gameInProgress.isEmpty()) {
			chessPiece = null;
			Move currentMove = gameInProgress.pop();
//			System.out.println(currentMove.printMove() + "\nMove Number: " + gameInProgress.size());
			Piece movingPiece = null;
			if (currentMove.getComingFromX() > -1) {
				for (Tile tile : GameHandler.getGameBoard()) {
					if (tile.getPieceOnTile() != null) {
						if (tile.getPieceOnTile().getX() == currentMove.getComingFromX()) {
							for (int i = 0; i < tile.getPieceOnTile().validMoves.size(); i++) {
								if (Arrays.equals(tile.getPieceOnTile().validMoves.get(i), currentMove.getMove()) && tile.getPieceOnTile().getPieceColour().equalsIgnoreCase(currentMove.getPieceColour()) && tile.getPieceOnTile().getClass().getSimpleName().equalsIgnoreCase(currentMove.getPieceName())) {
									movingPiece = tile.getPieceOnTile();
									NNHandler.currentMove = "pieceName=" + movingPiece.getClass().getSimpleName() + " pieceId=" + movingPiece.pieceId + " move=" + i;
									break;
								}
							}
						}
					}
				}
			}else if(currentMove.getComingFromY() > -1){
				for (Tile tile : GameHandler.getGameBoard()) {
					if (tile.getPieceOnTile() != null) {
						if (tile.getPieceOnTile().getY() == currentMove.getComingFromY()) {
							for (int i = 0; i < tile.getPieceOnTile().validMoves.size(); i++) {
								if (Arrays.equals(tile.getPieceOnTile().validMoves.get(i), currentMove.getMove()) && tile.getPieceOnTile().getPieceColour().equalsIgnoreCase(currentMove.getPieceColour()) && tile.getPieceOnTile().getClass().getSimpleName().equalsIgnoreCase(currentMove.getPieceName())) {
									movingPiece = tile.getPieceOnTile();
									NNHandler.currentMove = "pieceName=" + movingPiece.getClass().getSimpleName() + " pieceId=" + movingPiece.pieceId + " move=" + i;
									break;
								}
							}
						}
					}
				}
			}else {
				for (Tile tile : GameHandler.getGameBoard()) {
					if (tile.getPieceOnTile() != null) {
						for (int i = 0; i < tile.getPieceOnTile().validMoves.size(); i++) {
							if (Arrays.equals(tile.getPieceOnTile().validMoves.get(i), currentMove.getMove()) && tile.getPieceOnTile().getPieceColour().equalsIgnoreCase(currentMove.getPieceColour()) && tile.getPieceOnTile().getClass().getSimpleName().equalsIgnoreCase(currentMove.getPieceName())) {
//								System.out.println("NOT COMING FORM X");
								movingPiece = tile.getPieceOnTile();
								NNHandler.currentMove = "pieceName=" + movingPiece.getClass().getSimpleName() + " pieceId=" + movingPiece.pieceId + " move=" + i;
								break;
							}
						}
					}
				}
			}

			Component c = chessBoard.findComponentAt(currentMove.getMove()[0] * 75, currentMove.getMove()[1] * 75);
//			System.out.println(movingPiece.printDetails() + "\n------------------\n\n");
			chessPiece = (JLabel) chessBoard.findComponentAt(movingPiece.getX() * 75, movingPiece.getY() * 75);

			chessPiece.setLocation(movingPiece.getX() * 75, movingPiece.getY() * 75);
			chessPiece.setSize(chessPiece.getWidth(), chessPiece.getHeight());
			chessPiece.setVisible(false);
			movingPiece.isValidMove(movingPiece.getX(), movingPiece.getY());
			if (currentMove.isPawnPromotion()) {
 				movingPiece.setPromotionPiece(currentMove.getPromotionPiece());
				NNHandler.setPawnPromotionClassification(movingPiece.pieceColour, movingPiece.getPromotionPiece(), movingPiece.pieceId);
			}
			makeMove(movingPiece, currentMove.getMove()[0], currentMove.getMove()[1], movingPiece.getClass().getSimpleName(), movingPiece.pieceColour, c);
		}
		if(trainer.gamesLeft()){
			NNHandler.reset();
			NNHandler.weightsToFile("InputToHiddenSynapseWeights.txt", NNHandler.inputToHiddenSynapseMatrix, NNHandler.inputNum, NNHandler.hiddenNum);
			NNHandler.weightsToFile("HiddenToOutputSynapseWeights.txt", NNHandler.hiddenToOutputSynapseMatrix, NNHandler.hiddenNum, NNHandler.outputNum);
			System.out.println("\nNumber of games left: " + trainer.numGamesLeft() + "\n");
			resetBoard();
		}
	}


	// When the Mouse is pressed
    public void mousePressed(MouseEvent e){
//    	for(Tile tile : GameHandler.gameBoard){
//    		if((e.getX()/75 == tile.getX()) && (e.getY()/75 == tile.getY())){
//    			System.out.println(tile.printDetails());
//    			if(tile.getOccupied()) {
//					for (int[] moves : tile.getPieceOnTile().validMoves) {
//						if(moves[0] != -1){
//							System.out.println(Arrays.toString(moves));
//						}
//					}
//				}
//			}
//		}
		System.out.println("\n");
		// disable mousePressed if AI is playing
		if((GameHandler.aiRandomPlayer || GameHandler.aiNeuralNetwork) && GameHandler.whiteTurn){
			return;
		}
        chessPiece = null;
        Component c =  chessBoard.findComponentAt(e.getX(), e.getY());
        if (c instanceof JPanel || c == null) {
			return;
		}

		JLabel.getDefaultLocale();

        Point parentLocation = c.getParent().getLocation();
        xAdjustment = parentLocation.x - e.getX();
        yAdjustment = parentLocation.y - e.getY();
        chessPiece = (JLabel)c;

        String pieceColour = chessPiece.getIcon().toString().substring(6, 11);
		if(pieceColour.equalsIgnoreCase("White") && !GameHandler.whiteTurn || pieceColour.equalsIgnoreCase("Black") && GameHandler.whiteTurn){
			chessPiece = null;
			return;
		}

		initialX = e.getX();
		initialY = e.getY();
		movedFromX = e.getX() / 75;
		movedFromY = e.getY() / 75;
        chessPiece.setLocation(e.getX() + xAdjustment, e.getY() + yAdjustment);
        chessPiece.setSize(chessPiece.getWidth(), chessPiece.getHeight());
        layeredPane.add(chessPiece, JLayeredPane.DRAG_LAYER);
    }

    // When the mouse is dragged
    public void mouseDragged(MouseEvent me) {
		// disable mouseDragged if AI is playing
		if((GameHandler.aiRandomPlayer || GameHandler.aiNeuralNetwork) && GameHandler.whiteTurn){
			return;
		}

        if (chessPiece == null) return;
         chessPiece.setLocation(me.getX() + xAdjustment, me.getY() + yAdjustment);
     }
     
 	// When the mouse is released
    public void mouseReleased(MouseEvent e) {
		// disable mouseReleased if AI is playing
		if((GameHandler.aiRandomPlayer || GameHandler.aiNeuralNetwork) && GameHandler.whiteTurn){
			return;
		}

		if (chessPiece == null) return;

		chessPiece.setVisible(false);
		Component c = chessBoard.findComponentAt(e.getX(), e.getY());
		String tmp = chessPiece.getIcon().toString();
		String pieceName = tmp.substring(11, (tmp.length() - 4));
		String pieceColour = tmp.substring(6, 11);
		int droppedAtX = e.getX() / 75;
		int droppedAtY = e.getY() / 75;

		Piece currentPiece = null;
		for (Piece piece : GameHandler.gamePieces) {
			if (piece.getX() == movedFromX && piece.getY() == movedFromY && !piece.taken) {
				currentPiece = piece;
				break;
			}
		}

		if(c == null){
			resetPiece(pieceName, pieceColour);
		}else {
			makeMove(currentPiece, droppedAtX, droppedAtY, pieceName, pieceColour, c);
		}
	}

	// don't move the piece and redraw it on its origin tile
	private void resetPiece(String pieceName, String pieceColour){
		int location;
		if(movedFromY == 0){
			location = movedFromX;
		}else{
			location  = (movedFromY * 8) + movedFromX;
		}
		String pieceLocation = "Chess/" + pieceColour + pieceName + ".png";
		pieces = new JLabel( new ImageIcon(pieceLocation) );
		panels = (JPanel)chessBoard.getComponent(location);
		panels.add(pieces);
	}

	// Make the piece move
	private void makeMove(Piece currentPiece, int droppedAtX, int droppedAtY, String pieceName, String pieceColour, Component c){
		Container parent = c.getParent();
		parent.invalidate();
		if(!currentPiece.isValidMove(droppedAtX, droppedAtY)) {
			resetPiece(pieceName, pieceColour);
		}else {
			if (currentPiece.pawnPromotion()) {
				int location = currentPiece.currentTileID;
				int pieceChoice = 0;
				if (GameHandler.aiRandomPlayer && GameHandler.whiteTurn) {
					pieceChoice = GameHandler.random.nextInt(4);
				}else if(GameHandler.aiNeuralNetwork && GameHandler.whiteTurn){
					pieceChoice = 0;
				}else if(GameHandler.training){
					pieceChoice = currentPiece.getPromotionPiece();
				} else {
					String[] choices = {"Queen", "Rook", "Knight", "Bishop"};
					pieceChoice = JOptionPane.showOptionDialog(
							chessBoard,
							"Choose Promotion Piece: ",
							"Pawn Promotion",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.PLAIN_MESSAGE,
							chessPiece.getIcon(),
							choices,
							null);
				}
				String newPieceName;
				if (pieceChoice == 0) {
					Queen queen = new Queen(currentPiece.currentTileID, currentPiece.x, currentPiece.y, currentPiece.pieceColour, currentPiece.pieceId);
					GameHandler.gamePieces.add(queen);
					GameHandler.gameBoard.get(location).setPieceOnTile(queen);
					newPieceName = "Queen";
					if(GameHandler.training || GameHandler.aiNeuralNetwork) {
						NNHandler.addToPawnPromotionList(currentPiece.pieceColour, newPieceName, currentPiece.pieceId);
					}
				} else if (pieceChoice == 1) {
					Rook rook = new Rook(currentPiece.currentTileID, currentPiece.x, currentPiece.y, currentPiece.pieceColour, currentPiece.pieceId);
					GameHandler.gamePieces.add(rook);
					GameHandler.gameBoard.get(location).setPieceOnTile(rook);
					newPieceName = "Rook";
					if(GameHandler.training || GameHandler.aiNeuralNetwork){
						NNHandler.addToPawnPromotionList(currentPiece.pieceColour, newPieceName, currentPiece.pieceId);
					}
				} else if (pieceChoice == 2) {
					Knight knight = new Knight(currentPiece.currentTileID, currentPiece.x, currentPiece.y, currentPiece.pieceColour, currentPiece.pieceId);
					GameHandler.gamePieces.add(knight);
					GameHandler.gameBoard.get(location).setPieceOnTile(knight);
					newPieceName = "Knight";
					if(GameHandler.training || GameHandler.aiNeuralNetwork) {
						NNHandler.addToPawnPromotionList(currentPiece.pieceColour, newPieceName, currentPiece.pieceId);
					}
				} else {
					Bishop bishop = new Bishop(currentPiece.currentTileID, currentPiece.x, currentPiece.y, currentPiece.pieceColour, currentPiece.pieceId);
					GameHandler.gamePieces.add(bishop);
					GameHandler.gameBoard.get(location).setPieceOnTile(bishop);
					newPieceName = "Bishup";
					if(GameHandler.training || GameHandler.aiNeuralNetwork) {
						NNHandler.addToPawnPromotionList(currentPiece.pieceColour, currentPiece.getClass().getSimpleName(), currentPiece.pieceId);
					}
				}
//				GameHandler.gamePieces.remove(currentPiece);
				currentPiece.taken = true;
				if (c instanceof JLabel) {
					parent.remove(0);
					pieces = new JLabel(new ImageIcon("Chess/" + pieceColour + newPieceName + ".png"));
					parent = (JPanel) chessBoard.getComponent(location);
					parent.add(pieces);
				}
				else {
					pieces = new JLabel(new ImageIcon("Chess/" + pieceColour + newPieceName + ".png"));
					parent = (JPanel) chessBoard.getComponent(location);
					parent.add(pieces);
				}
			} else {
				if (c instanceof JLabel) {
					parent.remove(0);
					parent.add(chessPiece);
				} else {
					Container newParent = (Container) c;
					newParent.add(chessPiece);
				}
				chessPiece.setVisible(true);
				// check for castling
				if (GameHandler.updateCastling) {
					int x = 0;
					int y = 0;
					int index = 0;
					// get x and y coordinates for rook depending on movement of king
					if (currentPiece.getCurrentTileID() == 1) {
						index = 2;
					} else if (currentPiece.getCurrentTileID() == 5) {
						x = 525;
						index = 4;
					} else if (currentPiece.getCurrentTileID() == 57) {
						y = 525;
						index = 58;
					} else if (currentPiece.getCurrentTileID() == 61) {
						x = 525;
						y = 525;
						index = 60;
					}
					// move rook to the appropriate square
					Component comp = chessBoard.findComponentAt(x, y);
					chessPiece = (JLabel) comp;
					String rookName = chessPiece.getIcon().toString();
					Container newParent = comp.getParent();
					newParent.remove(0);
					pieces = new JLabel(new ImageIcon(rookName));
					panels = (JPanel) chessBoard.getComponent(index);
					panels.add(pieces);
					// check for en passant
				} else if (currentPiece instanceof Pawn) {
					if (((Pawn) currentPiece).getEnPassant().size() > 0) {
						int[] enPassantMoves = ((Pawn) currentPiece).getEnPassant().get(0);
						if (droppedAtX == enPassantMoves[0] && droppedAtY == enPassantMoves[1]) {
							int x = enPassantMoves[0] * 75;
							int y = (enPassantMoves[1] - ((Pawn) currentPiece).pawnDirection()) * 75;
							Component comp = chessBoard.findComponentAt(x, y);
							chessPiece = (JLabel) comp;
							Container newParent = comp.getParent();
							newParent.remove(0);
							Piece removingPawn = GameHandler.gameBoard.get(currentPiece.currentTileID - ((Pawn) currentPiece).pawnTileDifference()).getPieceOnTile();
							GameHandler.gameBoard.get(currentPiece.currentTileID - ((Pawn) currentPiece).pawnTileDifference()).setOccupied(false);
							GameHandler.gameBoard.get(currentPiece.currentTileID - ((Pawn) currentPiece).pawnTileDifference()).setPieceOnTile(null);
//						GameHandler.gamePieces.remove(removingPawn);
							removingPawn.taken = true;
							GameHandler.takenPieces.add(removingPawn);
						}
						((Pawn) currentPiece).clearEnPassant();
					}
				}
			}
			for (Piece piece : GameHandler.gamePieces) {
				if (piece.pieceColour.equalsIgnoreCase(currentPiece.pieceColour)) {
					if (piece instanceof Pawn) {
						if (((Pawn) piece).getEnPassant().size() > 0) {
							((Pawn) piece).clearEnPassant();
						}
					}
				}
			}
			GameHandler.endTurn();
			for (int i = 0; i < GameHandler.kings.length; i++) {
				if (GameHandler.kings[i].getCheckMate()) {
					if(!GameHandler.training){
						JOptionPane.showMessageDialog(chessBoard, GameHandler.kings[i].pieceColour + " LOSES!!!");
						System.exit(0);
					}
				}else if(GameHandler.kings[i].getStaleMate()){
					if(!GameHandler.training){
						JOptionPane.showMessageDialog(chessBoard, "STALEMATE!!! NOBODY WINS!!!");
						System.exit(0);
					}
				}
			}
			// update occupied squares to have green background
			for (int i = 0; i < GameHandler.gameBoard.size(); i++) {
				JPanel square = (JPanel) chessBoard.getComponent(i);
				if (GameHandler.gameBoard.get(i).getOccupied()) {
					square.setBackground(Color.green);
				} else if ((i / 8) % 2 == 0) {
					square.setBackground(i % 2 == 0 ? Color.white : Color.gray);
				} else {
					square.setBackground(i % 2 == 0 ? Color.gray : Color.white);
				}
			}
			chessBoard.validate();
			chessBoard.repaint();
			if(GameHandler.training){
				NNHandler.calculateInputLayer();
				NNHandler.calculateHiddenLayer();
				NNHandler.applyActivationFunction();
				NNHandler.calculateOutputLayer(currentPiece.pawnPromotion());
			}else if(GameHandler.aiNeuralNetwork){
				NNHandler.whiteTurn = !NNHandler.whiteTurn;
			}
		}
    }
 
    public void mouseClicked(MouseEvent e) {
	
    }
    public void mouseMoved(MouseEvent e) {
   }
    public void mouseEntered(MouseEvent e){
	
    }
    public void mouseExited(MouseEvent e) {
	
    }
 	
	/*
		Main method that gets the ball moving.
	*/
    public static void main(String[] args) {
        JFrame frame = new ChessProject();
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE );
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
        if(GameHandler.training) {
			((ChessProject) frame).startTraining();
		}else if(GameHandler.aiRandomPlayer){
			((ChessProject) frame).randomAiTurn();
		}else if(GameHandler.aiNeuralNetwork){
			((ChessProject) frame).neuralNetworkAiTurn();
		}
     }
}
