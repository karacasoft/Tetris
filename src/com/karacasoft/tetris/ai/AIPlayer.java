package com.karacasoft.tetris.ai;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Set;

import com.karacasoft.tetris.Game;
import com.karacasoft.tetris.GameController.Key;
import com.karacasoft.tetris.ai.InputCondition.Type;
import com.karacasoft.tetris.model.CellPattern;
import com.karacasoft.tetris.model.CellPattern.SearchCell;
import com.karacasoft.tetris.model.CellState;
import com.karacasoft.tetris.model.Tetromino;
import com.karacasoft.tetris.model.tetrominoes.ITetromino;
import com.karacasoft.tetris.model.tetrominoes.JTetromino;
import com.karacasoft.tetris.model.tetrominoes.LTetromino;
import com.karacasoft.tetris.model.tetrominoes.OTetromino;
import com.karacasoft.tetris.model.tetrominoes.STetromino;
import com.karacasoft.tetris.model.tetrominoes.TTetromino;
import com.karacasoft.tetris.model.tetrominoes.ZTetromino;
import com.karacasoft.tetris.view.GameView;
import com.karacasoft.tetris.view.GameView.OnDrawListener;
/**
 * The AI Player for this game. Giving the AIPlayer a game will be 
 * enough for it to be used. 
 * 
 * The AI can be blocked if the DEBUG flag is true. The block activator
 *  button is J on the keyboard.
 * 
 * @author Triforce
 *
 */
public class AIPlayer implements OnDrawListener {

	/**
	 * To give AI more realistic player feeling, AI waits for [aiWaitingTime]
	 * millisecondsS
	 */
	private int aiWaitingTime = 100;
	
	/**
	 * Main AI cycle thread.
	 * 
	 * A cycle will,
	 * - search for possible placements of the block,
	 * - find the best move among these possible placements and
	 * - enter the input for it to the game.
	 * 
	 */
	private Thread aiThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			while(aiworking)
			{
				try {
					while(aiblocked)
					{
						Thread.sleep(aiWaitingTime);
					}
					Tetromino current = game.getGameView().getCurrentTetromino();
					InputSchema schema = getBestMove();
					if(schema == null)
					{
						continue;
					}
					while(current == game.getGameView().getCurrentTetromino())
					{
						if(schema != null)
						{
							schema.execute();
							schema = schema.getNext();
						}
						Thread.sleep(aiWaitingTime);
					}
					Thread.sleep(aiWaitingTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	});
	private boolean aiblocked = false; //for debug
	private boolean aiworking = true;
	
	/**
	 * DEBUG flag for the AI. 
	 */
	public static final boolean DEBUG = true;
	/**
	 * Context reference
	 */
	private Game game;
	
	public AIPlayer(Game game) {
		this.game = game;
		game.setAi(this);
		game.getGameView().setOnDrawListener(this);
	}
	
	/**
	 * Returns the best move possible for current {@link Tetromino}.
	 * @return
	 */
	public InputSchema getBestMove()
	{
		HashMap<Point, CellPattern> map = null;
		if(game.getGameView().getCurrentTetromino() instanceof ITetromino)
		{
			map = searchIPatterns();
		}else if(game.getGameView().getCurrentTetromino() instanceof JTetromino)
		{
			map = searchJPatterns();
		}else if(game.getGameView().getCurrentTetromino() instanceof LTetromino)
		{
			map = searchLPatterns();
		}else if(game.getGameView().getCurrentTetromino() instanceof OTetromino)
		{
			map = searchOPatterns();
		}else if(game.getGameView().getCurrentTetromino() instanceof STetromino)
		{
			map = searchSPatterns();
		}else if(game.getGameView().getCurrentTetromino() instanceof TTetromino)
		{
			map = searchTPatterns();
		}else if(game.getGameView().getCurrentTetromino() instanceof ZTetromino)
		{
			map = searchZPatterns();
		}else{
			return null;
		}
		
		map = patternPreElimination(map);
		
		
		
		HashMap<Point, CellPattern> chosenPatterns = new HashMap<Point, CellPattern>();
		Set<Point> keySet = map.keySet();
		Point[] points = new Point[keySet.size()];
		keySet.toArray(points);
		int maxPrefRate = 0;
		int indexOfPattern = 0;
		
		for (Point p : map.keySet()) {
			CellPattern pattern = map.get(p);
			if(pattern.getPreferRate() + p.y * 2 > maxPrefRate)
			{
				chosenPatterns.clear();
				chosenPatterns.put(points[indexOfPattern], pattern);
				maxPrefRate = pattern.getPreferRate() + p.y * 2;
				
			}else if(pattern.getPreferRate() == maxPrefRate)
			{
				chosenPatterns.put(points[indexOfPattern], pattern);
			}
			indexOfPattern++;
		}
		System.out.println("Chosen patterns with pref rate: " + maxPrefRate);
		
		if(!chosenPatterns.isEmpty())
		{
			Point max = null;
			int maxY = 0;
			for(Point p : chosenPatterns.keySet())
			{
				if(maxY < p.y)
				{
					max = p;
					maxY = p.y;
				}
			}
			return getInputSchema(max, map.get(max));
		}else{
			if(game.isHoldUsed())
			{
				if(Math.random() > 0.5)
				{
					return dropLeftMost();
				}else{
					return dropRightMost();
				}
			}else{
				return new InputSchema(this.game, Key.HOLD);
			}
		}
	}
	/**
	 * Adds up an input to drop a piece to the left of the screen.
	 * @return InputSchema for the wanted action
	 */
	public InputSchema dropLeftMost()
	{
		InputSchema input = new InputSchema(game, Key.LEFT);
		input.addInput(Key.LEFT)
			.addInput(Key.LEFT)
			.addInput(Key.LEFT)
			.addInput(Key.LEFT)
			.addInput(Key.INSTANT_DROP);
		return input;
	}
	/**
	 * Adds up an input to drop a piece to the left of the screen
	 * @return InputSchema for the wanted action
	 */
	public InputSchema dropRightMost()
	{
		InputSchema input = new InputSchema(game, Key.RIGHT);
		input.addInput(Key.RIGHT)
			.addInput(Key.RIGHT)
			.addInput(Key.RIGHT)
			.addInput(Key.RIGHT)
			.addInput(Key.INSTANT_DROP);
		return input;
	}
	
	/**
	 * Eliminates the patterns which could not be possible to place blocks on.
	 * @param patterns Given patterns
	 * @return a map of eliminated patterns.
	 */
	public HashMap<Point, CellPattern> patternPreElimination(HashMap<Point, CellPattern> patterns)
	{
		HashMap<Point, CellPattern> returnPatterns = new HashMap<Point, CellPattern>();
		
		for (Point p : patterns.keySet()) {
			CellPattern pattern = patterns.get(p);
			if(pattern.isMustHaveEmptyTop())
			{
				boolean patternNotOk = false;
				for (SearchCell cell : pattern.getCells()) {
					if(cell.isEmpty)
					{
						for(int y = p.y + cell.y - 1; y >= 0; y--)
						{
							if(game.getGameView().getTileMap().getCellAt(p.x + cell.x, y) != null)
							{
								patternNotOk = true;
							}
						}
						if(patternNotOk)
						{
							break;
						}
					}
				}
				if(!patternNotOk)
				{
					returnPatterns.put(p, pattern);
				}
			}else{
				returnPatterns.put(p, pattern);
			}
		}
		
		return returnPatterns;
	}
	
	/**
	 * Returns an input schema for given pattern and point of it.
	 * @param position A Point position of the schema
	 * @param pattern Pattern for the input
	 * @return {@link InputSchema} to place the block in pattern.
	 */
	public InputSchema getInputSchema(Point position, CellPattern pattern)
	{
		Tetromino currentTetromino = game.getGameView().getCurrentTetromino();
		
		CellState state = currentTetromino.getCellState();
		
		InputSchema input = new InputSchema(this.game);
		InputSchema added = input;
		
		if(pattern.getRotationNeeded() > 0)
		{
			for (int i = 0; i < pattern.getRotationNeeded(); i++) {
				added = added.addInput(Key.ROTATE_RIGHT);
				state = state.getNextState();
			}
		}else if(pattern.getRotationNeeded() < 0){
			for (int i = 0; i > pattern.getRotationNeeded(); i--) {
				added = added.addInput(Key.ROTATE_LEFT);
				state = state.getPreviousState();
			}
		}

		
		position.x += pattern.getBlockPlacePositionX() - state.getTopLeftCellX();
		position.y += pattern.getBlockPlacePositionY() - state.getTopLeftCellY();
		
		int x = currentTetromino.getPositionX();
//		int y = currentTetromino.getPositionY();
		
		while(x != position.x)
		{
			if(x < position.x)
			{
				added = added.addInput(Key.RIGHT);
				x++;
			}else{
				added = added.addInput(Key.LEFT);
				x--;
			}
		}
		
		if(pattern.getDropDownInput() != null)
		{
			added.setNext(pattern.getDropDownInput());
		}else{
			added = added.addInput(Key.INSTANT_DROP);
		}
		
		return input;
	}
	
	/**
	 * Searches patterns for I Tetrominoes.
	 * @return A map of found patterns.
	 */
	public HashMap<Point, CellPattern> searchIPatterns()
	{
		HashMap<Point, CellPattern> patternList = new HashMap<Point, CellPattern>();
		
		
		CellPattern patternToSearch = new CellPattern();
		/*
		 * |x----x|
		 * | xxxx |
		 * |______|
		 * PR: 50
		 */
		patternToSearch.addCell(0, 0, false)
			.addCell(1, 0, true)
			.addCell(2, 0, true)
			.addCell(3, 0, true)
			.addCell(4, 0, true)
			.addCell(5, 0, false)
			.addCell(1, 1, false)
			.addCell(2, 1, false)
			.addCell(3, 1, false)
			.addCell(4, 1, false);
		patternToSearch.setPreferRate(50);
		patternToSearch.setBlockPlacePositionX(1);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(1);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		patternToSearch = new CellPattern();
		/*
		 * |-|
		 * |-|
		 * |-|
		 * |-|
		 * |x|
		 * |_|
		 * PR: 60
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(0, 1, true)
			.addCell(0, 2, true)
			.addCell(0, 3, true)
			.addCell(0, 4, false);
		patternToSearch.setPreferRate(60);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(0);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		return patternList;
	}
	/**
	 * Searches patterns for J Tetrominoes.
	 * @return A map of found patterns.
	 */
	public HashMap<Point, CellPattern> searchJPatterns()
	{
		HashMap<Point, CellPattern> patternList = new HashMap<Point, CellPattern>();
		
		CellPattern patternToSearch = new CellPattern();
		/*
		 * | -|
		 * | -|
		 * |--|
		 * |xx|
		 * |__|
		 * PR: 40
		 */
		patternToSearch.addCell(1, 0, true)
			.addCell(1, 1, true)
			.addCell(0, 2, true)
			.addCell(1, 2, true)
			.addCell(0, 3, false)
			.addCell(1, 3, false);
		patternToSearch.setPreferRate(40);
		patternToSearch.setBlockPlacePositionX(1);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(1);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		patternToSearch = new CellPattern();
		/*
		 * |-  |
		 * |---|
		 * |xxx|
		 * |___|
		 * PR: 55
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(0, 1, true)
			.addCell(1, 1, true)
			.addCell(2, 1, true)
			.addCell(0, 2, false)
			.addCell(1, 2, false)
			.addCell(2, 2, false);
		patternToSearch.setPreferRate(55);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(2);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		patternToSearch = new CellPattern();
		/*
		 * |--|
		 * |-x|
		 * |-x|
		 * |x |
		 * |__|
		 * PR: 80
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(1, 0, true)
			.addCell(0, 1, true)
			.addCell(1, 1, false)
			.addCell(0, 2, true)
			.addCell(1, 2, false)
			.addCell(0, 3, false);
		patternToSearch.setPreferRate(80);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(-1);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
	
		patternToSearch = new CellPattern();
		/*
		 * |---|
		 * |xx-|
		 * |  x|
		 * |___|
		 * PR: 70
		 * 
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(1, 0, true)
			.addCell(2, 0, true)
			.addCell(0, 1, false)
			.addCell(1, 1, false)
			.addCell(2, 1, true)
			.addCell(2, 2, false);
		patternToSearch.setPreferRate(70);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(0);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		patternToSearch = new CellPattern();
		/*
		 * |--|
		 * |-x|
		 * |-x|
		 * |__|
		 * PR: 10
		 * 
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(1, 0, true)
			.addCell(0, 1, true)
			.addCell(1, 1, false)
			.addCell(0, 2, true)
			.addCell(1, 2, false);
		patternToSearch.setPreferRate(10);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(-1);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		return patternList;	
	}
	/**
	 * Searches patterns for L Tetrominoes.
	 * @return A map of found patterns.
	 */
	public HashMap<Point, CellPattern> searchLPatterns()
	{
		HashMap<Point, CellPattern> patternList = new HashMap<Point, CellPattern>();
		
		CellPattern patternToSearch = new CellPattern();
		/*
		 * |- |
		 * |- |
		 * |--|
		 * |xx|
		 * |__|
		 * PR: 40
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(0, 1, true)
			.addCell(0, 2, true)
			.addCell(1, 2, true)
			.addCell(0, 3, false)
			.addCell(1, 3, false);
		patternToSearch.setPreferRate(40);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(-1);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		patternToSearch = new CellPattern();
		/*
		 * |  -|
		 * |---|
		 * |xxx|
		 * |___|
		 * PR: 55
		 */
		patternToSearch.addCell(2, 0, true)
			.addCell(0, 1, true)
			.addCell(1, 1, true)
			.addCell(2, 1, true)
			.addCell(0, 2, false)
			.addCell(1, 2, false)
			.addCell(2, 2, false);
		patternToSearch.setPreferRate(55);
		patternToSearch.setBlockPlacePositionX(2);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(-2);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		patternToSearch = new CellPattern();
		/*
		 * |--|
		 * |x-|
		 * |x-|
		 * | x|
		 * |__|
		 * PR: 80
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(1, 0, true)
			.addCell(0, 1, false)
			.addCell(1, 1, true)
			.addCell(0, 2, false)
			.addCell(1, 2, true)
			.addCell(1, 3, false);
		patternToSearch.setPreferRate(80);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(1);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
	
		patternToSearch = new CellPattern();
		/*
		 * |---|
		 * |-xx|
		 * |x  |
		 * |___|
		 * PR: 80
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(1, 0, true)
			.addCell(2, 0, true)
			.addCell(0, 1, true)
			.addCell(1, 1, false)
			.addCell(2, 1, false)
			.addCell(0, 2, false);
		patternToSearch.setPreferRate(0);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(0);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		
		
		patternToSearch = new CellPattern();
		/*
		 * |--|
		 * |x-|
		 * |x-|
		 * |__|
		 * PR: 10
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(1, 0, true)
			.addCell(0, 1, false)
			.addCell(1, 1, true)
			.addCell(0, 2, false)
			.addCell(1, 2, true);
		patternToSearch.setPreferRate(10);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(1);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		return patternList;	
	}
	/**
	 * Searches patterns for O Tetrominoes.
	 * @return A map of found patterns.
	 */
	public HashMap<Point, CellPattern> searchOPatterns()
	{
		HashMap<Point, CellPattern> patternList = new HashMap<Point, CellPattern>();
		
		CellPattern patternToSearch = new CellPattern();
		/*
		 * |--|
		 * |--|
		 * |xx|
		 * |__|
		 * PR: 50
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(1, 0, true)
			.addCell(0, 1, true)
			.addCell(1, 1, true)
			.addCell(0, 2, false)
			.addCell(1, 2, false);
		patternToSearch.setPreferRate(50);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(0);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		
		patternToSearch = new CellPattern();
		/*
		 * |--|
		 * |--|
		 * |x-|
		 * | x|
		 * |__|
		 * PR:20
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(1, 0, true)
			.addCell(0, 1, true)
			.addCell(1, 1, true)
			.addCell(0, 2, false)
			.addCell(1, 2, true)
			.addCell(1, 3, false);
		patternToSearch.setPreferRate(20);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(0);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		patternToSearch = new CellPattern();
		/*
		 * |--|
		 * |--|
		 * |-x|
		 * |x |
		 * |__|
		 * PR:20
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(1, 0, true)
			.addCell(0, 1, true)
			.addCell(1, 1, true)
			.addCell(1, 2, false)
			.addCell(0, 2, true)
			.addCell(0, 3, false);
		patternToSearch.setPreferRate(20);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(0);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		
		return patternList;	
	}
	/**
	 * Searches patterns for S Tetrominoes.
	 * @return A map of found patterns.
	 */
	public HashMap<Point, CellPattern> searchSPatterns()
	{
		HashMap<Point, CellPattern> patternList = new HashMap<Point, CellPattern>();
		
		CellPattern patternToSearch = new CellPattern();
		/*
		 * | --|
		 * |--x|
		 * |xx |
		 * |___|
		 * PR: 50
		 */
		patternToSearch.addCell(1, 0, true)
			.addCell(2, 0, true)
			.addCell(0, 1, true)
			.addCell(1, 1, true)
			.addCell(2, 1, false)
			.addCell(0, 2, false)
			.addCell(1, 2, false);
		patternToSearch.setPreferRate(50);
		patternToSearch.setBlockPlacePositionX(1);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(0);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		patternToSearch = new CellPattern();
		/*
		 * |- |
		 * |--|
		 * |x-|
		 * | x|
		 * |__|
		 * PR: 50
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(0, 1, true)
			.addCell(1, 1, true)
			.addCell(1, 2, true)
			.addCell(0, 2, false)
			.addCell(1, 3, false);
		patternToSearch.setPreferRate(50);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(1);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
			
		return patternList;	
	}
	/**
	 * Searches patterns for T Tetrominoes.
	 * @return A map of found patterns.
	 */
	public HashMap<Point, CellPattern> searchTPatterns()
	{
		HashMap<Point, CellPattern> patternList = new HashMap<Point, CellPattern>();
		
		CellPattern patternToSearch = new CellPattern();
		/*
		 * |---|
		 * |x-x|
		 * | x |
		 * |___|
		 * PR: 60
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(1, 0, true)
			.addCell(2, 0, true)
			.addCell(0, 1, false)
			.addCell(1, 1, true)
			.addCell(2, 1, false)
			.addCell(1, 2, false);
		patternToSearch.setPreferRate(60);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(0);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		patternToSearch = new CellPattern();
		/*
		 * |- |
		 * |--|
		 * |-x|
		 * |x |
		 * |__|
		 * PR: 50
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(0, 1, true)
			.addCell(1, 1, true)
			.addCell(0, 2, true)
			.addCell(1, 2, false)
			.addCell(0, 3, false);
		patternToSearch.setPreferRate(50);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(-1);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		patternToSearch = new CellPattern();
		/*
		 * | - |
		 * |---|
		 * |xxx|
		 * |___|
		 * PR: 50
		 */
		patternToSearch.addCell(1, 0, true)
			.addCell(0, 1, true)
			.addCell(1, 1, true)
			.addCell(2, 1, true)
			.addCell(0, 2, false)
			.addCell(1, 2, false)
			.addCell(2, 2, false);
		patternToSearch.setPreferRate(50);
		patternToSearch.setBlockPlacePositionX(1);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(2);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
	
		patternToSearch = new CellPattern();
		/*
		 * | -|
		 * |--|
		 * |x-|
		 * | x|
		 * |__|
		 * PR: 50
		 */
		patternToSearch.addCell(1, 0, true)
			.addCell(0, 1, true)
			.addCell(1, 1, true)
			.addCell(0, 2, false)
			.addCell(1, 2, true)
			.addCell(1, 3, false);
		patternToSearch.setPreferRate(50);
		patternToSearch.setBlockPlacePositionX(1);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(1);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		//Now it's time for some advanced shit to teach our AI
		
		patternToSearch = new CellPattern();
		/*
		 * | x---|
		 * |x----|
		 * | xxx |
		 * |_____|
		 * PR: 70
		 */
		patternToSearch.addCell(1, 0, false)
			.addCell(2, 0, true)
			.addCell(3, 0, true)
			.addCell(4, 0, true)
			.addCell(0, 1, false)
			.addCell(1, 1, true)
			.addCell(2, 1, true)
			.addCell(3, 1, true)
			.addCell(4, 1, true)
			.addCell(1, 2, false)
			.addCell(2, 2, false)
			.addCell(3, 2, false);
		patternToSearch.setPreferRate(70);
		patternToSearch.setBlockPlacePositionX(3);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(2);
		patternToSearch.setMustHaveEmptyTop(false);
		
		InputSchema dropInput = new InputSchema(game, Key.DROP);
		InputCondition condition = new InputCondition()
			.setType(Type.REPEAT_BEFORE_PLACEMENT);
		dropInput.setCondition(condition);
		
		condition = new InputCondition().setType(Type.BEFORE_PLACE);
		
		dropInput.addInput(Key.LEFT).setCondition(condition);
			
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		
		
		
		patternToSearch = new CellPattern();
		/*
		 * |---x |
		 * |----x|
		 * | xxx |
		 * |_____|
		 * PR: 70
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(1, 0, true)
			.addCell(2, 0, true)
			.addCell(3, 0, false)
			.addCell(0, 1, true)
			.addCell(1, 1, true)
			.addCell(2, 1, true)
			.addCell(3, 1, true)
			.addCell(4, 1, false)
			.addCell(1, 2, false)
			.addCell(2, 2, false)
			.addCell(3, 2, false);
		patternToSearch.setPreferRate(70);
		patternToSearch.setBlockPlacePositionX(2);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(2);
		patternToSearch.setMustHaveEmptyTop(false);
		
		dropInput = new InputSchema(game, Key.DROP);
		condition = new InputCondition()
			.setType(Type.REPEAT_BEFORE_PLACEMENT);
		dropInput.setCondition(condition);
		
		condition = new InputCondition().setType(Type.BEFORE_PLACE);
		
		dropInput.addInput(Key.RIGHT).setCondition(condition);
			
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		//THERE ARE T-SPINS OVER THERE!!!
		
		patternToSearch = new CellPattern();
		/*
		 * |--x |
		 * |---x|
		 * |x-x |
		 * | x  |
		 * |____|
		 * PR: 90
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(1, 0, true)
			.addCell(2, 0, false)
			.addCell(0, 1, true)
			.addCell(1, 1, true)
			.addCell(2, 1, true)
			.addCell(3, 1, false)
			.addCell(0, 2, false)
			.addCell(1, 2, true)
			.addCell(2, 2, false)
			.addCell(1, 3, false);
		patternToSearch.setPreferRate(90);
		patternToSearch.setBlockPlacePositionX(1);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(1);
		patternToSearch.setMustHaveEmptyTop(false);
		
		
		dropInput = new InputSchema(game, Key.DROP);
		condition = new InputCondition()
			.setType(Type.REPEAT_BEFORE_PLACEMENT);
		dropInput.setCondition(condition);
		
		condition = new InputCondition().setType(Type.BEFORE_PLACE);
		
		dropInput.addInput(Key.ROTATE_LEFT).setCondition(condition);
		
		
		patternToSearch.setDropDownInput(dropInput);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		
		
		patternToSearch = new CellPattern();
		/*
		 * | x--|
		 * |x---|
		 * | x-x|
		 * |  x |
		 * |____|
		 * PR: 90
		 */
		patternToSearch.addCell(1, 0, false)
			.addCell(2, 0, true)
			.addCell(3, 0, true)
			.addCell(0, 1, false)
			.addCell(1, 1, true)
			.addCell(2, 1, true)
			.addCell(3, 1, true)
			.addCell(1, 2, false)
			.addCell(2, 2, true)
			.addCell(3, 2, false)
			.addCell(2, 3, false);
		patternToSearch.setPreferRate(90);
		patternToSearch.setBlockPlacePositionX(2);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(-1);
		patternToSearch.setMustHaveEmptyTop(false);
		
		dropInput = new InputSchema(game, Key.DROP);
		condition = new InputCondition()
			.setType(Type.REPEAT_BEFORE_PLACEMENT);
		dropInput.setCondition(condition);
		
		condition = new InputCondition().setType(Type.BEFORE_PLACE);
		dropInput.addInput(Key.ROTATE_RIGHT).setCondition(condition);
		
		patternToSearch.setDropDownInput(dropInput);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		return patternList;	
	}
	/**
	 * Searches patterns for Z Tetrominoes.
	 * @return A map of found patterns.
	 */
	public HashMap<Point, CellPattern> searchZPatterns()
	{
		HashMap<Point, CellPattern> patternList = new HashMap<Point, CellPattern>();
		
		CellPattern patternToSearch = new CellPattern();
		/*
		 * |-- |
		 * |x--|
		 * | xx|
		 * |___|
		 * PR: 50
		 */
		patternToSearch.addCell(0, 0, true)
			.addCell(1, 0, true)
			.addCell(0, 1, false)
			.addCell(1, 1, true)
			.addCell(2, 1, true)
			.addCell(1, 2, false)
			.addCell(2, 2, false);
		patternToSearch.setPreferRate(50);
		patternToSearch.setBlockPlacePositionX(0);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(0);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
		
		patternToSearch = new CellPattern();
		/*
		 * | -|
		 * |--|
		 * |-x|
		 * |x |
		 * |__|
		 * PR: 50
		 */
		patternToSearch.addCell(1, 0, true)
			.addCell(0, 1, true)
			.addCell(1, 1, true)
			.addCell(0, 2, true)
			.addCell(1, 2, false)
			.addCell(0, 3, false);
		patternToSearch.setPreferRate(50);
		patternToSearch.setBlockPlacePositionX(1);
		patternToSearch.setBlockPlacePositionY(0);
		patternToSearch.setRotationNeeded(1);
		
		for (Point p : game.getGameView().getTileMap().searchPattern(patternToSearch)) {
			patternList.put(p, patternToSearch);
		}
			
		return patternList;	
	}
	
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * Draw the debug graphics for the AI.
	 */
	@Override
	public void onDrawCalled(Graphics g) {
		if(DEBUG)
		{
			HashMap<Point, CellPattern> map = null;
			if(game.getGameView().getCurrentTetromino() instanceof ITetromino)
			{
				map = searchIPatterns();
			}else if(game.getGameView().getCurrentTetromino() instanceof JTetromino)
			{
				map = searchJPatterns();
			}else if(game.getGameView().getCurrentTetromino() instanceof LTetromino)
			{
				map = searchLPatterns();
			}else if(game.getGameView().getCurrentTetromino() instanceof OTetromino)
			{
				map = searchOPatterns();
			}else if(game.getGameView().getCurrentTetromino() instanceof STetromino)
			{
				map = searchSPatterns();
			}else if(game.getGameView().getCurrentTetromino() instanceof TTetromino)
			{
				map = searchTPatterns();
			}else if(game.getGameView().getCurrentTetromino() instanceof ZTetromino)
			{
				map = searchZPatterns();
			}
			
			for (int i = 0; i < map.size(); i++) {
				Point p = (Point) map.keySet().toArray()[i];
	//			p.x += game.getGameView().getCurrentTetromino().getCellState().getTopLeftCellX();
	//			p.y += game.getGameView().getCurrentTetromino().getCellState().getTopLeftCellY() - 1;
				g.setColor(new Color(200, 200, 200, 100));
	//			g.fillRect(GameView.GAME_MARGIN_LEFT + game.getGameView().getTileMap().getTileWidthInPixels() * p.x,
	//					GameView.GAME_MARGIN_TOP + game.getGameView().getTileMap().getTileHeightInPixels() * p.y,
	//					game.getGameView().getTileMap().getTileWidthInPixels(),
	//					game.getGameView().getTileMap().getTileHeightInPixels());
				CellPattern pattern = map.get(p);
				for(SearchCell cell : pattern.getCells())
				{
					if(cell.isEmpty)
					{
						g.fillRect(GameView.GAME_MARGIN_LEFT + game.getGameView().getTileMap().getTileWidthInPixels() * (p.x + cell.x),
								GameView.GAME_MARGIN_TOP + game.getGameView().getTileMap().getTileHeightInPixels() * (p.y + cell.y),
								game.getGameView().getTileMap().getTileWidthInPixels(),
								game.getGameView().getTileMap().getTileHeightInPixels());
					}
				}
	//			break;
			}
		}
	}
	
	public Thread getAiThread() {
		return aiThread;
	}

	public boolean isAiblocked() {
		return aiblocked;
	}

	public void setAiblocked(boolean aiblocked) {
		if(DEBUG)
		{
			this.aiblocked = aiblocked;
		}
	}
	
	
	
}
