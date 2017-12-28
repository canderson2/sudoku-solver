import java.io.*;

/**
 * @author CAA - Chris Anderson, ander231
 * CSE 586 - Artificial Intelligence
 * Homework 3 - Search 2
 */
public class Driver {

	
	/*
	 * Solves all puzzles provided with the homework instructions.
	 */
	public static void main(String[] args) throws FileNotFoundException {
		solvePuzzle("sudoku1.txt");
		solvePuzzle("sudoku2.txt");
		solvePuzzle("sudoku3.txt");
		solvePuzzle("sudoku4.txt");
		solvePuzzle("sudoku5.txt");
	}
	
	
	/*
	 * Solves an instance of a Sudoku puzzle and prints the solution and time to solve.
	 */
	private static void solvePuzzle(String puzzleName) throws FileNotFoundException {
		System.out.println("Solving puzzle: " + puzzleName + "\n");
		System.out.println("Puzzle solution: \n");
		long startTime = System.nanoTime();
		Sudoku s = new Sudoku(puzzleName);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		double seconds = duration / 1000000000.0;
		System.out.println("\nSolve time: " + seconds + " seconds\n");
		System.out.println("---------------------------------------\n");
	}
	
}