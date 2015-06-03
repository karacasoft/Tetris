package com.karacasoft.tetris.model;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import com.karacasoft.tetris.model.CellPattern.SearchCell;

public class TileMap {

	private int tileWidthInPixels = 20;
	private int tileHeightInPixels = 20;
	
	private int widthInTiles = 10;
	private int heightInTiles = 20;
	
	private Cell[][] cells; 
	
	public TileMap() {
		this.cells = new Cell[widthInTiles][heightInTiles];
	}
	
	public TileMap(int widthInTiles, int heightInTiles) {
		this.widthInTiles = widthInTiles;
		this.heightInTiles = heightInTiles;
		this.cells = new Cell[widthInTiles][heightInTiles];
	}
	
	public int getTileWidthInPixels() {
		return tileWidthInPixels;
	}
	public void setTileWidthInPixels(int tileWidthInPixels) {
		this.tileWidthInPixels = tileWidthInPixels;
	}
	public int getTileHeightInPixels() {
		return tileHeightInPixels;
	}
	public void setTileHeightInPixels(int tileHeightInPixels) {
		this.tileHeightInPixels = tileHeightInPixels;
	}
	public int getWidthInTiles() {
		return widthInTiles;
	}
	public int getHeightInTiles() {
		return heightInTiles;
	}
	public int getWidth()
	{
		return tileWidthInPixels * widthInTiles;
	}
	public int getHeight() {
		return tileHeightInPixels * heightInTiles;
	}
	public Cell getCellAt(int x, int y)
	{
		try
		{
			return cells[x][y];
		}catch(ArrayIndexOutOfBoundsException e)
		{
			return null;
		}
	}
	public ArrayList<Cell> getCells()
	{
		ArrayList<Cell> cellArray = new ArrayList<Cell>();
		for (Cell[] cellColumn : cells) {
			for (Cell cell : cellColumn) {
				if(cell != null)
					cellArray.add(cell);
			}
		}
		return cellArray;
	}
	
	public ArrayList<Cell> getRow(int index)
	{
		ArrayList<Cell> cellArray = new ArrayList<Cell>();
		for (Cell[] cGroup : cells) {
			Cell cell = cGroup[index];
			if(cell != null)
				cellArray.add(cell);
		}
		return cellArray;
	}
	
	public void dropRows(int toIndex)
	{
		for(int i = toIndex; i >= 1; i--)
		{
			for (Cell[] cells2 : cells) {
				cells2[i] = cells2[i - 1];
				if(cells2[i] != null)
					cells2[i].setPositionY(cells2[i].getPositionY() + 1);
			}
			
		}
		for (Cell[] cGroup : cells) {
			cGroup[0] = null;
		}
		
	}
	
	public void addCell(Cell c)
	{
		cells[c.getPositionX()][c.getPositionY()] = c;
	}
	public void changeCellAt(int x, int y, Cell cellToReplace)
	{
		cells[x][y] = cellToReplace;
	}
	
	public void addTilesFromBottom(int count)
	{
		Random r = new Random();
		for(int i = 0; i < count; i++)
		{
			int randomEmptySpace = r.nextInt(10);
			for (Cell[] cells2 : cells) {
				for(int column = 0; column < 19; column++)
				{
					cells2[column] = cells2[column + 1];
					if(cells2[column] != null)
						cells2[column].setPositionY(column);
				}
			}
			int columnCount = 0;
			for (Cell[] cGroup : cells) {
				if(columnCount != randomEmptySpace)
				{
					cGroup[19] = new Cell(columnCount,
							19, AssetManager.getInstance().getImage("block_disappear1"),
							Color.WHITE);
				}else{
					cGroup[19] = null;
				}
				columnCount++;
			}
		}
	}
	
	public ArrayList<Point> searchPattern(CellPattern pattern)
	{
		ArrayList<Point> points = new ArrayList<Point>();
		for(int x = 0; x < 10; x++)
		{
			for(int y = 0; y < 20; y++)
			{
				boolean patternOkFlag = true;
				for (SearchCell cell : pattern.getCells()) {
					
					if(getCellAt(x + cell.x, y + cell.y) == null)
					{
						if(x + cell.x < 10 && y + cell.y < 20 && x + cell.x > -1)
						{
							if(!cell.isEmpty)
								patternOkFlag = false;
						}else{
							if(cell.isEmpty)
								patternOkFlag = false;
						}
					}else{
						if(cell.isEmpty)
							patternOkFlag = false;
					}
					
				}if(patternOkFlag)
				{
					points.add(new Point(x, y));
				}
			}
		}
		return points;
	}
}
