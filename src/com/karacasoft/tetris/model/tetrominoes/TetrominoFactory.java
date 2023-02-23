package com.karacasoft.tetris.model.tetrominoes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import com.karacasoft.tetris.model.AssetManager;
import com.karacasoft.tetris.model.Tetromino;


public class TetrominoFactory {
	
	private LinkedList<Tetromino> tetriminoQueue = new LinkedList<Tetromino>();
	private static ArrayList<Integer> lastRolls = new ArrayList<Integer>();
	
	public Tetromino next()
	{
		while(tetriminoQueue.size() < 7)
		{
			Tetromino[] bag = generateNewBag();
			for(Tetromino t : bag) {
				addNewTetrimino(t);
			}
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
	
	private static Tetromino getPieceForNumber(int n) {
		switch (n) {
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

	private static void shuffleArray(int[] array)
{
		int index;
		Random random = new Random();
		for (int i = array.length - 1; i > 0; i--)
		{
			index = random.nextInt(i + 1);
			if (index != i)
			{
				array[index] ^= array[i];
				array[i] ^= array[index];
				array[index] ^= array[i];
			}
		}
	}

	public static Tetromino[] generateNewBag()
	{
		Tetromino[] tetrominos = new Tetromino[7];

		int[] pieces = { 0, 1, 2, 3, 4, 5, 6, };
		shuffleArray(pieces);

		for (int i = 0; i < pieces.length; i++) {
			tetrominos[i] = getPieceForNumber(pieces[i]);
		}

		return tetrominos;
	}
}
