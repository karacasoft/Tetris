package com.karacasoft.tetris;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.karacasoft.tetris.ai.AIPlayer;
import com.karacasoft.tetris.model.AssetManager;
import com.karacasoft.tetris.model.Cell;
import com.karacasoft.tetris.model.KeyMap;
import com.karacasoft.tetris.model.Tetromino;
import com.karacasoft.tetris.model.tetrominoes.TetrominoFactory;
import com.karacasoft.tetris.sound.SoundManager;
import com.karacasoft.tetris.view.GameFrame;
import com.karacasoft.tetris.view.GameView;
/**
 * Main game class for the game.
 * Every single object should be aware of this context and work with this class.
 * 
 * Different Game instances will open different game screens which allows us
 *  to build a multiplayer feature for this game. :)
 * 
 * @author triforce
 *
 */
public class Game {
	/**
	 * Defines different states that the game will have while running.
	 * 
	 * @author Karaca
	 *
	 */
	public enum State{
		NOT_STARTED,
		RUNNING,
		LINE_CLEARING,
		LANDING_A_PIECE,
		PAUSED,
		LOST,
		WIN
	}
	
	/**
	 * Defines the block landing time limit. The block will land
	 * 600 milliseconds after touching something.(Ground or another block)
	 * 
	 * Landing time will reset if player moves or rotates the block
	 */
	public static final long LANDING_TIME = 600;
	/**
	 * Defines the time for line clear animation. Input can be buffered while lines
	 * are being cleared. If player presses left button five times while the
	 * line clear animation continues, the block will move 5 cells left of its current 
	 * position after line clear animation finishes.
	 */
	public static final long LINE_CLEAR_TIME = 500;
	
	private long gravityTime = 750;
	private long gravityTimer = 0;
	private long landingTimer = 0;
	private long lineClearTimer = 0;
	private GameView gameView;
	private GameFrame gameFrame;
	private GameThread gameThread;
	private GameController gameController;
	private TetrominoFactory tetrominoFactory;
	
	private AIPlayer ai;
	
	private Game opponentGame = null;
	
	private Tetromino heldTetromino = null;
	private boolean holdUsed = false;
	
	private KeyMap keyMap = KeyMap.getPlayer1();
	
	private int level = 1;
	private int linesCleared = 9; //TODO changed for testing. Real starting value = 0
	
	private int score = 0;
	
	/**
	 * Destroyed lines will be taken to these queue and will be handled after they're
	 * counted.
	 */
	private ArrayList<Integer> destroyQueue = new ArrayList<Integer>();
	
	private State gameState = State.RUNNING;
	
	/**
	 * Empty constructor
	 */
	public Game() {
		gameFrame = new GameFrame();
		gameView = new GameView(this);
		gameThread = new GameThread(this);
		gameController = new GameController(this);
		gameFrame.addKeyListener(gameController);
		tetrominoFactory = new TetrominoFactory();
	}
	
	/**
	 * Loads assets into the memory and holds their reference in {@link AssetManager}
	 * class.
	 * @throws IOException
	 */
	public static void loadGameAssets() throws IOException
	{
		BufferedImage blue_block_image = ImageIO.read(new File("assets/blue_block.png"));
		AssetManager.getInstance().addImage("blue", blue_block_image);
		BufferedImage cyan_block_image = ImageIO.read(new File("assets/cyan_block.png"));
		AssetManager.getInstance().addImage("cyan", cyan_block_image);
		BufferedImage green_block_image = ImageIO.read(new File("assets/green_block.png"));
		AssetManager.getInstance().addImage("green", green_block_image);
		BufferedImage orange_block_image = ImageIO.read(new File("assets/orange_block.png"));
		AssetManager.getInstance().addImage("orange", orange_block_image);
		BufferedImage purple_block_image = ImageIO.read(new File("assets/purple_block.png"));
		AssetManager.getInstance().addImage("purple", purple_block_image);
		BufferedImage red_block_image = ImageIO.read(new File("assets/red_block.png"));
		AssetManager.getInstance().addImage("red", red_block_image);
		BufferedImage yellow_block_image = ImageIO.read(new File("assets/yellow_block.png"));
		AssetManager.getInstance().addImage("yellow", yellow_block_image);
		
		BufferedImage boundary_block = ImageIO.read(new File("assets/boundary.png"));
		AssetManager.getInstance().addImage("boundary", boundary_block);
		
		for (int i = 1; i < 12; i++) {
			BufferedImage block_dissapear = ImageIO.read(new File("assets/block_disappear" + i + ".png"));
			AssetManager.getInstance().addImage("block_disappear" + i, block_dissapear);
		}
		
		for (int i = 1; i < GameView.MAX_BG_NUMBER + 1; i++)
		{
			BufferedImage background = ImageIO.read(new File("assets/background/background" + i + ".png"));
			AssetManager.getInstance().addImage("background" + i, background);
		}
		
		SoundManager soundManager = SoundManager.getInstance();
		
		soundManager.addAudio("drop_piece", "drop_piece.wav");
		soundManager.addAudio("menu_move", "menu_move.wav");
		soundManager.addAudio("menu_select", "menu_select.wav");
		soundManager.addAudio("move_piece", "move_piece.wav");
		
	}
	
	/**
	 * Starts the game.
	 */
	public void start()
	{
		gameFrame.setContentPane(gameView);
		gameFrame.setVisible(true);
		
		gameThread.start();
		gameController.setInputEnabled(true);
	}
	
	/**
	 * Calls repaint for game view, updates the game and processes inputs.
	 * @param dTime defines the time passed while rendering this frame. Should be
	 * 				around 20 milliseconds.
	 */
	public void render(long dTime)
	{
		gameView.repaint();
		
		update(dTime);
		
		if(gameController.isInputEnabled())
		{
			gameController.processAllInputs();
		}
		
	}
	
	/**
	 * Updates the game variables according to the state of the game.
	 * @param dTime The time passed while rendering the last frame.
	 */
	public void update(long dTime)
	{
		switch (this.gameState) {
		case NOT_STARTED:
			
			break;
		case RUNNING:
			if(gameView.getCurrentTetromino() == null) gameView.setCurrentTetromino(tetrominoFactory.next());
			
			gravityTimer += dTime;
			if(gravityTimer > gravityTime)
			{
				applyGravity();
				gravityTimer -= gravityTime;
			}
			break;
		case LINE_CLEARING:
			lineClearTimer += dTime;
			
			for (Integer integer : destroyQueue) {
				ArrayList<Cell> rowCells = gameView.getTileMap().getRow(integer);
				for (Cell c : rowCells) {
					if(c.isDestroyed())
					{
						c.setDestroyAnimFrameTimer(c.getDestroyAnimFrameTimer() + dTime);
					}else{
						c.setDestroyed(true);
						break;
					}
				}
			}
			
			
			if(lineClearTimer > LINE_CLEAR_TIME)
			{
				for (Integer integer : destroyQueue) {
					gameView.getTileMap().dropRows(integer);
				}
				destroyQueue.clear();
				lineClearTimer = 0;
				gameState = State.RUNNING;
				gameController.setInputEnabled(true);
			}
			break;
		case LANDING_A_PIECE:
			landingTimer += dTime;
			if(landingTimer >= LANDING_TIME)
			{
				applyGravity();
			}
			break;
		case PAUSED:
			break;
		case LOST:
			break;
		default:
			break;
		}
	}
	
	/**
	 * Applies gravity once for the current piece. If the piece is landing on a block,
	 * it'll set the game mode to {@link State#LANDING_A_PIECE}. If landing time has passed.
	 * it finally will place the block.
	 */
	public void applyGravity()
	{
		int tPosX = gameView.getCurrentTetromino().getPositionX();
		int tPosY = gameView.getCurrentTetromino().getPositionY();
		
		boolean placeFlag = false;
		
		for (Cell c : gameView.getCurrentTetromino().getCellState().getCells()) {
			if(gameView.getTileMap().getCellAt(tPosX + c.getPositionX(), tPosY + c.getPositionY() + 1) != null
					||
			   tPosY + c.getPositionY() + 1 >= gameView.getTileMap().getHeightInTiles())
			{
				placeFlag = true;
				break;
			}
		}
		if(placeFlag)
		{
			gameState = State.LANDING_A_PIECE;
			if(landingTimer < LANDING_TIME)
			{
			}else{
				SoundManager.getInstance().playAudio("drop_piece");
				for (Cell c : gameView.getCurrentTetromino().getCellState().getCells()) {
					c.setPositionX(tPosX + c.getPositionX());
					c.setPositionY(tPosY + c.getPositionY());
					if(c.getPositionY() < 0)
					{
						this.setGameState(State.LOST);
						if(opponentGame != null)
							opponentGame.setGameState(State.WIN);
						return;
					}
					gameView.getTileMap().changeCellAt(c.getPositionX(), c.getPositionY(), c);
				}
				holdUsed = false;
				int destroyedRows = 0;
				for(int i = 0; i < 20; i++)
				{
					if(gameView.getTileMap().getRow(i).size() >= 10)
					{
						destroyQueue.add(i);
						destroyedRows++;
					}
				}
				if(destroyedRows > 0)
				{
					this.gameState = State.LINE_CLEARING;
					gameController.setInputEnabled(false);
					linesCleared += destroyedRows;
					if(linesCleared / 10 + 1> level)
					{
						level++;
						gravityTime -= 100;
						gameView.setLevelingUp(true);
					}
					switch (destroyedRows) {
					case 1:
						this.score += 100;
						break;
					case 2:
						this.score += 300;
						if(opponentGame != null)
						{
							opponentGame.getGameView().getTileMap().addTilesFromBottom(2);
						}
						break;
					case 3:
						this.score += 700;
						if(opponentGame != null)
						{
							opponentGame.getGameView().getTileMap().addTilesFromBottom(3);
						}
						break;
					case 4:
						this.score += 1500;
						if(opponentGame != null)
						{
							opponentGame.getGameView().getTileMap().addTilesFromBottom(4);
						}
						break;
					default:
						break;
					}
				}else{
					this.gameState = State.RUNNING;
				}
				
				gameView.setCurrentTetromino(tetrominoFactory.next());
				
				landingTimer = 0;
				
			}
		}else{
			gameView.getCurrentTetromino().applyGravity();
		}
		
	}
	
	public GameFrame getGameFrame() {
		return gameFrame;
	}
	
	public GameView getGameView() {
		return gameView;
	}
	
	public void setGravityTimer(long gravityTimer) {
		this.gravityTimer = gravityTimer;
	}
	
	public void setLandingTimer(long landingTimer) {
		this.landingTimer = landingTimer;
	}
	
	public State getGameState() {
		return gameState;
	}
	
	public void setGameState(State gameState) {
		this.gameState = gameState;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLinesCleared() {
		return linesCleared;
	}

	public void setLinesCleared(int linesCleared) {
		this.linesCleared = linesCleared;
	}

	public KeyMap getKeyMap() {
		return keyMap;
	}

	public void setKeyMap(KeyMap keyMap) {
		this.keyMap = keyMap;
	}

	public Game getOpponentGame() {
		return opponentGame;
	}

	public void setOpponentGame(Game opponentGame) {
		this.opponentGame = opponentGame;
	}

	public GameController getGameController() {
		return gameController;
	}

	public void setGameController(GameController gameController) {
		this.gameController = gameController;
	}

	public TetrominoFactory getTetrominoFactory() {
		return tetrominoFactory;
	}

	public void setTetrominoFactory(TetrominoFactory tetrominoFactory) {
		this.tetrominoFactory = tetrominoFactory;
	}

	public Tetromino getHeldTetromino() {
		return heldTetromino;
	}

	public void setHeldTetromino(Tetromino heldTetromino) {
		this.heldTetromino = heldTetromino;
	}

	public boolean isHoldUsed() {
		return holdUsed;
	}

	public void setHoldUsed(boolean holdUsed) {
		this.holdUsed = holdUsed;
	}

	public AIPlayer getAi() {
		return ai;
	}

	public void setAi(AIPlayer ai) {
		this.ai = ai;
	}
}
