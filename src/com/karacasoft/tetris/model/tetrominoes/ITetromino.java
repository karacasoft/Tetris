package com.karacasoft.tetris.model.tetrominoes;

import java.awt.Color;

import com.karacasoft.tetris.model.AssetManager;
import com.karacasoft.tetris.model.Cell;
import com.karacasoft.tetris.model.CellState;
import com.karacasoft.tetris.model.Tetromino;

public class ITetromino extends Tetromino{

	public static ITetromino getNewInstance(AssetManager assets)
	{
		ITetromino t = new ITetromino();
		
		//define cell states
		CellState state = createCellState(assets);
		t.setCellState(state);
		
		t.setPositionX(3);
		t.setPositionY(-2);
		
		return t;
	}
	
	private static CellState createCellState(AssetManager assets)
	{
		CellState state1 = new CellState();
		state1.getCells().add(new Cell(2, 0, assets.getImage("cyan"), Color.CYAN));
		state1.getCells().add(new Cell(2, 1, assets.getImage("cyan"), Color.CYAN));
		state1.getCells().add(new Cell(2, 2, assets.getImage("cyan"), Color.CYAN));
		state1.getCells().add(new Cell(2, 3, assets.getImage("cyan"), Color.CYAN));
		
		state1.setTopLeftCellX(2);
		state1.setTopLeftCellY(0);
		
		CellState state2 = new CellState();
		state2.getCells().add(new Cell(0, 1, assets.getImage("cyan"), Color.CYAN));
		state2.getCells().add(new Cell(1, 1, assets.getImage("cyan"), Color.CYAN));
		state2.getCells().add(new Cell(2, 1, assets.getImage("cyan"), Color.CYAN));
		state2.getCells().add(new Cell(3, 1, assets.getImage("cyan"), Color.CYAN));
		
		state2.setTopLeftCellX(0);
		state2.setTopLeftCellY(1);
		
		state1.setPreviousState(state2);
		state1.setNextState(state2);
		
		state2.setPreviousState(state1);
		state2.setNextState(state1);
		//Random states doesn't work that great. Also TGM2 doesn't use this kind of randomization.
//		if(Math.random() > 0.5)
//		{
//			return state1;
//		}else{
//			return state2;
//		}
		
		return state1;
	}
}
