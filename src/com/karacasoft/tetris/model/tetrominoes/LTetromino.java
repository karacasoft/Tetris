package com.karacasoft.tetris.model.tetrominoes;

import java.awt.Color;

import com.karacasoft.tetris.model.AssetManager;
import com.karacasoft.tetris.model.Cell;
import com.karacasoft.tetris.model.CellState;
import com.karacasoft.tetris.model.Tetromino;

public class LTetromino extends Tetromino{

	public static LTetromino getNewInstance(AssetManager assets)
	{
		LTetromino t = new LTetromino();
		
		t.setCellState(createCellState(assets));
		
		t.setPositionX(3);
		t.setPositionY(-2);
		
		
		return t;
	}
	
	public static CellState createCellState(AssetManager assets)
	{
		CellState state1 = new CellState();
		state1.getCells().add(new Cell(1, 1, assets.getImage("blue"), Color.BLUE));
		state1.getCells().add(new Cell(2, 1, assets.getImage("blue"), Color.BLUE));
		state1.getCells().add(new Cell(3, 1, assets.getImage("blue"), Color.BLUE));
		state1.getCells().add(new Cell(1, 2, assets.getImage("blue"), Color.BLUE));
		
		state1.setTopLeftCellX(1);
		state1.setTopLeftCellY(1);
		
		CellState state2 = new CellState();
		state2.getCells().add(new Cell(1, 1, assets.getImage("blue"), Color.BLUE));
		state2.getCells().add(new Cell(2, 1, assets.getImage("blue"), Color.BLUE));
		state2.getCells().add(new Cell(2, 2, assets.getImage("blue"), Color.BLUE));
		state2.getCells().add(new Cell(2, 3, assets.getImage("blue"), Color.BLUE));
		
		state2.setTopLeftCellX(1);
		state2.setTopLeftCellY(1);
		
		CellState state3 = new CellState();
		state3.getCells().add(new Cell(2, 1, assets.getImage("blue"), Color.BLUE));
		state3.getCells().add(new Cell(2, 2, assets.getImage("blue"), Color.BLUE));
		state3.getCells().add(new Cell(1, 2, assets.getImage("blue"), Color.BLUE));
		state3.getCells().add(new Cell(0, 2, assets.getImage("blue"), Color.BLUE));
		
		state3.setTopLeftCellX(2);
		state3.setTopLeftCellY(1);
		
		CellState state4 = new CellState();
		state4.getCells().add(new Cell(2, 0, assets.getImage("blue"), Color.BLUE));
		state4.getCells().add(new Cell(2, 1, assets.getImage("blue"), Color.BLUE));
		state4.getCells().add(new Cell(2, 2, assets.getImage("blue"), Color.BLUE));
		state4.getCells().add(new Cell(3, 2, assets.getImage("blue"), Color.BLUE));
		
		state4.setTopLeftCellX(2);
		state4.setTopLeftCellY(0);
		
		state1.setNextState(state2);
		state2.setNextState(state3);
		state3.setNextState(state4);
		state4.setNextState(state1);
		
		state1.setPreviousState(state4);
		state2.setPreviousState(state1);
		state3.setPreviousState(state2);
		state4.setPreviousState(state3);
		
//		double random = Math.random();
//		if(random < 0.25)
//		{
//			return state1;
//		}else if(random < 0.5)
//		{
//			return state2;
//		}else if(random < 0.75)
//		{
//			return state3;
//		}else
//		{
//			return state4;
//		}
		return state1;
	}
	
}
