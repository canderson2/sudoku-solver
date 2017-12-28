import java.util.*;

/**
 * @author CAA - Chris Anderson, ander231
 * CSE 586 - Artificial Intelligence
 * Homework 3 - Search 
 */
public class Square {
	
	private int row;
	private int col;
	private static final int SIZE = 9;
	
	public Square(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public Square(Square s) {
		this.row = s.row;
		this.col = s.col;
	}
	
	public String toString() {
		return "(" + this.row + "," + this.col + ")";
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int getColumn() {
		return this.col;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.row, this.col);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Square) {
			Square that = (Square) obj;
			return this.row == that.row && this.col == that.col;
		}
		return false;
	}
	
	/*
	 * Returns a set of peers for a given square.  A peer group includes all squares in the same
	 * column, row and quadrant on a Sudoku board, but excluding the given square itself.
	 */
	public Set<Square> getPeers() {
		Set<Square> peers = new HashSet<Square>();
		for(int row = 0; row < SIZE; row++)
			peers.add(new Square(row, this.col));
		
		for(int col = 0; col < SIZE; col++)
			peers.add(new Square(this.row, col));
		
		int rowQuad = this.row / 3;
		int colQuad = this. col / 3;
		int rqStart = rowQuad * 3;
		int cqStart = colQuad * 3;
		
		for(int r = rqStart; r < rqStart + 3; r++) {
			for(int c = cqStart; c < cqStart + 3; c++) {
				peers.add(new Square(r,c));
			}
		}
		peers.remove(new Square(this.row, this.col)); // a square itself is not in peer group		
		return peers;
	}

}
