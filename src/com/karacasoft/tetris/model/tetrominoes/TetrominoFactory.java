package com.karacasoft.tetris.model.tetrominoes;

import java.util.LinkedList;
import java.util.Random;

import com.karacasoft.tetris.model.AssetManager;
import com.karacasoft.tetris.model.Tetromino;


public class TetrominoFactory {
	
	private LinkedList<Tetromino> tetriminoQueue = new LinkedList<Tetromino>();
	
	public Tetromino next()
	{
		while(tetriminoQueue.size() < 5)
		{
			addNewTetrimino(newRandomTetrimino());
		}
		return tetriminoQueue.poll();
	}
	
	public Tetromino peekNext()
	{
		return tetriminoQueue.peek();
	}
	
	public void addNewTetrimino(Tetromino t)
	{
		tetriminoQueue.add(t);
	}
	
	public static Tetromino newRandomTetrimino()
	{
		Random r = new Random();
		int rng = r.nextInt(7);
		
		switch (rng) {
		case 0:
			return ITetromino.getNewInstance(AssetManager.getInstance());
		case 1:
			return JTetromino.getNewInstance(AssetManager.getInstance());
		case 2:
			return LTetromino.getNewInstance(AssetManager.getInstance());
		case 3:
			return OTetromino.getNewInstance(AssetManager.getInstance());
		case 4:
			return STetromino.getNewInstance(AssetManager.getInstance());
		case 5:
			return TTetromino.getNewInstance(AssetManager.getInstance());
		case 6:
			return ZTetromino.getNewInstance(AssetManager.getInstance());
		default:
			System.err.println("WTF! This should not happen normally.");
		}
		return null;
	}
}
