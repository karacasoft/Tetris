package com.karacasoft.tetris.model.tetrominoes;

import java.awt.Color;

import com.karacasoft.tetris.model.AssetManager;
import com.karacasoft.tetris.model.Cell;
import com.karacasoft.tetris.model.CellState;
import com.karacasoft.tetris.model.Tetromino;

public class OTetromino extends Tetromino{
	public static OTetromino getNewInstance(AssetManager assets)
	{
		OTetromino t = new OTetromino();
		
		t.setCellState(createCellState(assets));
		
		t.setPositionX(3);
		t.setPositionY(-2);
		
		return t;
	}
	
	public static CellState createCellState(AssetManager assets)
	{
		CellState state1 = new CellState();
		state1.getCells().add(new Cell(1, 1, assets.getImage("yellow"), Color.YELLOW));
		state1.getCells().add(new Cell(1, 2, assets.getImage("yellow"), Color.YELLOW));
		state1.getCells().add(new Cell(2, 1, assets.getImage("yellow"), Color.YELLOW));
		state1.getCells().add(new Cell(2, 2, assets.getImage("yellow"), Color.YELLOW));

		state1.setTopLeftCellX(1);
		state1.setTopLeftCellY(1);
		
		state1.setNextState(state1);
		state1.setPreviousState(state1);
	
		return state1;
	}
}
