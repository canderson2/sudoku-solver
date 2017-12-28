import java.io.*;
import java.util.*;

/**
 * @author CAA - Chris Anderson, ander231
 * CSE 586 - Artificial Intelligence
 * Homework 3 - Search 2
 */
public class Sudoku {

	
	/*
	 * Constructor to solve a Sudoku puzzle with DFS recursive backtracking.
	 */
	public Sudoku(String fileName) throws FileNotFoundException {
		Board currBoard = new Board(fileName); 
		if(backtrackSolve(currBoard))
			currBoard.print();
		else
			System.out.println("No solution found.");
	}
	
	
	/*
	 * Implements a recursive DFS backtracking algorithm to solve a Sudoku puzzle.
	 * Uses Minimum Remaining Value ("MRV"), Least Constraining Value ("LCV") and
	 * Forward Checking heuristics to prune the search space. 
	 */
	public boolean backtrackSolve(Board currBoard) {
		
		// stop backtracking if solution has been found
		if(currBoard.isSolved())
			return true;

		// MRV heuristic - get next unassigned variable with fewest values left in domain
		Square currSquare = currBoard.getMRVSquare();
		String currDomain = currBoard.getDomain(currSquare);
		
		// LCV heuristic - process values in order that rule out fewest choices for neighbors 
		ArrayList<String> possibleValues = currBoard.getValuesInDomainByLCV(currSquare);
		
		for(String value : possibleValues) {
			
			// skip values that would invalidate the domains of peer/neighbor variables
			if(!currBoard.isValidAssignment(currSquare, value))
				continue;
				
			currBoard.setDomain(currSquare, value);
			
			// forward checking heuristic - ensure arc consistency from currSquare to all neighbors
			Set<Square> affected = currBoard.forwardCheck(currSquare, value);
			
			// if forward checking produces failure, reset affected squares and try next value
			if(currBoard.hasConstraintViolation()) {
				currBoard.addValueToAllDomains(affected, value);
				currBoard.setDomain(currSquare, currDomain);
				continue;
			}
			
			// recursively call DFS backtrack search with curr board
			if(backtrackSolve(currBoard))
				return true;
			
			// if backtracking, reset affected squares and try again with next value
			currBoard.addValueToAllDomains(affected, value);
			currBoard.setDomain(currSquare, currDomain);
		}
		return false;
	}
}