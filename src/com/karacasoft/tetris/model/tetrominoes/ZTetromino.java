package com.karacasoft.tetris.model.tetrominoes;

import java.awt.Color;

import com.karacasoft.tetris.model.AssetManager;
import com.karacasoft.tetris.model.Cell;
import com.karacasoft.tetris.model.CellState;
import com.karacasoft.tetris.model.Tetromino;

public class ZTetromino extends Tetromino {

	public static ZTetromino getNewInstance(AssetManager assets)
	{
		ZTetromino t = new ZTetromino();
		
		t.setCellState(createCellState(assets));
		
		t.setPositionX(3);
		t.setPositionY(-2);
		
		return t;
	}
	
	public static CellState createCellState(AssetManager assets)
	{
		CellState state1 = new CellState();
		state1.getCells().add(new Cell(1, 1, assets.getImage("red"), Color.RED));
		state1.getCells().add(new Cell(2, 1, assets.getImage("red"), Color.RED));
		state1.getCells().add(new Cell(2, 2, assets.getImage("red"), Color.RED));
		state1.getCells().add(new Cell(3, 2, assets.getImage("red"), Color.RED));
		
		state1.setTopLeftCellX(1);
		state1.setTopLeftCellY(1);
		
		CellState state2 = new CellState();
		state2.getCells().add(new Cell(3, 0, assets.getImage("red"), Color.RED));
		state2.getCells().add(new Cell(3, 1, assets.getImage("red"), Color.RED));
		state2.getCells().add(new Cell(2, 1, assets.getImage("red"), Color.RED));
		state2.getCells().add(new Cell(2, 2, assets.getImage("red"), Color.RED));
		
		state2.setTopLeftCellX(3);
		state2.setTopLeftCellY(0);
		
		state1.setNextState(state2);
		state2.setNextState(state1);
		
		state1.setPreviousState(state2);
		state2.setPreviousState(state1);
		
//		double random = Math.random();
//		if(random < 0.5)
//		{
//			return state1;
//		}else
//		{
//			return state2;
//		}
		
		return state1;
	}
	
}
