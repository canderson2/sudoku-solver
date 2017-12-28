import java.io.*;
import java.util.*;

/**
 * @author CAA - Chris Anderson, ander231
 * CSE 586 - Artificial Intelligence
 * Homework 3 - Search 2
 */
public class Board {

	private HashMap<Square, String> board; // maps square position to domains/values
	private final HashMap<Square, Set<Square>> peerMap; // maps squares to all of its peers/neighbors
	private static final int SIZE = 9;
	
	
	/*
	 * Reads a .txt file representation of a sudoku puzzle.
	 */
	public Board(String startBoard) throws FileNotFoundException {
		board = new HashMap<Square, String>();
		// read in txt file to populate the map of squares to domains
		Scanner input = new Scanner(new File(startBoard));
		for(int row = 0; row < SIZE; row++) {
			String line = input.nextLine();
			for(int col = 0; col < SIZE; col++) {
				String value = null;
				if(line.charAt(col) == '-')
					value = "123456789";
				else
					value = Character.toString(line.charAt(col));
				board.put(new Square(row, col), value);
			}
		}
		peerMap = getPeerMap();
		reduceStartBoard(); // eliminate conflicts with permanently placed values
	}
	
	
	/*
	 * Private helper method to map puzzle squares to all squares in its peer/neighbor group.
	 * A peer group includes all squares in the same column, row and quadrant.
	 */
	private HashMap<Square, Set<Square>> getPeerMap() {
		HashMap<Square, Set<Square>> result = new HashMap<Square, Set<Square>>();
		for(Square curr : board.keySet()) {
			result.put(curr, curr.getPeers());
		}
		return result;
	}
	
	
	/*
	 * Private helper method to ensures arc consistency between initially places values in the
	 * starting board and the domains of peer/neighboring squares.
	 */
	private void reduceStartBoard() {
		for(Square curr : board.keySet()) {
			if(isAssigned(curr)) {
				forwardCheck(curr, getDomain(curr));
			}
		}	
	}
	
	/*
	 * Prints the current representation of the game board.  If the game board is solved, it
	 * prints the correctly placed values at each square.  If the game board is not solved, it
	 * prints the domains of each square. 
	 */
	public void print() {
		if(isSolved())
			printFormatted();
		else 
			printWithTabs();	
	}
	
	
	/*
	 * Private helper method to print a formatted, solved puzzle.
	 */
	public void printFormatted() {
		for(int i = 0; i < SIZE; i++) {
			if(i % 3 == 0) {
				System.out.println("------------------------");
			}
			for(int j = 0; j < SIZE; j++) {
				if(j % 3 == 0) {
					System.out.print("| ");
				}
				System.out.print(board.get(new Square(i,j)) + " ");
			}
			System.out.println();
		}
	}
	
	
	/*
	 * Private helper method to print the domains of each square.
	 */
	private void printWithTabs() {
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < SIZE; j++) {
				String domain = board.get(new Square(i,j));
				System.out.printf("%-11s",domain);
			}
			System.out.println();
		}
	}
	
	
	/*
	 * Returns a set of Squares that represent the peer/neighboring group for a given square.
	 */
	public Set<Square> getPeers(Square curr) {
		return peerMap.get(curr);
	}
	
	
	/*
	 * Returns the number of values left in the domain of a given Square.
	 */
	public int getDomainSize(Square curr) {
		return board.get(curr).trim().length();
	}
	
	
	/*
	 * Returns true of a Square has been assigned a value / if the domain size is 1.
	 */
	public boolean isAssigned(Square curr) {
		return getDomainSize(curr) == 1;
	}
	
	
	/*
	 * Sets the domain of a given Square if it exists on the Sudoku board.
	 */
	public void setDomain(Square curr, String newDomain) {
		if(!board.keySet().contains(curr))
			return;
		board.put(curr, newDomain.trim());
	}
	
	
	/*
	 * Returns the domain for a given Square on the Sudoku board.
	 */
	public String getDomain(Square curr) {
		return board.get(curr);
	}
	
	
	/*
	 * Ensures arc consistency between a square when assigned a given value by eliminating
	 * that value from the domain of its peers/neighbors.  Returns a set of all Squares
	 * whose domains were reduced by applying forward checking.
	 */
	public Set<Square> forwardCheck(Square curr, String value) {
		Set<Square> peers = getPeers(curr);
		Set<Square> impacted = new HashSet<Square>();
		for(Square peer : peers) {
			if(getDomain(peer).contains(value)) {
				// remove value from the peer's domain
				String newDomain = getDomain(peer).replace(value, "").trim();
				setDomain(peer, newDomain);
				impacted.add(peer);
			}
		}
		return impacted;
	}
	
	
	/*
	 * Adds a value to the domain of a given Square.
	 */
	public void addValueToDomain(Square curr, String value) {
		String oldDomain = getDomain(curr);
		String newDomain = value.trim() + oldDomain.trim();
		setDomain(curr, newDomain);
	}
	
	
	/*
	 * Adds a value to the domains of all squares in the set.
	 */
	public void addValueToAllDomains(Set<Square> affected, String value) {
		for(Square peer : affected) {
			addValueToDomain(peer, value);
		}
	}
	
	
	/*
	 * Returns true if all squares in the Sudoku board have been assigned and no constraint
	 * conflicts exist (e.g., all columns, rows and quadrants contain unique values).
	 */
	public boolean isSolved() {
		Set<Square> squares = board.keySet();
		for(Square curr : squares) {
			if(!isAssigned(curr))
				return false;
			
			Set<Square> peers = getPeers(curr);
			for(Square peer : peers) {
				if(getDomain(curr).equals(getDomain(peer)))
					return false;
			}
		}
		return true;
	}
	
	
	/*
	 * Returns an ArrayList of all values in the domain of a given Square.
	 */
	public ArrayList<String> getValuesInDomain(Square curr) {
		ArrayList<String> domainValues = new ArrayList<String>();
		String domain = getDomain(curr);
		int domainSize = domain.length();
		
		for(int i = 0; i < domainSize; i++) {
			domainValues.add(Character.toString(domain.charAt(i)));
		}
		return domainValues;
	}
	
	
	/*
	 * Returns a sorted ArrayList of all values in the domain of a given Square, sorted in order
	 * of values that impose the fewest constraints / rule out the fewest choices for neighboring
	 * Squares (e.g., those in the same column, row or quadrant).
	 */
	public ArrayList<String> getValuesInDomainByLCV(Square curr) {
		
		ArrayList<String> values = getValuesInDomain(curr);
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<LCVPair> lcvPairs = new ArrayList<Board.LCVPair>();
		
		for(String value : values)
			lcvPairs.add(new LCVPair(curr, value, getNumDomainsImpacted(curr, value)));
		
		// sort so that the LCV pairs with the fewest number of constraints are first in the list
		Collections.sort(lcvPairs);
		
		for(LCVPair pair : lcvPairs)
			result.add(pair.value);
		
		return result;
	}
	
	
	
	/*
	 * Returns a count of the number of domains that would be impacted if the current square were
	 * assigned a given value.
	 */
	private int getNumDomainsImpacted(Square curr, String value) {
		Set<Square> peers = getPeers(curr);
		int count = 0;
		for(Square peer : peers) {
			if(getDomain(peer).contains(value)) {
				count++;
			}
		}
		return count;
	}
	
	
	/*
	 * Returns an ArrayList of all Squares that are not assigned a value / have a domain size
	 * of greater than 1.
	 */
	private ArrayList<Square> getUnassignedSquares() {
		Set<Square> squares = board.keySet();
		ArrayList<Square> unassigned = new ArrayList<Square>();
		for(Square curr : squares) {
			if(!isAssigned(curr))
				unassigned.add(curr);
		}
		return unassigned;
	}
	
	
	/*
	 * Returns an unassigned Square from the Sudoku board that has the fewest possible values left.
	 */
	public Square getMRVSquare() {
		ArrayList<Square> unassigned = getUnassignedSquares();
		Square minDomainSquare = unassigned.get(0);
		int minDomainSize = getDomainSize(minDomainSquare);
		
		for(Square curr : unassigned) {
			int currDomainSize = getDomainSize(curr);
			if(currDomainSize < minDomainSize) {
				minDomainSquare = curr;
				minDomainSize = currDomainSize;
			}
		}
		return minDomainSquare;
	}
	
	
	/*
	 * Returns true if the assignment is valid, and false if the assignment would violate a
	 * constraint with another assigned variable on the board (e.g., if a Square in the same column,
	 * row or quadrant has the same value).
	 */
	public boolean isValidAssignment(Square curr, String value) {
		Set<Square> peers = getPeers(curr);
		String currDomain = getDomain(curr);
		for(Square peer : peers) {
			if(!isAssigned(peer))
				continue;
			// if assigned, ensure that the peer domain doesn't equal the value
			if(getDomain(peer).equals(value))
				return false;
		}
		return true;
	}
	
	
	/*
	 * Returns true if a constraint violation is detected on the Sudoku board.
	 */
	public boolean hasConstraintViolation() {
		Set<Square> squares = board.keySet();
		for(Square curr : squares) {
			Set<Square> peers = getPeers(curr);
			for(Square peer : peers) {
				if(isAssigned(curr) && isAssigned(peer) && getDomain(curr).equals(getDomain(peer))) {
					return true;
				}
					
			}
		}
		return false;
	}
	
	
	/*
	 * Inner class used to assist in sorting domain values of a Square by least constraining value.
	 */
	class LCVPair implements Comparable<LCVPair> {
		protected Square square;
		protected String value;
		protected int numConflicts;
		
		public LCVPair(Square s, String v, int c) {
			this.square = s;
			this.value = v;
			this.numConflicts = c;
		}
		
		public int compareTo(LCVPair other) {
			return this.numConflicts - other.numConflicts;
		}
	}
}
