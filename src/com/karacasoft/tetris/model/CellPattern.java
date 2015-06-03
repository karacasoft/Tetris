package com.karacasoft.tetris.model;

import java.util.ArrayList;

import com.karacasoft.tetris.ai.InputSchema;
/**
 * Designed to search patterns on {@link TileMap}.
 * 
 * @author Triforce
 *
 */
public class CellPattern {

	/**
	 * Cells that can be searched on {@link TileMap}
	 * @author Triforce
	 *
	 */
	public class SearchCell
	{
		public int x;
		public int y;
		public boolean isEmpty;
	}
	
	private ArrayList<SearchCell> cells = new ArrayList<CellPattern.SearchCell>();
	
	private int preferRate = 0;
	
	private int blockPlacePositionX;
	private int blockPlacePositionY;
	
	private int rotationNeeded = 0;
	
	private InputSchema dropDownInput;
	
	private boolean mustHaveEmptyTop = true;
	
	public CellPattern() {}
	/**
	 * Add new {@link SearchCell} according to a {@link Cell}.
	 * @param c Cell
	 * @return Returns itself for chaining.
	 */
	public CellPattern addCell(Cell c) {
		SearchCell cellToAdd = new SearchCell();
		cellToAdd.x = c.getPositionX();
		cellToAdd.y = c.getPositionY();
		cellToAdd.isEmpty = false;
		cells.add(cellToAdd);
		return this;
	}
	
	/**
	 * Adds a new {@link SearchCell} to the cells.
	 * @param cell SearchCell
	 * @return Returns itself for chaining calls.
	 */
	public CellPattern addCell(SearchCell cell)
	{
		cells.add(cell);
		return this;
	}
	
	/**
	 * Add a new {@link SearchCell} at x, y coordinates and define if it should be empty or not.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param isEmpty is Cell Empty
	 * @return Returns itself for chaining calls.
	 */
	public CellPattern addCell(int x, int y, boolean isEmpty)
	{
		SearchCell cellToAdd = new SearchCell();
		cellToAdd.x = x;
		cellToAdd.y = y;
		cellToAdd.isEmpty = isEmpty;
		cells.add(cellToAdd);
		return this;
	}
	
	/**
	 * Adds a new empty {@link SearchCell} at given coordinates.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return Returns itself for chaining calls.
	 */
	public CellPattern addEmptyCell(int x, int y)
	{
		SearchCell cellToAdd = new SearchCell();
		cellToAdd.x = x;
		cellToAdd.y = y;
		cellToAdd.isEmpty = true;
		cells.add(cellToAdd);
		return this;
	}
	
	public ArrayList<SearchCell> getCells() {
		return cells;
	}
	
	/**
	 * THIS IS JUST FOR DEBUGGING PURPOSES!
	 * Visualizes the pattern on console.
	 */
	public void visualizeOnConsole()
	{
		int y = 0;
		for (SearchCell searchCell : cells) {
			while(searchCell.y > y)
			{
				System.out.println();
				y++;
			}
			if(searchCell.isEmpty)
				System.out.print("o");
			else
				System.out.print("-");
		}
	}

	public int getPreferRate() {
		return preferRate;
	}

	public void setPreferRate(int preferRate) {
		this.preferRate = preferRate;
	}

	public int getBlockPlacePositionX() {
		return blockPlacePositionX;
	}

	public void setBlockPlacePositionX(int blockPlacePositionX) {
		this.blockPlacePositionX = blockPlacePositionX;
	}

	public int getBlockPlacePositionY() {
		return blockPlacePositionY;
	}

	public void setBlockPlacePositionY(int blockPlacePositionY) {
		this.blockPlacePositionY = blockPlacePositionY;
	}

	public int getRotationNeeded() {
		return rotationNeeded;
	}

	public void setRotationNeeded(int rotationNeeded) {
		this.rotationNeeded = rotationNeeded;
	}

	public InputSchema getDropDownInput() {
		return dropDownInput;
	}

	public void setDropDownInput(InputSchema dropDownInput) {
		this.dropDownInput = dropDownInput;
	}

	public boolean isMustHaveEmptyTop() {
		return mustHaveEmptyTop;
	}

	public void setMustHaveEmptyTop(boolean mustHaveEmptyTop) {
		this.mustHaveEmptyTop = mustHaveEmptyTop;
	}
}
