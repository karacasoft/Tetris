package com.karacasoft.tetris.model;


public abstract class Tetromino {

	private int positionX;
	private int positionY;
	
	private CellState cellState;
	
	public int getPositionX() {
		return positionX;
	}
	
	public int getPositionY() {
		return positionY;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}
	
	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}
	
	public void rotateLeft()
	{
		cellState = cellState.getPreviousState();
	}
	
	public void rotateRight()
	{
		cellState = cellState.getNextState();
	}
	
	public CellState getCellState() {
		return cellState;
	}
	
	public void setCellState(CellState cellState) {
		this.cellState = cellState;
	}
	
	public void applyGravity()
	{
		this.positionY++;
	}
}
