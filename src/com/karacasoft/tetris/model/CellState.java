package com.karacasoft.tetris.model;

import java.util.ArrayList;
/**
 * CellState for a Tetromino.
 * 
 * A CellState holds what pieces the Tetromino holds. It also has references for rotations.
 * 
 * Rotation references are held on nextState and previousState.
 * If someone wants the left-rotated version of the given Tetromino.
 * 
 * @author Triforce
 *
 */
public class CellState {

	private CellState nextState;
	private CellState previousState;
	
	private ArrayList<Cell> cells = new ArrayList<Cell>();

	private int topLeftCellX;
	private int topLeftCellY;
	
	public ArrayList<Cell> getCells() {
		return cells;
	}
	
	public CellState getNextState() {
		return nextState;
	}
	
	public CellState getPreviousState() {
		return previousState;
	}
	
	public void setNextState(CellState nextState) {
		this.nextState = nextState;
	}
	
	public void setPreviousState(CellState previousState) {
		this.previousState = previousState;
	}
	
	/**
	 * Returns the cell at given coordinates.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return the cell at x,y or null if no such cell was found.
	 */
	public Cell getCellAt(int x, int y) {
		for (Cell cell : cells) {
			if(cell.getPositionX() == x && cell.getPositionY() == y)
			{
				return cell;
			}
		}
		return null;
	}

	public int getTopLeftCellX() {
		return topLeftCellX;
	}

	public void setTopLeftCellX(int topLeftCellX) {
		this.topLeftCellX = topLeftCellX;
	}

	public int getTopLeftCellY() {
		return topLeftCellY;
	}

	public void setTopLeftCellY(int topLeftCellY) {
		this.topLeftCellY = topLeftCellY;
	}
}
