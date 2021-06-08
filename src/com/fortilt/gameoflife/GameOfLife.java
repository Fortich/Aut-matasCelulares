package com.fortilt.gameoflife;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;

import static java.lang.Integer.parseInt;

import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

public class GameOfLife implements ActionListener, Runnable {

	final static String ALIVE_CELL = "■ ";

	final static String DEAD_CELL = "□ ";

	JFrame mainFrame;

	JTextArea mainTextAreaToDraw;

	JTextField updateSpeedTextField;
	JTextField filePathTextField;

	JPanel cellNumberPanel;
	JPanel welcomePanel;

	JLabel cellNumberLabel;
	JLabel welcomeLabel;

	JButton randomCaseButton;
	JButton fileCaseButton;

	JButton exitButton;
	JButton restartButton;

	Thread mainThread;

	String filePathString;
	int updateSpeedInteger;

	boolean[][] cellArray;
	boolean[][] cellArrayCopy;

	boolean stopFlag = false;

	public GameOfLife() { //Constructor, parte de las ventanas y llamado a celulitas

		mainThread = new Thread(this);

		cellArray = new boolean[40][90];

		mainFrame = new JFrame("Game of life");

		mainFrame.setSize(1250, 710);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainTextAreaToDraw = new JTextArea();

		cellNumberPanel = new JPanel();

		updateSpeedTextField = new JTextField("Frame update speed in milliseconds");
		updateSpeedTextField.setBounds(475, 150, 300, 50);

		filePathTextField = new JTextField("Filepath to case");
		filePathTextField.setBounds(475, 550, 300, 50);

		randomCaseButton = new JButton("Random case");
		randomCaseButton.setBounds(525, 250, 200, 100);
		randomCaseButton.addActionListener(this);

		fileCaseButton = new JButton("File case");
		fileCaseButton.setBounds(525, 400, 200, 100);
		fileCaseButton.addActionListener(this);

		exitButton = new JButton("Exit");
		exitButton.addActionListener(this);

		restartButton = new JButton("Restart");
		restartButton.addActionListener(this);

		cellNumberPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		cellNumberPanel.setSize(new Dimension(mainFrame.getWidth(), 25));

		cellNumberLabel = new JLabel("");

		cellNumberPanel.add(restartButton);
		cellNumberPanel.add(cellNumberLabel);
		cellNumberPanel.add(exitButton);

		welcomePanel = new JPanel();
		welcomePanel.setBounds(525, 100, 200, 100);
		welcomeLabel = new JLabel("Welcome to Game of Life");

		welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

		mainFrame.add(filePathTextField);
		mainFrame.add(updateSpeedTextField);
		mainFrame.add(fileCaseButton);
		mainFrame.add(randomCaseButton);
		mainFrame.add(welcomePanel);

		mainFrame.setVisible(true);
		mainFrame.setResizable(false);

		cellNumberPanel.setSize(250, 50);
		cellNumberPanel.setVisible(true);

	}

	static boolean isBorderPosition(boolean[][] board, int i, int j) {

		final boolean isUpperBorder = i == 0;
		final boolean isLeftBorder = j == 0;
		final boolean isRightBorder = i == board.length - 1;
		final boolean isBottomBorder = j == board[0].length - 1;
		return isUpperBorder ||
				isBottomBorder ||
				isRightBorder ||
				isLeftBorder;
	}

	static boolean isCellAlive(boolean[][] board, int i, int j) {

		return board[i][j];
	}

	static boolean isToDrawAliveCell(boolean[][] board, int i, int j) {

		return isBorderPosition(board, i, j) || isCellAlive(board, i, j);
	}

	static String getStringToDrawCells(boolean[][] board) {

		StringBuilder resultStringBuilder = new StringBuilder();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (isToDrawAliveCell(board, i, j)) {
					resultStringBuilder.append(ALIVE_CELL);
				} else {
					resultStringBuilder.append(DEAD_CELL);
				}
			}
			resultStringBuilder.append("\n");
		}
		return resultStringBuilder.toString();
	}

	static int contador_aux(boolean[][] x, int i, int j) {

		if (i == 0 || j == 0) {
			if (x[i][j]) {
				return 1;
			} else {
				return 0;
			}
		} else if (i > j) {
			if (x[i][j]) {
				return 1 + contador_aux(x, i, j - 1);
			} else {
				return contador_aux(x, i, j - 1);
			}
		} else if (j > i) {
			if (x[i][j]) {
				return 1 + contador_aux(x, i - 1, j);
			} else {
				return contador_aux(x, i - 1, j);
			}
		} else {
			if (x[i][j]) {
				return 1 + contador_aux(x, i - 1, j - 1) + contador_aux(x, i - 1, j) + contador_aux(x, i, j - 1);
			} else {
				return contador_aux(x, i - 1, j - 1) + contador_aux(x, i - 1, j) + contador_aux(x, i, j - 1);
			}
		}
	}

	static String countCellsAliveRecursiveMethod(boolean[][] x) {//Función que da el número de células vivas
		int acumulador;
		StringBuilder sb = new StringBuilder();
		acumulador = contador_aux(x, x.length - 1, x.length - 1);
		sb.append(acumulador);
		return sb.toString();
	}

	static boolean[][] countCellsAliveIterativeMethod(boolean[][] x) {

		boolean[][] y;
		if (x.length > x[0].length) {
			y = new boolean[x.length][x.length];
			for (int i = 0; i < x.length; i++) {
				for (int j = 0; j < x[0].length; j++) {
					y[i][j] = x[i][j];
				}
			}
		} else {
			y = new boolean[x[0].length][x[0].length];
			for (int i = 0; i < x.length; i++) {
				for (int j = 0; j < x[0].length; j++) {
					y[i][j] = x[i][j];
				}
			}
		}
		return y;
	}

	static int numberOfNeighbors(boolean[][] x, int i, int j) {//Cuenta la cantidad de numberOfNeighbors de una celda
		int countOfNeighbors = 0;
		if (x[i - 1][j - 1]) {
			countOfNeighbors++;
		}
		if (x[i][j - 1]) {
			countOfNeighbors++;
		}
		if (x[i + 1][j - 1]) {
			countOfNeighbors++;
		}
		if (x[i - 1][j]) {
			countOfNeighbors++;
		}
		if (x[i + 1][j]) {
			countOfNeighbors++;
		}
		if (x[i - 1][j + 1]) {
			countOfNeighbors++;
		}
		if (x[i][j + 1]) {
			countOfNeighbors++;
		}
		if (x[i + 1][j + 1]) {
			countOfNeighbors++;
		}
		return countOfNeighbors;
	}

	static boolean nextGenerationCellKeepsAlive(int numberOfNeighborsOfCell) {

		return numberOfNeighborsOfCell == 2 || numberOfNeighborsOfCell == 3;
	}

	static boolean nextGenerationCellBeReborn(int numberOfNeighborsOfCell) {

		return numberOfNeighborsOfCell == 3;
	}

	static boolean nextGenerationCellIsGonnaBeAlive(boolean[][] board, int i, int j) {

		final int numberOfNeighborsOfCell = numberOfNeighbors(board, i, j);
		if (board[i][j]) {
			return nextGenerationCellKeepsAlive(numberOfNeighborsOfCell);
		} else {
			return nextGenerationCellBeReborn(numberOfNeighborsOfCell);
		}
	}

	static boolean[][] updateBoard(boolean[][] board) {

		boolean[][] newBoard = new boolean[board.length][board[0].length];
		for (int i = 1; i < (board.length - 1); i++) {
			for (int j = 1; j < (board[0].length - 1); j++) {
				newBoard[i][j] = nextGenerationCellIsGonnaBeAlive(board, i, j);
			}
		}
		return newBoard;
	}

	static void doWait(final int milliseconds) {//Para el hilo
		Thread.sleep(milliseconds);
		//http://www.guillerweb.com/bytes/2009/02/02/esperar-un-tiempo-en-java/comment-page-1/
	}

	static boolean[][] randomCase() {//Hace una matriz booleana c:
		boolean[][] board = new boolean[40][95];
		Random rm = new Random();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				board[i][j] = rm.nextBoolean();
			}
		}
		return board;
	}

	static boolean[][] filePathCase(String filePath) {//Hace una matriz booleana con un archivo
		boolean[][] board = new boolean[40][95];
		FileReader fr = null;

		try {
			fr = new FileReader(filePath);
			int ch;
			int ch2 = 0;
			boolean flag = true;
			int i = 0;
			int j = 0;
			StringBuilder numbers = new StringBuilder();
			while ((ch = fr.read()) != -1) {
				if (((ch - 48) <= 10) && ((ch - 48) >= 0)) {
					numbers.append((char) ch);
				} else if (((ch2 - 48) <= 10) && ((ch2 - 48) >= 0)) {
					if (flag) {
						j = parseInt(numbers.toString());
						numbers.setLength(0);
						flag = false;
					} else {
						i = parseInt(numbers.toString());
						board[i][j] = true;
						numbers.setLength(0);
						flag = true;
					}
				}
				ch2 = ch;
			}
		} catch (Exception e) {

		}
		return board;
	}

	void setMainFrame() {

		try {
			updateSpeedInteger = parseInt(updateSpeedTextField.getText());
		} catch (Exception e) {
			updateSpeedInteger = 150;
		}
		mainFrame.remove(fileCaseButton);
		mainFrame.remove(randomCaseButton);
		mainFrame.remove(welcomePanel);
		mainFrame.remove(updateSpeedTextField);
		mainFrame.remove(filePathTextField);
		mainFrame.add(mainTextAreaToDraw, BorderLayout.CENTER);
		mainFrame.add(cellNumberPanel, BorderLayout.SOUTH);
	}

	public static void main(String[] args) {

		new GameOfLife();
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource() == randomCaseButton) {
			setMainFrame();
			cellArray = randomCase();
			cellArrayCopy = cellArray;
			mainThread.start();
		} else if (event.getSource() == fileCaseButton) {
			setMainFrame();
			filePathString = filePathTextField.getText();
			cellArray = filePathCase(filePathString);
			cellArrayCopy = cellArray;
			mainThread.start();
		} else if (event.getSource() == exitButton) {
			System.exit(0);
		} else if (event.getSource() == restartButton) {
			stopFlag = true;
			cellArray = cellArrayCopy;
			stopFlag = false;
		}

	}

	@Override
	public void run() {

		while (!(stopFlag)) {
			mainTextAreaToDraw.setText(getStringToDrawCells(cellArray));
			cellArray = updateBoard(cellArray);
			doWait(updateSpeedInteger);
			cellNumberLabel.setText(
					"\t" + "Cell number: " + countCellsAliveRecursiveMethod(countCellsAliveIterativeMethod(cellArray)) + "\t");
		}

	}

}
